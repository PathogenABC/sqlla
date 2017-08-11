package com.greenbean.poplar.sqlla;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by chrisding on 2017/5/12.
 * <br/>Function: 结果集转换器: 结果集 --> java bean
 */
public interface ResultConverter<T> {

    T convert(Param param) throws SQLException;

    interface Factory {
        ResultConverter<?> getConverter(Type returnType);
    }

    /**
     * Created by chrisding on 2017/6/11.
     * Function: 转换参数
     */
    interface Param {

        String getSql();

        Type getTargetType();

        ResultSet getResultSet();

        <T extends Annotation> T getAnnotation(Class<T> annoClass);

    }
}
