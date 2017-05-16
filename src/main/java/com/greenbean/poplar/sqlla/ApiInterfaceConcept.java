package com.greenbean.poplar.sqlla;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisding on 2017/5/11.
 * <br/>Function: DAO接口的概念模型
 */
class ApiInterfaceConcept {

    private final Map<Method, ApiMethod> mApiMethodMap = new HashMap<>(0);

    ApiInterfaceConcept(SqllaImpl sqlla, Class<?> apiInterface) {
        resolveAllApiMethods(sqlla, apiInterface);
    }

    private void resolveAllApiMethods(SqllaImpl sqlla, Class<?> apiInterface) {

        if (!apiInterface.isInterface()) {
            throw new SqllarException("apiInterface [" + apiInterface.getName() + "] type not a java interface");
        }

        Method[] methods = apiInterface.getDeclaredMethods();
        for (Method method : methods) {

            int[] resultSetConfigs = {ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, -1};
            String sql = null;
            Annotation[] annotations = method.getDeclaredAnnotations();

            for (Annotation anno : annotations) {
                Class<? extends Annotation> type = anno.annotationType();
                if (type == ResultSetType.class) {
                    resultSetConfigs[0] = ((ResultSetType) anno).value();
                } else if (type == ResultSetConcurrency.class) {
                    resultSetConfigs[1] = ((ResultSetConcurrency) anno).value();
                } else if (type == ResultSetHoldability.class) {
                    resultSetConfigs[2] = ((ResultSetHoldability) anno).value();
                } else if (type == Sql.class) {
                    sql = ((Sql) anno).value();
                }
            }
            if (sql == null || sql.length() == 0) {
                throw new SqllarException("api interface [" + apiInterface.getName() + "]'s method [" +
                        method.getName() + "] must be annotated by a valid @Sql annotation");
            }

            Type returnType = method.getGenericReturnType();
            ResultConverter<?> adapter = sqlla.getConverter(returnType);
            mApiMethodMap.put(method, new ApiMethod(apiInterface, method, sql, returnType, resultSetConfigs, adapter));
        }
    }

    ApiMethod getApiMethod(Method rawMethod) {
        return mApiMethodMap.get(rawMethod);
    }

}
