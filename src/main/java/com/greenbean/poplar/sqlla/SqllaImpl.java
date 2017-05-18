package com.greenbean.poplar.sqlla;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by chrisding on 2017/5/10.
 * <br/>Function: Sqlla的实现类
 */
class SqllaImpl implements Sqlla, Sqlla.ConnectionPool, ResultConverter.Factory {

    private final Object mConceptsWriteReadLock = new Object();

    private final HashMap<Class<?>, ApiInterfaceConcept> mApiConcepts = new HashMap<>(0);

    private final ConnectionPool mPool;
    private final List<ResultConverter.Factory> mFactories;

    SqllaImpl(ConnectionPool pool, List<ResultConverter.Factory> factories) {
        this.mPool = pool;
        mFactories = factories;
    }

    @Override
    public <T> T createApi(Class<T> apiInterface) {
        if (apiInterface == null) {
            throw new NullPointerException("api interface null");
        }
        if (!apiInterface.isInterface()) {
            throw new IllegalArgumentException("api interface not a valid java interface");
        }

        ClassLoader loader = apiInterface.getClassLoader();
        InvocationHandler handler = new ApiInterfaceProxy(this, apiInterface);
        Class[] interfaces = {apiInterface};
        //noinspection unchecked
        return (T) Proxy.newProxyInstance(loader, interfaces, handler);
    }

    @Override
    public <T> T transact(Transaction<T> transaction, T failedVal) {
        TransactionStack stack = TransactionStack.get(this);
        TransactionInstance ti = stack.allocTransaction();
        return ti.doTransaction(transaction, failedVal);
    }

    @Override
    public void transact(Transaction0 transaction) {
        transact(transaction, null);
    }

    ApiInterfaceConcept getConcept(Class<?> apiInterface) {
        HashMap<Class<?>, ApiInterfaceConcept> concepts = this.mApiConcepts;
        if (concepts.containsKey(apiInterface)) {
            return concepts.get(apiInterface);
        }
        synchronized (mConceptsWriteReadLock) {
            if (concepts.containsKey(apiInterface)) {
                return concepts.get(apiInterface);
            }
            ApiInterfaceConcept concept = new ApiInterfaceConcept(this, apiInterface);
            concepts.put(apiInterface, concept);
            return concept;
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return mPool.getConnection();
    }


    @Override
    public ResultConverter<?> getConverter(Type returnType) {
        for (ResultConverter.Factory factory : mFactories) {
            ResultConverter<?> adapter = factory.getConverter(returnType);
            if (adapter != null) {
                return adapter;
            }
        }
        throw new SqllarException("Unknown return type for result adapter");
    }
}
