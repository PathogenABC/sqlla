package com.greenbean.poplar.sqlla;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by chrisding on 2017/5/11.
 * <br/>Function: 结果集可保持性选项
 * <br/>{@link java.sql.ResultSet#HOLD_CURSORS_OVER_COMMIT}, {@link java.sql.ResultSet#CLOSE_CURSORS_AT_COMMIT}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ResultSetHoldability {
    int value();
}
