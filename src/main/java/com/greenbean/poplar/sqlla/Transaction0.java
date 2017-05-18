package com.greenbean.poplar.sqlla;

/**
 * Created by chrisding on 2017/5/17.
 * Function: NULL
 */
public abstract class Transaction0 extends Transaction<Void> {

    @Override
    protected final Void transact() throws Exception {
        transact0();
        return null;
    }

    protected abstract void transact0() throws Exception;
}
