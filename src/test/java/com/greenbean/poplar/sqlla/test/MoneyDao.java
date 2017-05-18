package com.greenbean.poplar.sqlla.test;

import com.greenbean.poplar.sqlla.Sql;

/**
 * Created by chrisding on 2017/5/17.
 * Function: NULL
 */
public interface MoneyDao {

    @Sql("update t_table set money = (money + ?) where uid = ?")
    boolean addMoneyForUser(float money, String uid);

    boolean addJifenForUser(String uid, int jifen);
}
