# Sqlla

[![](https://jitpack.io/v/PathogenABC/sqlla.svg)](https://jitpack.io/#PathogenABC/sqlla)

一套数据库的 ORM 微型库，提供简单高效的 API 来操作数据库。
> Sqlla 拥有极少的API，使用方式简单。让开发者不需要关心数据库操作的具体细节，只需专注SQL和业务逻辑。同时简单的事务模型让开发过程增益很多。

### 引入
在 root build.gradle 中添加下面代码

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
在 module build.gradle 中添加依赖

```
dependencies {
    compile 'com.github.PathogenABC:sqlla:1.0.2'
}
```

### 简单使用

* 创建实体类，用 @SqllaEntity 标识

```
@SqllaEntity
public class UserBean {

    private String uid;
    private String phone;
    private String name;

    // 使用 SqllaColumnAlias 标识后的属性使用 alias 的值来对应表中的列
    // 没有标识时直接使用属性的名字来对应
    @SqllaColumnAlias("gender")
    private int sex;

    // setter getter here
}
```

* DAO 接口类

```
public interface UserDao {

    // 用 @Sql 标识
    @Sql("select * from t_user where uid = ?")
    UserBean getUserById(String uid);

    @Sql("select * from t_user desc by lastActiveTS limit ?")
    List<UserBean> topUsers(int limit);

    @Sql("select (count(*) > 0) from t_user where name = ?")
    boolean userExist(String name);

    @Sql("insert into t_user (id, uid, name, phone) values (null, ?, ?, ?)")
    boolean insertUser(String uid, String name, String phone);

    @Sql("delete from t_user where uid = ?")
    boolean deleteUserById(String uid);
}
```

* 创建 Sqlla 实例

```
Sqlla.ConnectionPool pool = your implimention;
Sqlla sqlla = new Sqlla.Builder().pool(pool).build();
```

* 数据库操作 CRUD

```
UserDao dao = sqlla.createApi(UserDao.class);

// 获取结果集中的第一个对象(结果集的第一行)
UserBean bean = dao.getUserById("uid_10000001");

// 查询最新的10个用户
List<UserBean> beanList = dao.topUsers(10);

// 查询用户是否存在：对于updateable sql, 返回值可以是int, boolean(>0 for true), void
boolean exists = dao.userExist("李多情");

// 插入用户到数据库(update count > 0 for true)
boolean inserted = dao.insertUser("uid_10000002", "李明洙", "1324113361*");

// 删除指定用户
boolean deleted = dao.deleteUserById("uid_10000003");
```
> DAO接口不需要任何的实现类，是不是使用非常简单？😁

### 概念

* 实体： 库中预置了两种实体模型
    * `@SqllaEntity` 标识的Pojo实体
    * `ViewObject` 结果集视图实体，代表着结果集的一行，类似于扁平的 `JSONObject`
* 转换器： 将结果集转换成实体的部件，可以自定义
* DAO接口： CRUD操作集合，每个方法代表一条SQL操作
* 事务： Transaction/<T> 代表一个多条DAO方法的事务


### 深入使用

  `Sqlla.Builder` 提供 `pool()` 和 `addConverterFactory()` 方法。

  `pool()`方法必须设置一个`ConnectionPool`实例（这里有一个基于 [c3p0 数据源](https://github.com/PathogenABC/sqlla-pool-c3p0)的 pool）。

  `addConverterFactory()` 方法设置 自定义的结果集转换工厂(实现 `ResultConverter.Factory` 接口)。 内部预置了三种工厂,

  * `PrimitiveTypeConverterFactory`	    转换基础数据类型
  * `SqllaEntityConverterFactory`      转换 @SqllaEntity 表示的实体类和其列表(List)
  * `ViewObjectConverterFactory`       转换 ViewObject 结果集视图和其列表(List<ViewObject>)

> 外部可以自定义针对自己特定类型的转换工厂 (ResultSet --> CustomType)，自定义的转换工厂 `return !null` 时会拦截系统预置的转换工厂。


### 事物支持

当前版本对事物进行了简单的支持，与Spring的配合暂时没有做过测试。

#### 例子

```
Boolean ret = sqlla.transact(new Transaction<Boolean>(Isolation.SERIALIZABLE) {
    public Boolean transact() throw Exception {
        UserDao dao = sqlla.createApi(UserDao.class);
        dao.deleteUserById("uid_10000004");
        // 也可以手动rollback() or commit(true)
        dao.deleteUserById("uid_10000005");
        return true;
    }
}, false);	// failed value
```

上面的代码执行了一个简单的事务，事务中包含两条删除语句。如果都成功，则自动 `commit`；只要有一个失败，则会`committed`。当回滚之后，事务将返回给定的 failed value。在事务中，也可以手动  `committed()` 或者 `commit(val)`，这两个操作只能调用一次，而且会中断其后面的代码执行，要谨慎使用。

##### 开启一个事务有两种方法:

`<T> T sqlla.transact(Transaction/<T> transaction);`

`void sqlla.transact(Transaction0 transaction);`

注意： transact方法是一个同步方法，它会立马调用transaction, 并返回。

`Transaction<T>` 是一个抽象类，抽象方法transact用于实现事务的具体逻辑。最多有三个参数：isolation，readOnly 和 timeout，分别代表隔离级别，是否只读，超时秒数。`Transaction0` 是其范型为 Void 的子类。

#### 嵌套事务

Sqlla 对事务的嵌套提供了简单的支持。相比于Spring事务间的多种传播机制，Sqlla只提供了 `REQUIRES_NEW` 的传播机制：内层和外层完全隔离开来，互不影响。

methodA 和 methodB 是两个事务方法，他们之间完全隔离，提交和回滚互不影响。

```
public void methodA() {
    sqlla.transact(new Transaction0() {
        protected void transact0() throws Exception {
            UserDao dao = sqlla.createApi(UserDao.class);

            boolean inserted = dao.insertUser("uid_1000007", "王五", "13241133615");
            methodB();  // 事务B方法
            boolean deleted = dao.deleteUserByName("张三");
        }
    });
}

public void methodB() {
    sqlla.transact(new Transaction0() {
        protected void transact0() throws Exception {
            UserDao dao = sqlla.createApi(UserDao.class);
            boolean inserted = dao.insertUser("uid_1000008", "赵六", "13241133616");
        }
    }
}
```

> 如何验证两个事务之间互不影响？

我们先把 mehtodA 中的 `deleteUserByName` 方法的sql改成一个错的sql

```
@Sql("delete from t_user where name = ????????")
boolean deleteUserByName(String name);
```

现在再调用 methodA，你会发现 “王五” 这个用户并未插入到数据库中, “张三” 也没有被删除，而 “赵六” 却实实在在的插入到了数据库中。

注意：假如 methodB 本身并不是一个单独的事务方法 (未用 `sqlla.transact` 开启)，那么他将使用外层的事务。所以要不要使用事务，一定要考虑好，不然可能会有一些意想不到的效果。

> Sqlla虽然提供了注解的方式来操作SQL，但是事务并没有使用注解的方式， 这和MyBatis一致。实际内部实现也和 MyBatis 相差无几。


有问题可以在简书上[联系我](http://www.jianshu.com/u/4c0007dc5b43)
