package com.greenbean.poplar.sqlla.entity;

import com.greenbean.poplar.sqlla.ResultConverter;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by chrisding on 2017/5/13.
 * Function: 实体类的转换器工厂
 */
public class SqllaEntityConverterFactory implements ResultConverter.Factory {

    private Map<Class<?>, SqllaEntityConcept<?>> mEntityConcepts = new HashMap<>(0);
    private final Object mEntityConceptsWRLock = new Object();

    private Map<Type, ResultConverter<?>> mCached = new LinkedHashMap<>(0);

    @Override
    public ResultConverter<?> getConverter(Type returnType) {

        Map<Type, ResultConverter<?>> cachedConverters = this.mCached;
        if (cachedConverters.containsKey(returnType)) {
            return cachedConverters.get(returnType);
        }

        if (returnType instanceof Class<?>) {
            Class<?> rawType = (Class<?>) returnType;
            if (rawType.isAnnotationPresent(SqllaEntity.class)) {
                SingleSqllaEntityConverter<?> converter = new SingleSqllaEntityConverter<>(rawType);
                cachedConverters.put(returnType, converter);
                return converter;
            }
        } else if (returnType instanceof ParameterizedType) {
            ParameterizedType prType = (ParameterizedType) returnType;
            final Class<?> rawType = (Class<?>) prType.getRawType();
            Type[] typeArgs = prType.getActualTypeArguments();
            if (typeArgs.length == 1
                    && rawType == List.class
                    && typeArgs[0] instanceof Class<?>
                    && ((Class<?>) typeArgs[0]).isAnnotationPresent(SqllaEntity.class)) {

                ListSqllaEntityConverter<?> converter = new ListSqllaEntityConverter<>((Class<?>) typeArgs[0]);
                cachedConverters.put(returnType, converter);
                return converter;
            }
        }

        cachedConverters.put(returnType, null);
        return null;
    }

    <T> SqllaEntityConcept<T> getConcept(Class<T> returnRawType) {
        if (mEntityConcepts.containsKey(returnRawType)) {
            //noinspection unchecked
            return (SqllaEntityConcept<T>) mEntityConcepts.get(returnRawType);
        }

        synchronized (mEntityConceptsWRLock) {
            if (mEntityConcepts.containsKey(returnRawType)) {
                //noinspection unchecked
                return (SqllaEntityConcept<T>) mEntityConcepts.get(returnRawType);
            }
            SqllaEntityConcept<T> concept = new SqllaEntityConcept<>(returnRawType);
            mEntityConcepts.put(returnRawType, concept);
            mEntityConceptsWRLock.notify();
            concept.resolveColumnConcepts(this);
            return concept;
        }
    }

    private <T> T newEntityFromCursor(SqllaEntityConcept<T> concept, ResultConverter.Param param) throws SQLException {
        T obj = concept.newEntity();
        ResultSet resultSet = param.getResultSet();
        Connection conn = resultSet.getStatement().getConnection();
        for (SqllaEntityConcept.ColumnField field : concept) {
            Class<?> type = field.mField.getType();
            if (type == java.util.Date.class) {
                type = java.sql.Date.class;
            }
            Object value;
            if (field.mIsEntityType) {
                do {
                    try {
                        resultSet.findColumn(field.mColumn);
                    } catch (SQLException e) {
                        value = null;   // 没有这一列，置空
                        break;
                    }

                    value = resultSet.getObject(field.mColumn, field.mEntityConcept.mPrimaryKey.mField.getType());
                    Exclude exclude = param.getAnnotation(Exclude.class);
                    if ((exclude == null || Arrays.binarySearch(exclude.value(), "") == -1) && field.mIsInclude) {
                        // 是Include的同时不是Exclude, 查询出此对象
                        value = queryObject(conn, field, value);
                    } else {
                        // 填充一个之后ID数据的对象, 这个对象对应的表数据可能是不存在
                        Object emptyValJustId = field.mEntityConcept.newEntity();
                        field.mEntityConcept.mPrimaryKey.set(emptyValJustId, value);
                        value = emptyValJustId;
                    }
                } while (false);
            } else {
                try {
                    value = resultSet.getObject(field.mColumn, type);
                } catch (SQLException e) {
                    value = null;   // 没有这一列，置空
                }
            }
            if (value != null) {
                field.set(obj, value);
            }
        }
        return obj;
    }

    private Object queryObject(Connection conn, final SqllaEntityConcept.ColumnField field, Object value) throws SQLException {
        if (!field.mIsEntityType) {
            return value;
        }
        final String fieldQuerySql = field.mEntityConcept.mQuerySql;
        if (fieldQuerySql == null || fieldQuerySql.isEmpty()) {
            return value;
        }

        PreparedStatement ps = conn.prepareStatement(fieldQuerySql);
        ps.setObject(1, value);

        System.out.println("Sqlla: EntityConverter: fetch [" + field.mField.getType() + "] by execute sql "
                + fieldQuerySql.toUpperCase() + ", arg = " + String.valueOf(value));

        final ResultSet queryRet = ps.executeQuery();
        value = getConverter(field.mField.getGenericType()).convert(new ResultConverter.Param() {
            @Override
            public String getSql() {
                return fieldQuerySql;
            }

            @Override
            public Type getTargetType() {
                return field.mField.getGenericType();
            }

            @Override
            public ResultSet getResultSet() {
                return queryRet;
            }

            @Override
            public <T extends Annotation> T getAnnotation(Class<T> annoClass) {
                return null;
            }
        });
        return value;
    }

    private class SingleSqllaEntityConverter<T> implements ResultConverter<T> {

        private SqllaEntityConcept<T> mConcept;

        private SingleSqllaEntityConverter(Class<T> returnRawType) {
            mConcept = getConcept(returnRawType);
        }

        @Override
        public T convert(ResultConverter.Param param) throws SQLException {
            ResultSet resultSet = param.getResultSet();
            if (resultSet.next()) {
                return newEntityFromCursor(mConcept, param);
            }
            return null;
        }
    }

    private class ListSqllaEntityConverter<T> implements ResultConverter<List<T>> {

        private SqllaEntityConcept<T> mConcept;

        private ListSqllaEntityConverter(Class<T> componentRawType) {
            mConcept = getConcept(componentRawType);
        }

        @Override
        public List<T> convert(ResultConverter.Param param) throws SQLException {
            SqllaEntityConcept<T> concept = this.mConcept;
            List<T> list = new ArrayList<>();
            while (param.getResultSet().next()) {
                list.add(newEntityFromCursor(concept, param));
            }
            return list;
        }
    }
}
