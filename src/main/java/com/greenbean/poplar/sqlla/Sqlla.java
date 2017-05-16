package com.greenbean.poplar.sqlla;

import com.greenbean.poplar.sqlla.entity.SqllaEntityConverterFactory;
import com.greenbean.poplar.sqlla.view.ViewObjectConverterFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by chrisding on 2016/12/10.
 * <br/>Function: DAO框架的管理者类
 */
public interface Sqlla {

    <T> T createApi(Class<T> apiClass);

    class Builder {

        private ConnectionPool mPool;
        private final List<ResultConverter.Factory> mFactories = new LinkedList<>();

        public Builder pool(ConnectionPool pool) {
            this.mPool = pool;
            return this;
        }

        public Builder addConverterFactory(ResultConverter.Factory factory) {
            mFactories.add(factory);
            return this;
        }

        public Sqlla build() {
            if (mPool == null) {
                throw new SqllarException("null connection pool");
            }
            List<ResultConverter.Factory> factories = this.mFactories;
            factories.add(new SqllaEntityConverterFactory());
            factories.add(new ViewObjectConverterFactory());
            factories.add(new PrimitiveTypeConverterFactory());
            return new SqllaImpl(mPool, factories);
        }
    }

    interface ConnectionPool {
        Connection getConnection() throws SQLException;
    }

}
