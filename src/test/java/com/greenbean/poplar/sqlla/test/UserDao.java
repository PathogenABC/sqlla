package com.greenbean.poplar.sqlla.test;

import com.greenbean.poplar.sqlla.ResultSetConcurrency;
import com.greenbean.poplar.sqlla.ResultSetHoldability;
import com.greenbean.poplar.sqlla.ResultSetType;
import com.greenbean.poplar.sqlla.Sql;
import com.greenbean.poplar.sqlla.view.ViewObject;

import java.sql.ResultSet;
import java.util.List;

/**
 * Created by chrisding on 2017/5/10.
 * Function: NULL
 */
public interface UserDao {

    @ResultSetType(ResultSet.TYPE_FORWARD_ONLY)
    @ResultSetConcurrency(ResultSet.CONCUR_READ_ONLY)
    @ResultSetHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT)
    @Sql("select * from t_user where uid = ?")
    UserBean getUserById(String uid);

    @Sql("select * from t_user desc by lastActiveTS limit ?")
    List<UserBean> topUsers(int limit);

    @Sql("select * from t_user where phone = ?")
    List<UserBean> getUserByPhone(String phone);

    @Sql("select (count(*) > 0) as count from t_user where name = ?1")
    boolean userExist(String name);

    @Sql("insert into t_user (id, uid, name, phone) values (null, ?, ?, ?)")
    boolean insertUser(String uid, String name, String phone);

    @Sql("select * from t_user")
    List<ViewObject> selectUsers();

    @Sql("select * from t_user where id = (select max(id) from t_user)")
    UserBean maxIdUser();
}
