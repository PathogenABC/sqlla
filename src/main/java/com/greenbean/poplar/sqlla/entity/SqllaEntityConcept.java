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

    private Constructor<T> mNoneArgCtor;
    private List<ColumnField> mFields;

    SqllaEntityConcept(Class<T> entityType) {
        List<ColumnField> fields = new LinkedList<>();
        for (Field field : entityType.getDeclaredFields()) {

            int modifiers = field.getModifiers();
            if (Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers)) {
                continue;
            }

            String columnName;
            if (field.isAnnotationPresent(SqllaColumnAlias.class)) {
                columnName = field.getAnnotation(SqllaColumnAlias.class).value();
            } else {
                columnName = field.getName();
            }
            field.setAccessible(true);
            ColumnField columnField = new ColumnField();
            columnField.mColumn = columnName;
            columnField.mField = field;
            fields.add(columnField);
        }
        mFields = fields;

        try {
            mNoneArgCtor = entityType.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new SqllarException("none-arg public constructor not found");
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

        void set(Object target, Object value) {
            try {
                mField.set(target, value);
            } catch (IllegalAccessException e) {
                throw new SqllarException("set failed", e);
            }
        }
    }
}
