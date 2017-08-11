package com.greenbean.poplar.sqlla.view;

import com.greenbean.poplar.sqlla.ResultConverter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by chrisding on 2017/5/16.
 * <br/>Function: 结果集视图的转换器工厂: 可以转换 {@link ViewObject} 和 {@link List}<{@link ViewObject}>
 */
public class ViewObjectConverterFactory implements ResultConverter.Factory {

    private Map<Type, ResultConverter<?>> mCached = new LinkedHashMap<>(0);

    @Override
    public ResultConverter<?> getConverter(Type returnType) {

        Map<Type, ResultConverter<?>> cachedConverters = this.mCached;
        if (cachedConverters.containsKey(returnType)) {
            return cachedConverters.get(returnType);
        }

        if (returnType == ViewObject.class) {
            SingleViewObjectConverter converter = new SingleViewObjectConverter();
            cachedConverters.put(returnType, converter);
            return converter;
        }

        if (returnType instanceof ParameterizedType) {
            ParameterizedType prType = (ParameterizedType) returnType;
            final Class<?> rawType = (Class<?>) prType.getRawType();
            Type[] componentTypes = prType.getActualTypeArguments();
            if (componentTypes.length == 1 && rawType == List.class && componentTypes[0] == ViewObject.class) {
                ListViewObjectConverter converter = new ListViewObjectConverter();
                cachedConverters.put(returnType, converter);
                return converter;
            }
        }

        cachedConverters.put(returnType, null);
        return null;
    }

    private static ViewObject newObjectFromCursor(ResultSet resultSet) throws SQLException {
        Map<String, Object> map = new HashMap<>(0);
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 1; i < metaData.getColumnCount() + 1; i++) {   // column index start at 1
            String columnName = metaData.getColumnName(i);
            Object value = resultSet.getObject(i);
            map.put(columnName, value);
        }
        return new ViewObjectImpl(map);
    }

    private static class SingleViewObjectConverter implements ResultConverter<ViewObject> {

        @Override
        public ViewObject convert(Param param) throws SQLException {
            ResultSet resultSet = param.getResultSet();
            if (resultSet.next()) {
                return newObjectFromCursor(resultSet);
            }
            return new ViewObjectImpl(null);
        }
    }

    private static class ListViewObjectConverter implements ResultConverter<List<ViewObject>> {

        @Override
        public List<ViewObject> convert(Param param) throws SQLException {
            ResultSet resultSet = param.getResultSet();
            List<ViewObject> list = new ArrayList<>(0);
            while (resultSet.next()) {
                list.add(newObjectFromCursor(resultSet));
            }
            return list;
        }
    }

}
