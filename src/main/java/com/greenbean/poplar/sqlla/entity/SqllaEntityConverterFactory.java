package com.greenbean.poplar.sqlla.entity;

import com.greenbean.poplar.sqlla.ResultConverter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chrisding on 2017/5/13.
 * Function: 实体类的转换器工厂
 */
public class SqllaEntityConverterFactory implements ResultConverter.Factory {

    private Map<Class<?>, SqllaEntityConcept<?>> mEntityConcepts = new HashMap<>(0);

    private final Object mEntityConceptsWRLock = new Object();

    @Override
    public ResultConverter<?> getConverter(Type returnType) {

        if (returnType instanceof Class<?>) {
            Class<?> rawType = (Class<?>) returnType;
            if (rawType.isAnnotationPresent(SqllaEntity.class)) {
                return new SingleSqllaEntityConverter<>(rawType);
            }
        } else if (returnType instanceof ParameterizedType) {
            ParameterizedType prType = (ParameterizedType) returnType;
            final Class<?> rawType = (Class<?>) prType.getRawType();
            Type[] typeArgs = prType.getActualTypeArguments();
            if (typeArgs.length == 1
                    && rawType == List.class
                    && typeArgs[0] instanceof Class<?>
                    && ((Class<?>) typeArgs[0]).isAnnotationPresent(SqllaEntity.class)) {
                return new ListSqllaEntityConverter<>((Class<?>) typeArgs[0]);
            }
        }

        return null;
    }

    private <T> SqllaEntityConcept<T> getConcept(Class<T> returnRawType) {
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
            return concept;
        }
    }

    private <T> T newEntityFromCursor(SqllaEntityConcept<T> concept, ResultSet resultSet) {
        T obj = concept.newEntity();
        for (SqllaEntityConcept.ColumnField field : concept) {
            try {
                Class<?> type = field.mField.getType();
                if (type == java.util.Date.class) {
                    type = java.sql.Date.class;
                }
                field.set(obj, resultSet.getObject(field.mColumn, type));
            } catch (SQLException ignored) {
            }
        }
        return obj;
    }

    private class SingleSqllaEntityConverter<T> implements ResultConverter<T> {

        private SqllaEntityConcept<T> mConcept;

        private SingleSqllaEntityConverter(Class<T> returnRawType) {
            mConcept = getConcept(returnRawType);
        }

        @Override
        public T convert(ResultSet resultSet) throws SQLException {
            if (resultSet.next()) {
                return newEntityFromCursor(mConcept, resultSet);
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
        public List<T> convert(ResultSet resultSet) throws SQLException {
            SqllaEntityConcept<T> concept = this.mConcept;
            List<T> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(newEntityFromCursor(concept, resultSet));
            }
            return list;
        }
    }
}
