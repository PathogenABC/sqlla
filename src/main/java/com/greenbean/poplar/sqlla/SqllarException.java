package com.greenbean.poplar.sqlla;

/**
 * Created by chrisding on 2017/5/12.
 * <br/>Function: Sqlla runtime Exception
 */
public class SqllarException extends RuntimeException {

    public SqllarException() {
    }

    public SqllarException(String message) {
        super(message);
    }

    public SqllarException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqllarException(Throwable cause) {
        super(cause);
    }

    public SqllarException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
