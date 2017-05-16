package com.greenbean.poplar.sqlla;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by chrisding on 2017/5/11.
 * <br/>Function: DAO接口的方法代理
 */
class ApiInterfaceProxy implements InvocationHandler {

    private final SqllaImpl mSqlla;
    private final Class<?> mApiInterface;

    ApiInterfaceProxy(SqllaImpl sqlla, Class<?> apiInterface) {
        mSqlla = sqlla;
        mApiInterface = apiInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        SqllaImpl sqlla = this.mSqlla;
        ApiInterfaceConcept apiConcept = sqlla.getConcept(mApiInterface);
        ApiMethod apiMethod = apiConcept.getApiMethod(method);
        return apiMethod.invoke(sqlla, args);
    }

}
