package com.greenbean.poplar.sqlla;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chrisding on 2017/5/12.
 * <br/>Function: 基础数据类型的转换器工厂
 */
class PrimitiveTypeConverterFactory implements ResultConverter.Factory {

    private Map<Type, ResultConverter<?>> mCached = new LinkedHashMap<>(0);

    @Override
    public ResultConverter<?> getConverter(Type returnType) {

        Map<Type, ResultConverter<?>> cachedConverters = this.mCached;
        if (cachedConverters.containsKey(returnType)) {
            return cachedConverters.get(returnType);
        }

        final Class<?> rawType = TypeUtils.getRawType(returnType);

        if (rawType == Void.class || rawType == void.class) {
            ResultConverter<Void> converter = new ResultConverter<Void>() {
                @Override
                public Void convert(ResultSet resultSet) throws SQLException {
                    return null;
                }
            };
            cachedConverters.put(returnType, converter);
            return converter;
        }

        if (rawType == String.class || TypeUtils.isPrimitive(rawType)) {
            ResultConverter<Object> converter = new ResultConverter<Object>() {
                @Override
                public Object convert(ResultSet resultSet) throws SQLException {
                    if (resultSet.next()) {
                        return resultSet.getObject(1, rawType);
                    }
                    return null;
                }
            };
            cachedConverters.put(returnType, converter);
            return converter;
        }

        if (rawType == List.class) {
            final Class<?> componentRawType = TypeUtils.getGenericComponentRawType(returnType);

            if (componentRawType == String.class || TypeUtils.isPrimitive(componentRawType)) {
                ResultConverter<List<Object>> converter = new ResultConverter<List<Object>>() {
                    @Override
                    public List<Object> convert(ResultSet resultSet) throws SQLException {
                        ArrayList<Object> list = new ArrayList<>();
                        if (resultSet.next()) {
                            list.add(resultSet.getObject(1, componentRawType));
                        }
                        return list;
                    }
                };
                cachedConverters.put(returnType, converter);
                return converter;
            }
        }

        cachedConverters.put(returnType, null);
        return null;
    }
}
