package com.greenbean.poplar.sqlla;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by chrisding on 2017/5/10.
 * <br/>Function: 标识DAO接口的方法，指明此方法对应的SQL语句
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Sql {
    String value();
}
