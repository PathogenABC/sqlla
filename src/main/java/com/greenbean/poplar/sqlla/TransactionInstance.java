package com.greenbean.poplar.sqlla;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by chrisding on 2017/5/17.
 * Function: NULL
 */
class TransactionInstance {

    /**
     * 所在的事务栈
     */
    private final TransactionStack mStack;

    /**
     * 对应的事务
     */
    private Transaction<?> mTransaction;

    /**
     * 是否失效
     */
    private boolean mDead = false;

    /**
     * 绑定到此事务的连接
     */
    private Connection mConnection;

    TransactionInstance(TransactionStack stack) {
        mStack = stack;
    }

    void setDead() {
        mDead = true;
        mConnection = null;
    }

    boolean isDead() {
        return mDead;
    }

    Connection getConnection() throws SQLException {
        if (mDead) {
            throw new SqllarException("connection is already dead");
        }
        if (mConnection == null) {
            Transaction<?> transaction = this.mTransaction;
            Connection connection = mStack.mSqlla.getConnection();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(transaction.mIsolation.level);
            connection.setReadOnly(transaction.mReadOnly);
            mConnection = connection;
        }
        return mConnection;
    }

    private void closeConnection() {
        if (mConnection != null) {
            try {
                mConnection.close();
            } catch (SQLException ignored) {
            }
            mConnection = null;
        }
    }

    void commit() throws SQLException {
        if (mConnection != null) {
            mConnection.commit();
        }
    }

    void rollback() throws SQLException {
        if (mConnection != null) {
            mConnection.rollback();
        }
    }

    int getTimeout() {
        return mTransaction.mTimeout;
    }

    private <T> void setTransaction(Transaction<T> transaction) {
        mTransaction = transaction;
    }

    <T> Result<T> doTransaction(Transaction<T> transaction, T failedVal) {
        transaction.setInstance(this);
        try {
            setTransaction(transaction);

            // call real transaction
            transaction.transact();

            transaction.commitIfUncompleted();

            return new Result<>(null, true);
        } catch (Transaction.CommitAbort ca) {

            //noinspection unchecked
            return new Result<>((T) ca.mCommitValue, true);
        } catch (Transaction.RollbackAbort ra) {
            if (ra.mHasRollbackVal) {
                //noinspection unchecked
                return new Result<>((T) ra.mRollbackValue, false, ra.mException);
            }
            return new Result<>(failedVal, false, ra.mException);
        } catch (Exception e) {
            transaction.rollbackIfUncompleted();
            return new Result<>(failedVal, false, e);
        } finally {
            closeConnection();  // close transaction connection
            transaction.setInstance(null);
            mStack.freeTransaction(this);
        }
    }
}
