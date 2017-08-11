package com.greenbean.poplar.sqlla;

/**
 * Created by chrisding on 2017/6/14.
 * Function: NULL
 */
public final class Result<T> {

    public final T value;
    public final boolean committed;
    public final Exception exception;

    Result(T value, boolean committed) {
        this.value = value;
        this.committed = committed;
        this.exception = null;
    }

    Result(T value, boolean committed, Exception exception) {
        this.value = value;
        this.committed = committed;
        this.exception = exception;
    }
}
