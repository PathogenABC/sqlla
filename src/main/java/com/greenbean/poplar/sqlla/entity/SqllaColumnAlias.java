package com.greenbean.poplar.sqlla.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by chrisding on 2017/5/12.
 * Function: 标识列的名称，如果没有标识，使用属性名作为列的名称
 * <br/>@SqllaEntity
 * <br/>class UserBean {
 * <br/>@SqllaColumnAlias("user_id")
 * <br/>String uid;
 * <br/>String nickname;
 * <br/>}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SqllaColumnAlias {
    String value();
}
