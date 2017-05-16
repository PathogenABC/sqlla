package com.greenbean.poplar.sqlla;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by chrisding on 2017/5/11.
 * <br/>Function: 结果集并发选项
 * <br/>{@link java.sql.ResultSet#CONCUR_READ_ONLY} default
 * <br/>{@link java.sql.ResultSet#CONCUR_UPDATABLE}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ResultSetConcurrency {
    int value();
}
