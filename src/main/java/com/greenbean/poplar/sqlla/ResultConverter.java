package com.greenbean.poplar.sqlla;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by chrisding on 2017/5/12.
 * <br/>Function: 结果集转换器: 结果集 --> java bean
 */
public interface ResultConverter<T> {

    T convert(ResultSet resultSet) throws SQLException;

    interface Factory {
        ResultConverter<?> getConverter(Type returnType);
    }
}
