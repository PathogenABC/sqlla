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

    void commit() throws SQLException {
        mConnection.commit();
    }

    void rollback() throws SQLException {
        mConnection.rollback();
    }

    int getTimeout() {
        return mTransaction.mTimeout;
    }

    private <T> void setTransaction(Transaction<T> transaction) {
        mTransaction = transaction;
    }

    <T> T doTransaction(Transaction<T> transaction, T failedVal) {
        transaction.setInstance(this);
        try {
            setTransaction(transaction);

            // call real transaction
            T val = transaction.transact();

            transaction.commitIfUncompleted();
            return val;
        } catch (Transaction.CommitAbort ca) {
            //noinspection unchecked
            return (T) ca.mCommitValue;
        } catch (Transaction.RollbackAbort ra) {
            if (ra.mHasRollbackVal) {
                //noinspection unchecked
                return (T) ra.mRollbackValue;
            }
            return failedVal;
        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollbackIfUncompleted();
            return failedVal;
        } finally {
            transaction.setInstance(null);
            mStack.freeTransaction(this);
        }
    }
}
