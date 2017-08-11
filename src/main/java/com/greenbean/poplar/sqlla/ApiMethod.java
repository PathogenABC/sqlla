package com.greenbean.poplar.sqlla;

import com.greenbean.poplar.sqlla.entity.SqllaEntity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

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

        TransactionStack stack = TransactionStack.get(sqlla);
        TransactionInstance currentTransaction = stack.currentTransaction();

        Connection conn;
        if (currentTransaction != null) {
            conn = currentTransaction.getConnection();
        } else {
            conn = sqlla.getConnection();
            conn.setAutoCommit(true);
        }

        System.out.println("SQLLA: " + logPrefix() + mSql + ", ARGS = " + Arrays.toString(args));

        int[] configs = this.mResultSetConfigs;
        int holdability = configs[2] == -1 ? conn.getHoldability() : configs[2];
        //noinspection MagicConstant
        PreparedStatement ps = conn.prepareStatement(mSql, configs[0], configs[1], holdability);

        if (currentTransaction != null) {
            int timeout = currentTransaction.getTimeout();
            if (timeout > 0) {
                ps.setQueryTimeout(timeout);
            }
        }

        return executeSqlAndHandleResult(args, ps);
    }

    private Object executeSqlAndHandleResult(Object[] args, PreparedStatement ps) throws SQLException {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg == null) {
                    ps.setNull(i + 1, Types.NULL);
                } else {
                    Class<?> argClass = arg.getClass();
                    if (argClass == String.class) {
                        ps.setString(i + 1, (String) arg);
                    } else if (argClass == java.util.Date.class) {
                        ps.setDate(i + 1, new java.sql.Date(((java.util.Date) arg).getTime()));
                    } else if (argClass == java.sql.Date.class) {
                        ps.setDate(i + 1, (java.sql.Date) arg);
                    } else if (TypeUtils.isPrimitive(argClass)) {
                        ps.setObject(i + 1, arg);
                    } else {
                        throw new SqllarException(logPrefix() + "unsupported argument type: " + argClass.getName() + " at " + i);
                    }
                }
            }
        }
        try {
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
        } catch (SQLException e) {
            throw new SqllarException(logPrefix() + "execute sql and handle result failed", e);
        }
    }

    private Object handleQuerySqlResult(final ResultSet resultSet) throws SQLException {
        return mAdapter.convert(new ResultConverter.Param() {
            @Override
            public String getSql() {
                return mSql;
            }

            @Override
            public Type getTargetType() {
                return mReturnType;
            }

            @Override
            public ResultSet getResultSet() {
                return resultSet;
            }

            @Override
            public <T extends Annotation> T getAnnotation(Class<T> annoClass) {
                return mMethod.getAnnotation(annoClass);
            }
        });
    }

    private boolean isSqllaEntity() {
        return TypeUtils.getRawType(mReturnType).isAnnotationPresent(SqllaEntity.class);
    }

    private boolean isSqllaEntityList() {
        return TypeUtils.getRawType(mReturnType) == List.class
                && TypeUtils.getGenericComponentRawType(mReturnType).isAnnotationPresent(SqllaEntity.class);
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
            throw new SqllarException(logPrefix() + "return type is [" + rawType.getName() + "], boolean, int or void expected for an UPDATABLE sql.");
        }
    }

    private String logPrefix() {
        return "API INTERFACE [" + mApiInterface.getName() + "]'s METHOD[ " + mMethod.getName() + " ]: ";
    }

}
