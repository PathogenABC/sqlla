package com.greenbean.poplar.sqlla.test;

import com.greenbean.poplar.sqlla.Sqlla;
import com.greenbean.poplar.sqlla.Transaction;
import com.greenbean.poplar.sqlla.Transaction0;
import com.greenbean.poplar.sqlla.view.ViewObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by chrisding on 2017/5/10.
 * Function: NULL
 */
public class Main {

    private static Sqlla sSqlla;

    public static void main0(String[] args) throws IOException {

//        InputStream confResource = Main.class.getResourceAsStream("/sqlla/c3p0_config.properties");
//        Sqlla.ConnectionPool pool = new C3P0ConnectionPool(confResource);
        Sqlla.ConnectionPool pool = new C3P0ConnectionPool("sqlla/conf/c3p0_config.properties");
        Sqlla sqlla = new Sqlla.Builder().pool(pool).build();
        sSqlla = sqlla;

        final UserDao api = sqlla.createApi(UserDao.class);
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

    public static void main(String[] args) {
        Sqlla.ConnectionPool pool = new C3P0ConnectionPool("sqlla/conf/c3p0_config.properties");
        final Sqlla sqlla = new Sqlla.Builder().pool(pool).build();
        sSqlla = sqlla;


        final UserDao api = sqlla.createApi(UserDao.class);

        UserBean maxIdUser = api.maxIdUser();
        System.out.println(maxIdUser);

        sqlla.transact(new Transaction0() {

            @Override
            protected void transact0() throws Exception {

                boolean yes = api.insertUser("test_1234", "王五", "13241133615");
                System.out.println("insert user " + (yes ? "success" : "failed"));

                boolean exist = api.userExist("李明洙");   // sql 错误, 会rollback
                System.out.println("user named '李明洙' " + (exist ? "exists" : "doesn't exist"));

                sqlla.transact(new Transaction0() {

                    @Override
                    protected void transact0() throws Exception {
                        boolean yes = api.insertUser("test_1234", "赵六", "13241133616");
                        commit(null);
                        System.out.println("insert user " + (yes ? "success" : "failed"));
                    }
                });
            }
        });

        maxIdUser = api.maxIdUser();
        System.out.println(maxIdUser);
    }

    static boolean transferMoney(final String myUid, final String acceptorUid, final float money) {
        return sSqlla.transact(new Transaction<Boolean>() {
            public Boolean transact() throws Exception {
                MoneyDao api = sSqlla.createApi(MoneyDao.class);
                boolean b = api.addMoneyForUser(-money, myUid);
                b &= api.addMoneyForUser(money, acceptorUid);
                if (!b) rollback();
                return b;
            }
        }, false);
    }

    static boolean transferMoneyAndUpdateJifen() {
        return sSqlla.transact(new Transaction<Boolean>() {
            @Override
            protected Boolean transact() throws Exception {
                MoneyDao api = sSqlla.createApi(MoneyDao.class);
                boolean b = transferMoney("111", "222", 100);
                b &= api.addJifenForUser("111", 2);
                b &= api.addJifenForUser("222", 2);
                if (!b) rollback();
                return b;
            }
        }, false);
    }

}
