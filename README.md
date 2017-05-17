# Sqlla
一个数据库的 ORM 微型库，提供简单高可用的API来操作数据库。
> Sqlla 拥有极少的API，使用方式简单。让开发者不需要关心数据库操作的具体细节，只需专注SQL和业务逻辑。有别于MyBatis的XML配置方式，Sqlla默认采用少量的纯注解的方式来配置实体类，当然如果自定义转换器，也可摒弃默认的注解方式，实现自定义的方式配置实体类。系统还提供了另外一种实体方式，类似于 JSONObject 的万能实体 ViewObject。


### 使用方法:

实体类, 用 @SqllaEntity 标识

```
@SqllaEntity
public class UserBean {

    private String uid;
    private String phone;
    private String name;
    private int gender;
    private Date birthdate;
    
    // 使用 SqllaColumnAlias 标识后的属性使用 alias 的值来对应表中的列
    // 没有标识时直接使用属性的名字来对应
    @SqllaColumnAlias("grade")
    private float score;
    
    // setter getter here
}
```

DAO 接口类

```
public interface UserDao {

    @ResultSetType(ResultSet.TYPE_FORWARD_ONLY)
    @ResultSetConcurrency(ResultSet.CONCUR_READ_ONLY)
    @ResultSetHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT)
    @Sql("select * from t_user where uid = ?")
    UserBean getUserById(String uid);

    // 必须要用 @Sql 标识
    @Sql("select * from t_user desc by lastActiveTS limit ?")
    List<UserBean> topUsers(int limit);

    @Sql("select * from t_user where phone = ?")
    List<UserBean> getUserByPhone(String phone);

    @Sql("select (count(*) > 0) from t_user where name = ?")
    boolean userExist(String name);

    @Sql("insert into t_user (id, uid, name, phone) values (null, ?, ?, ?)")
    boolean insertUser(String uid, String name, String phone);

    @Sql("select * from t_user")
    ViewObject selectFirstUser();
    
    @Sql("select * from t_user")
    List<ViewObject> selectUsers();

    @Sql("delete from t_user where uid = ?")
    boolean deleteUserById(String uid);
}
```

数据库操作 CRUD

```
Sqlla.ConnectionPool pool = your implimention;
Sqlla sqlla = new Sqlla.Builder().pool(pool).build();
UserDao dao = sqlla.createApi(UserDao.class);

// 获取结果集中的第一个对象(结果集的第一行)
UserBean bean = dao.getUserById("wxid_kjcvioer?sak193d_dsagx");

// 查询最新的10个用户
List<UserBean> beanList = dao.topUsers(10);

// 查询用户是否存在：对于updateable sql, 返回值可以是int, boolean(>0 for true), void
boolean exists = dao.userExist("李多情");

// 插入用户到数据库(update count > 0 for true)
boolean inserted = dao.insertUser("uid_xsdvo6ek93_1xz", "李明洙", "1324113361*");

// 获取第一个用户, 不建议这种方式获取第一个用户，因为结果集实际上读了很多条。建议使用sql来过滤(limit 1)
// ViewObject 是结果集视图对象, 代表结果集的一行, 类似于 JSONObject
// 不同点在于 ViewObject 是扁平的，内部没有多层级和数组
ViewObject user = dao.selectFirstUser();

// 获取所有用户
List<ViewObject> userList = dao.selectUsers();

// 删除指定用户
boolean deleted = dao.deleteUserById("uid_xsdvo6ek93_1xz");
```


### 深入使用

  Sqlla.Builder 提供 pool(poolinstance) 和 addConverterFactory(factory) 等方法。
  
  pool()方法必须设置一个ConnectionPool实例（test 代码提供了一个基于c3p0数据源的 pool）。
  
  addConverterFactory() 方法设置 自定义的结果集转换工厂(实现 ResultConverter.Factory 接口)。 内部预置了三种工厂, 
  
  * PrimitiveTypeConverterFactory		转换基础数据类型
  * SqllaEntityConverterFactory 		转换 @SqllaEntity 表示的实体类和其列表(List)
  * ViewObjectConverterFactory		    转换 ViewObject 结果集视图和其列表(List<ViewObject>)
  
> 外部可以自定义针对自己特定类型的转换工厂 (ResultSet --> CustomBeanType)，自定义的转换工厂 return !null 时会拦截系统预置的转换工厂。


### 事物支持

当前版本对事物进行了简单的支持，与Spring的配合暂时没有做过测试。

现将userExist方法的sql故意写错, 下面代码执行userExist方法时会抛出SQLException, 会导致事务的回滚。经过测试你会发现，回滚不会对内部事务造成影响：即内部 insertUser 成功插入到数据库了，外部的 insertUser 却没有。

> 所以，相比于Spring事务间的多种传播机制，Sqlla只提供了 REQUIRES_NEW 的传播机制：内层和外层完全隔离开来，互不影响。

```
sqlla.transact(new VoidTransaction() {
	@Override
	protected void transact0() throws Exception {
		sqlla.transact(new VoidTransaction() {
    		@Override
			protected void transact0() throws Exception {
				boolean yes = api.insertUser("test_1234", "赵六", "13241133616");
				System.out.println("insert user " + (yes ? "success" : "failed"));
          }
		});

		boolean yes = api.insertUser("test_1234", "王五", "13241133615");
		System.out.println("insert user " + (yes ? "success" : "failed"));
		
		boolean exist = api.userExist("李明洙");   // sql 错误, 会rollback
		System.out.println("user named '李明洙' " + (exist ? "exists" : "doesn't exist"));
    }
});
```

对于上面的逻辑我们可以拆分成两个service方法:	methodA(), methodB()

```
public void methodA() {
	sqlla.transact(new VoidTransaction() {
	   @Override
		protected void transact0() throws Exception {
	    
	    	methodB();
	    	
			boolean yes = api.insertUser("test_1234", "王五", "13241133615");
			System.out.println("insert user " + (yes ? "success" : "failed"));
			
			boolean exist = api.userExist("李明洙");   // sql 错误, 会rollback
			System.out.println("user named '李明洙' " + (exist ? "exists" : "doesn't exist"));
	    }
	});
}

public void methodB() {
	sqlla.transact(new VoidTransaction() {
	   @Override
		protected void transact0() throws Exception {
			boolean yes = api.insertUser("test_1234", "赵六", "13241133616");
			System.out.println("insert user " + (yes ? "success" : "failed"));
		}
	}
}
```
methodA 和 methodB 是两个事务方法，他们之间完全隔离，提交和回滚互不影响。

但是，假如 methodB 本身并不是一个单独的事务方法 (sqlla.transact)，那么他将使用外层的事务。

##### 开启一个事务

`<T> T sqlla.transact(Transaction<T> transaction);`

`void sqlla.transact(VoidTransaction transaction);`

注意： transact方法是一个同步方法，它会立马调用transaction, 并返回。

Transaction<T> 是一个抽象类，抽象方法transact用于实现事务的具体逻辑，可以手动rollback或者commit，但最多只能调用一次。

> Sqlla虽然提供了注解的方式来操作SQL，但是事务并没有使用注解的方式， 这和MyBatis一致。实际内部实现也和 MyBatis 相差无几。

[GitHub链接](https://github.com/PathogenABC/sqlla)
