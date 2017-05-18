package com.greenbean.poplar.sqlla;

import java.util.*;

/**
 * Created by chrisding on 2017/5/17.
 * Function: NULL
 */
class TransactionStack {

    private static ThreadLocal<Map<SqllaImpl, TransactionStack>> stInstance = new ThreadLocal<>();

    static {
        stInstance.set(new WeakHashMap<SqllaImpl, TransactionStack>(0));
    }

    final SqllaImpl mSqlla;
    private final Stack<TransactionInstance> mStack;

    private TransactionStack(SqllaImpl sqlla) {
        mSqlla = sqlla;
        mStack = new Stack<>();
    }

    static TransactionStack get(SqllaImpl sqlla) {
        Map<SqllaImpl, TransactionStack> stackMap = stInstance.get();

        if (stackMap.containsKey(sqlla)) {
            return stackMap.get(sqlla);
        }

        synchronized (TransactionStack.class) {
            if (stackMap.containsKey(sqlla)) {
                return stackMap.get(sqlla);
            }
            TransactionStack stack = new TransactionStack(sqlla);
            stackMap.put(sqlla, stack);
            return stack;
        }
    }

    TransactionInstance allocTransaction() {
        TransactionInstance concept = new TransactionInstance(this);
        mStack.push(concept);
        return concept;
    }

    void freeTransaction(TransactionInstance concept) {
        if (!concept.isDead()) {
            mStack.remove(concept);
            concept.setDead();
        }
    }

    TransactionInstance currentTransaction() {
        if (mStack.isEmpty()) {
            return null;
        }
        return mStack.peek();
    }
}
