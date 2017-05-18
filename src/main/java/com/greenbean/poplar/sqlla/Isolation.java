package com.greenbean.poplar.sqlla;

import java.sql.Connection;

/**
 * Created by chrisding on 2017/5/17.
 * Function: NULL
 */
public enum Isolation {

    DEFAULT(Connection.TRANSACTION_NONE),

    READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),

    READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),

    REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),

    SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

    final int level;

    Isolation(int level) {
        this.level = level;
    }
}
