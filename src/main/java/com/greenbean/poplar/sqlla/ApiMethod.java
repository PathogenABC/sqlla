package com.greenbean.poplar.sqlla;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by chrisding on 2017/5/12.
 * <br/>Function: DAO方法的概念模型
 */
class ApiMethod {

    /**
     * Api interface that belongs to
     */
    private final Class<?> mApiInterface;

    /**
     * Target method
     */
    private final Method mMethod;

    /**
     * Relative sql
     */
    private final String mSql;

    /**
     * ResultSetType,
     * ResultSetConcurrency,
     * ResultSetHoldability
     */
    private final int[] mResultSetConfigs;

    /**
     * Return type for result set or updated count
     */
    private final Type mReturnType;

    /**
     * Result adapter for converting sql result set to java object of return type.
     */
    private final ResultConverter mAdapter;

    ApiMethod(Class<?> apiInterface, Method method, String sql, Type returnType, int[] resultSetConfigs, ResultConverter adapter) {
        this.mApiInterface = apiInterface;
        this.mMethod = method;
        this.mSql = sql;
        this.mReturnType = returnType;
        this.mResultSetConfigs = resultSetConfigs;
        this.mAdapter = adapter;
    }

    Object invoke(SqllaImpl sqlla, Object[] args) throws SQLException {

        Connection conn = sqlla.getConnection();

        int[] configs = this.mResultSetConfigs;
        int holdability = configs[2] == -1 ? conn.getHoldability() : configs[2];
        //noinspection MagicConstant
        PreparedStatement ps = conn.prepareStatement(mSql, configs[0], configs[1], holdability);

        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                Class<?> argClass = arg.getClass();
                if (argClass.isPrimitive()) {
                    ps.setObject(i + 1, arg);
                } else if (argClass == String.class) {
                    ps.setString(i + 1, (String) arg);
                } else if (argClass == java.util.Date.class) {
                    ps.setDate(i + 1, new java.sql.Date(((java.util.Date) arg).getTime()));
                } else if (argClass == java.sql.Date.class) {
                    ps.setDate(i + 1, (java.sql.Date) arg);
                } else {
                    throw new SqllarException(errorPrefix() + "unsupported argument type: " + argClass.getName() + " at " + i);
                }
            }
        }
        boolean query = ps.execute();
        int updateCount = ps.getUpdateCount();
        if (query) {
            // result set
            return handleQuerySqlResult(ps.getResultSet());
        } else if (updateCount != -1) {
            // update count
            return handleUpdatableSqlResult(updateCount);
        } else {
            // no result
            return null;
        }
    }

    private Object handleQuerySqlResult(ResultSet resultSet) throws SQLException {
        return mAdapter.convert(resultSet);
    }

    private Object handleUpdatableSqlResult(int updateCount) {
        Class<?> rawType = TypeUtils.getRawType(mReturnType);
        if (rawType == boolean.class || rawType == Boolean.class) {
            return updateCount > 0;
        } else if (rawType == int.class || rawType == Integer.class) {
            return updateCount;
        } else if (rawType == void.class || rawType == Void.class) {
            return null;
        } else {
            throw new SqllarException(errorPrefix() + "return type is [" + rawType.getName() + "], boolean, int or void expected for an UPDATABLE sql.");
        }
    }

    private String errorPrefix() {
        return "api interface [" + mApiInterface.getName() + "]'s api method[" + mMethod.getName() + "]: ";
    }

}
