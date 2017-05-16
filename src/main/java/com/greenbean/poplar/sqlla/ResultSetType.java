package com.greenbean.poplar.sqlla;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by chrisding on 2017/5/11.
 * <br/>Function: 结果集类型选项
 * <br/>{@link java.sql.ResultSet#TYPE_FORWARD_ONLY} default
 * <br/>{@link java.sql.ResultSet#TYPE_SCROLL_INSENSITIVE}
 * <br/>{@link java.sql.ResultSet#TYPE_SCROLL_SENSITIVE}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ResultSetType {
    int value();
}
