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

    protected abstract void transact() throws Exception;

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

    protected final void rollback(T retValue) throws SQLException {
//        if (mCompleted) {
//            throw new SqllarException("transaction failed: transaction has been already completed");
//        }
        mCompleted = true;
        mInstance.rollback();
        throw new RollbackAbort(retValue);
    }

    protected final void rollback() throws SQLException {
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

        final Object mCommitValue;

        CommitAbort(Object retValue) {
            mCommitValue = retValue;
        }
    }

    static class RollbackAbort extends RuntimeException {

        final Object mRollbackValue;
        final boolean mHasRollbackVal;
        final Exception mException;

        RollbackAbort(Object retValue, Exception exception) {
            mRollbackValue = retValue;
            mHasRollbackVal = true;
            mException = exception;
        }

        RollbackAbort(Object retValue) {
            this(retValue, null);
        }

        RollbackAbort(Exception exception) {
            mRollbackValue = null;
            mHasRollbackVal = false;
            mException = exception;
        }

        RollbackAbort() {
            this(null);
        }
    }
}
