package com.greenbean.poplar.sqlla;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chrisding on 2017/5/12.
 * <br/>Function: 基础数据类型的转换器工厂
 */
class PrimitiveTypeConverterFactory implements ResultConverter.Factory {

    @Override
    public ResultConverter<?> getConverter(Type returnType) {
        final Class<?> rawType = TypeUtils.getRawType(returnType);

        if (rawType == Void.class || rawType == void.class) {
            return new ResultConverter<Void>() {
                @Override
                public Void convert(ResultSet resultSet) throws SQLException {
                    return null;
                }
            };
        }

        if (rawType == String.class || rawType.isPrimitive()) {
            return new ResultConverter<Object>() {
                @Override
                public Object convert(ResultSet resultSet) throws SQLException {
                    if (resultSet.next()) {
                        return resultSet.getObject(1, rawType);
                    }
                    return null;
                }
            };
        }

        if (rawType == List.class) {
            final Class<?> componentRawType = TypeUtils.getGenericComponentRawType(returnType);

            if (componentRawType == String.class || componentRawType.isPrimitive()) {
                return new ResultConverter<List<Object>>() {
                    @Override
                    public List<Object> convert(ResultSet resultSet) throws SQLException {
                        ArrayList<Object> list = new ArrayList<>();
                        if (resultSet.next()) {
                            list.add(resultSet.getObject(1, componentRawType));
                        }
                        return list;
                    }
                };
            }
        }

        return null;
    }
}
