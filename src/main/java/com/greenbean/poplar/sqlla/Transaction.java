package com.greenbean.poplar.sqlla;

import java.sql.SQLException;

/**
 * Created by chrisding on 2017/5/17.
 * Function: NULL
 */
public abstract class Transaction<T> {

    final Isolation mIsolation;
    final boolean mReadOnly;
    final int mTimeout;
    private boolean mCompleted;
    private TransactionInstance mInstance;

    protected Transaction(Isolation isolation, boolean readOnly, int timeoutSeconds) {
        this.mIsolation = isolation;
        this.mReadOnly = readOnly;
        this.mTimeout = timeoutSeconds;
    }

    protected Transaction(Isolation isolation, boolean readOnly) {
        this(isolation, readOnly, -1);   // 2秒
    }

    protected Transaction(Isolation isolation, int timeoutSeconds) {
        this(isolation, false, timeoutSeconds);
    }

    protected Transaction(Isolation isolation) {
        this(isolation, -1);
    }

    protected Transaction(boolean readOnly, int timeoutSeconds) {
        this(Isolation.READ_COMMITTED, readOnly, timeoutSeconds);
    }

    protected Transaction(boolean readOnly) {
        this(readOnly, -1);
    }

    protected Transaction(int timeoutSeconds) {
        this(false, timeoutSeconds);
    }

    protected Transaction() {
        this(-1);
    }

    protected abstract T transact() throws Exception;

    protected boolean isCompleted() {
        return mCompleted;
    }

    void setInstance(TransactionInstance instance) {
        mInstance = instance;
    }

    protected final void commit(T retValue) throws SQLException {
//        if (mCompleted) {
//            throw new SqllarException("transaction failed: transaction has been already completed");
//        }
        mCompleted = true;
        mInstance.commit();
        throw new CommitAbort(retValue);
    }

    protected final void rollback() throws SQLException {
//        if (mCompleted) {
//            throw new SqllarException("transaction failed: transaction has been already completed");
//        }
        mCompleted = true;
        mInstance.rollback();
        throw new RollbackAbort();
    }

    void commitIfUncompleted() {
        if (!mCompleted) {
            mCompleted = true;
            try {
                mInstance.commit();
            } catch (SQLException ignored) {
            }
        }
    }

    void rollbackIfUncompleted() {
        if (!mCompleted) {
            mCompleted = true;
            try {
                mInstance.rollback();
            } catch (SQLException ignored) {
            }
        }
    }

    static class CommitAbort extends RuntimeException {

        final Object mRetValue;

        CommitAbort(Object retValue) {
            mRetValue = retValue;
        }
    }

    static class RollbackAbort extends RuntimeException {
    }
}
