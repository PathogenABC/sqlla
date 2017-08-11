package com.greenbean.poplar.sqlla.test;

import java.util.List;

/**
 * Created by chrisding on 2017/6/5.
 * Function: NULL
 */
public class GroupBean {

    private long id;
    private String name;

//    @RelevantKey("t_group_relation", "group_id", "")
    private List<UserBean> members;
}
