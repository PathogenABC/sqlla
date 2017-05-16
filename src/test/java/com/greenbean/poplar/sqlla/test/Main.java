package com.greenbean.poplar.sqlla.test;

import com.greenbean.poplar.sqlla.Sqlla;
import com.greenbean.poplar.sqlla.view.ViewObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by chrisding on 2017/5/10.
 * Function: NULL
 */
public class Main {

    public static void main(String[] args) throws IOException {

//        InputStream confResource = Main.class.getResourceAsStream("/sqlla/c3p0_config.properties");
//        Sqlla.ConnectionPool pool = new C3P0ConnectionPool(confResource);
        Sqlla.ConnectionPool pool = new C3P0ConnectionPool("sqlla/conf/c3p0_config.properties");
        Sqlla sqlla = new Sqlla.Builder().pool(pool).build();

        UserDao api = sqlla.createApi(UserDao.class);
        UserBean bean = api.getUserById("test_123");

        if (bean == null) {
            System.out.println("no user with id 'test_123'");
        } else {
            System.out.println(bean);
        }

        List<UserBean> beans = api.getUserByPhone("13241133612");
        if (beans == null) {
            System.out.println("no user with phone '13241133612'");
        } else {
            System.out.println(beans);
        }

        boolean exist = api.userExist("李明洙");
        System.out.println("user named '李明洙' " + (exist ? "exists" : "doesn't exist"));

        List<ViewObject> viewObjects = api.selectUsers();
        System.out.println(viewObjects);

        boolean yes = api.insertUser("test_1234", "张三", "13241133616");
        System.out.println("insert user " + (yes ? "success" : "failed"));

    }

}
