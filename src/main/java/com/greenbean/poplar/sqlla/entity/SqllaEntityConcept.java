package com.greenbean.poplar.sqlla.entity;

import com.greenbean.poplar.sqlla.SqllarException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by chrisding on 2017/5/13.
 * Function: 代表实体类的概念模型
 */
class SqllaEntityConcept<T> implements Iterable<SqllaEntityConcept.ColumnField> {

    private final Constructor<T> mNoneArgCtor;
    private final List<ColumnField> mFields;
    final String mQuerySql;
    final ColumnField mPrimaryKey;

    SqllaEntityConcept(Class<T> entityType) {
        ColumnField primaryKey = null;
        List<ColumnField> fields = new LinkedList<>();
        for (Field field : entityType.getDeclaredFields()) {

            int modifiers = field.getModifiers();
            if (Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers)) {
                continue;
            }

            String columnName;
            if (field.isAnnotationPresent(ColumnAlias.class)) {
                columnName = field.getAnnotation(ColumnAlias.class).value();
                if (columnName.isEmpty()) {
                    throw new SqllarException("column name empty of " + entityType.getName());
                }
            } else {
                columnName = field.getName();
            }
            // TODO: columnName 重复检查

            field.setAccessible(true);
            ColumnField columnField = new ColumnField();
            columnField.mColumn = columnName;
            columnField.mField = field;
            resolveColumnType(columnField);
            fields.add(columnField);

            if (field.isAnnotationPresent(PrimaryKey.class)) {
                if (primaryKey == null) {
                    primaryKey = columnField;
                } else {
                    throw new SqllarException("duplicated primary key of " + entityType.getName());
                }
            } else if (columnName.equals("id")) {
                primaryKey = columnField;
            }
        }
        mFields = fields;
        mPrimaryKey = primaryKey;

        SqllaEntity entityAnno = entityType.getAnnotation(SqllaEntity.class);
        mQuerySql = entityAnno.value();

        try {
            mNoneArgCtor = entityType.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new SqllarException("none-arg public constructor not found");
        }
    }

    private void resolveColumnType(ColumnField columnField) {
        Field field = columnField.mField;
        Class<?> type = field.getType();
        if (type.isAnnotationPresent(SqllaEntity.class)) {
            columnField.mIsEntityType = true;
            columnField.mIsInclude = field.isAnnotationPresent(Include.class);
            columnField.mEntityRawType = type;
        } else {
            columnField.mIsEntityType = false;
        }
    }

    void resolveColumnConcepts(SqllaEntityConverterFactory context) {
        for (ColumnField field : mFields) {
            if (field.mIsEntityType) {
                field.mEntityConcept = context.getConcept(field.mEntityRawType);
            }
        }
    }

    T newEntity() {
        try {
            return mNoneArgCtor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new SqllarException("create entity failed", e);
        }
    }

    @Override
    public Iterator<ColumnField> iterator() {
        return mFields.iterator();
    }

    static class ColumnField {

        String mColumn;
        Field mField;

        /**
         * 是否是实体类型
         */
        boolean mIsEntityType;

        /**
         * 实体类型
         */
        private Class<?> mEntityRawType;

        /**
         * 实体类的concept
         */
        SqllaEntityConcept<?> mEntityConcept;

        /**
         * 是否有@include注解, 只有在SqllaEntity实体类才有效
         */
        boolean mIsInclude;

        void set(Object target, Object value) {
            try {
                mField.set(target, value);
            } catch (IllegalAccessException e) {
                throw new SqllarException("set failed", e);
            }
        }
    }
}
