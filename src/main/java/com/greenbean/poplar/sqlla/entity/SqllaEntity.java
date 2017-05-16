package com.greenbean.poplar.sqlla.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by chrisding on 2017/5/12.
 * Function: 标识某个类为实体对象
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SqllaEntity {
}
