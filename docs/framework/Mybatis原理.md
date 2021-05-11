# MyBatis 原理

> MyBatis 的前身就是 iBatis ，是一个作用在数据持久层的对象关系映射（Object Relational Mapping，简称 ORM）框架。

![img](http://dunwu.test.upcdn.net/snap/20200716162305.png)

<!-- TOC depthFrom:2 depthTo:3 -->

- [1. Mybatis 完整示例](#1-mybatis-完整示例)
  - [1.1. 数据库准备](#11-数据库准备)
  - [1.2. 添加 Mybatis](#12-添加-mybatis)
  - [1.3. Mybatis 配置](#13-mybatis-配置)
  - [1.4. Mapper](#14-mapper)
  - [1.5. 测试程序](#15-测试程序)
- [2. Mybatis 生命周期](#2-mybatis-生命周期)
  - [2.1. SqlSessionFactoryBuilder](#21-sqlsessionfactorybuilder)
  - [2.2. SqlSessionFactory](#22-sqlsessionfactory)
  - [2.3. SqlSession](#23-sqlsession)
  - [2.4. 映射器](#24-映射器)
- [3. Mybatis 原理](#3-mybatis-原理)
  - [3.1. MyBatis 的架构](#31-mybatis-的架构)
  - [3.2. 接口层](#32-接口层)
  - [3.3. 数据处理层](#33-数据处理层)
  - [3.4. 框架支撑层](#34-框架支撑层)
  - [3.5. 引导层](#35-引导层)
  - [3.6. 主要组件](#36-主要组件)
- [4. 源码解读](#4-源码解读)
  - [4.1. SqlSession 工作流程](#41-sqlsession-工作流程)
- [5. 参考资料](#5-参考资料)

<!-- /TOC -->

## 1. Mybatis 完整示例

> 这里，我将以一个入门级的示例来演示 Mybatis 是如何工作的。
>
> 注：本文后面章节中的原理、源码部分也将基于这个示例来进行讲解。

### 1.1. 数据库准备

在本示例中，需要针对一张用户表进行 CRUD 操作。其数据模型如下：

```sql
CREATE TABLE IF NOT EXISTS user (
    id      BIGINT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Id',
    name    VARCHAR(10)         NOT NULL DEFAULT '' COMMENT '用户名',
    age     INT(3)              NOT NULL DEFAULT 0 COMMENT '年龄',
    address VARCHAR(32)         NOT NULL DEFAULT '' COMMENT '地址',
    email   VARCHAR(32)         NOT NULL DEFAULT '' COMMENT '邮件',
    PRIMARY KEY (id)
) COMMENT = '用户表';

INSERT INTO user (name, age, address, email)
VALUES ('张三', 18, '北京', 'xxx@163.com');
INSERT INTO user (name, age, address, email)
VALUES ('李四', 19, '上海', 'xxx@163.com');
```

### 1.2. 添加 Mybatis

如果使用 Maven 来构建项目，则需将下面的依赖代码置于 pom.xml 文件中：

```xml
<dependency>
  <groupId>org.mybatis</groupId>
  <artifactId>mybatis</artifactId>
  <version>x.x.x</version>
</dependency>
```

### 1.3. Mybatis 配置

XML 配置文件中包含了对 MyBatis 系统的核心设置，包括获取数据库连接实例的数据源（DataSource）以及决定事务作用域和控制方式的事务管理器（TransactionManager）。

本示例中只是给出最简化的配置。

【示例】mybatis-config.xml 文件

```xml
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
  <environments default="development">
    <environment id="development">
      <transactionManager type="JDBC" />
      <dataSource type="POOLED">
        <property name="driver" value="com.mysql.cj.jdbc.Driver" />
        <property name="url"
                  value="jdbc:mysql://127.0.0.1:3306/spring_tutorial?serverTimezone=UTC" />
        <property name="username" value="root" />
        <property name="password" value="root" />
      </dataSource>
    </environment>
  </environments>
  <mappers>
    <mapper resource="mybatis/mapper/UserMapper.xml" />
  </mappers>
</configuration>
```

> 说明：上面的配置文件中仅仅指定了数据源连接方式和 User 表的映射配置文件。

### 1.4. Mapper

#### Mapper.xml

个人理解，Mapper.xml 文件可以看做是 Mybatis 的 JDBC SQL 模板。

【示例】UserMapper.xml 文件

下面是一个通过 Mybatis Generator 自动生成的完整的 Mapper 文件。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="io.github.dunwu.spring.orm.mapper.UserMapper">
  <resultMap id="BaseResultMap" type="io.github.dunwu.spring.orm.entity.User">
    <id column="id" jdbcType="BIGINT" property="id" />
    <result column="name" jdbcType="VARCHAR" property="name" />
    <result column="age" jdbcType="INTEGER" property="age" />
    <result column="address" jdbcType="VARCHAR" property="address" />
    <result column="email" jdbcType="VARCHAR" property="email" />
  </resultMap>
  <delete id="deleteByPrimaryKey" parameterType="java.lang.Long">
    delete from user
    where id = #{id,jdbcType=BIGINT}
  </delete>
  <insert id="insert" parameterType="io.github.dunwu.spring.orm.entity.User">
    insert into user (id, name, age,
      address, email)
    values (#{id,jdbcType=BIGINT}, #{name,jdbcType=VARCHAR}, #{age,jdbcType=INTEGER},
      #{address,jdbcType=VARCHAR}, #{email,jdbcType=VARCHAR})
  </insert>
  <update id="updateByPrimaryKey" parameterType="io.github.dunwu.spring.orm.entity.User">
    update user
    set name = #{name,jdbcType=VARCHAR},
      age = #{age,jdbcType=INTEGER},
      address = #{address,jdbcType=VARCHAR},
      email = #{email,jdbcType=VARCHAR}
    where id = #{id,jdbcType=BIGINT}
  </update>
  <select id="selectByPrimaryKey" parameterType="java.lang.Long" resultMap="BaseResultMap">
    select id, name, age, address, email
    from user
    where id = #{id,jdbcType=BIGINT}
  </select>
  <select id="selectAll" resultMap="BaseResultMap">
    select id, name, age, address, email
    from user
  </select>
</mapper>
```

#### Mapper.java

Mapper.java 文件是 Mapper.xml 对应的 Java 对象。

【示例】UserMapper.java 文件

```java
public interface UserMapper {

    int deleteByPrimaryKey(Long id);

    int insert(User record);

    User selectByPrimaryKey(Long id);

    List<User> selectAll();

    int updateByPrimaryKey(User record);

}
```

对比 UserMapper.java 和 UserMapper.xml 文件，不难发现：

UserMapper.java 中的方法和 UserMapper.xml 的 CRUD 语句元素（ `<insert>`、`<delete>`、`<update>`、`<select>`）存在一一对应关系。

在 Mybatis 中，正是通过方法的全限定名，将二者绑定在一起。

#### 数据实体.java

【示例】User.java 文件

```java
public class User {
    private Long id;

    private String name;

    private Integer age;

    private String address;

    private String email;

}
```

`<insert>`、`<delete>`、`<update>`、`<select>` 的 `parameterType` 属性以及 `<resultMap>` 的 `type` 属性都可能会绑定到数据实体。这样就可以把 JDBC 操作的输入输出和 JavaBean 结合起来，更加方便、易于理解。

### 1.5. 测试程序

【示例】MybatisDemo.java 文件

```java
public class MybatisDemo {

    public static void main(String[] args) throws Exception {
        // 1. 加载 mybatis 配置文件，创建 SqlSessionFactory
        // 注：在实际的应用中，SqlSessionFactory 应该是单例
        InputStream inputStream = Resources.getResourceAsStream("mybatis/mybatis-config.xml");
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(inputStream);

        // 2. 创建一个 SqlSession 实例，进行数据库操作
        SqlSession sqlSession = factory.openSession();

        // 3. Mapper 映射并执行
        Long params = 1L;
        List<User> list = sqlSession.selectList("io.github.dunwu.spring.orm.mapper.UserMapper.selectByPrimaryKey", params);
        for (User user : list) {
            System.out.println("user name: " + user.getName());
        }
        // 输出：user name: 张三
    }

}
```

> 说明：
>
> `SqlSession` 接口是 Mybatis API 核心中的核心，它代表了 Mybatis 和数据库的一次完整会话。
>
> - Mybatis 会解析配置，并根据配置创建 `SqlSession` 。
> - 然后，Mybatis 将 Mapper 映射为 `SqlSession`，然后传递参数，执行 SQL 语句并获取结果。

## 2. Mybatis 生命周期

![](https://raw.githubusercontent.com/dunwu/images/dev/snap/20210510113446.png)

### 2.1. SqlSessionFactoryBuilder

#### SqlSessionFactoryBuilder 的职责

**`SqlSessionFactoryBuilder` 负责创建 `SqlSessionFactory` 实例**。`SqlSessionFactoryBuilder` 可以从 XML 配置文件或一个预先定制的 `Configuration` 的实例构建出 `SqlSessionFactory` 的实例。

`Configuration` 类包含了对一个 `SqlSessionFactory` 实例你可能关心的所有内容。

![](https://raw.githubusercontent.com/dunwu/images/dev/snap/20210508173040.png)

`SqlSessionFactoryBuilder` 有五个 `build()` 方法，每一种都允许你从不同的资源中创建一个 `SqlSessionFactory` 实例。

```java
SqlSessionFactory build(InputStream inputStream)
SqlSessionFactory build(InputStream inputStream, String environment)
SqlSessionFactory build(InputStream inputStream, Properties properties)
SqlSessionFactory build(InputStream inputStream, String env, Properties props)
SqlSessionFactory build(Configuration config)
```

#### SqlSessionFactoryBuilder 的生命周期

`SqlSessionFactoryBuilder` 可以被实例化、使用和丢弃，一旦创建了 `SqlSessionFactory`，就不再需要它了。 因此 `SqlSessionFactoryBuilder` 实例的最佳作用域是方法作用域（也就是局部方法变量）。你可以重用 `SqlSessionFactoryBuilder` 来创建多个 `SqlSessionFactory` 实例，但最好还是不要一直保留着它，以保证所有的 XML 解析资源可以被释放给更重要的事情。

### 2.2. SqlSessionFactory

#### SqlSessionFactory 职责

**`SqlSessionFactory` 负责创建 `SqlSession` 实例。**

![](https://raw.githubusercontent.com/dunwu/images/dev/snap/20210510105641.png)

`SqlSessionFactory` 的主要方法：

```java
SqlSession openSession()
SqlSession openSession(boolean autoCommit)
SqlSession openSession(Connection connection)
SqlSession openSession(TransactionIsolationLevel level)
SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level)
SqlSession openSession(ExecutorType execType)
SqlSession openSession(ExecutorType execType, boolean autoCommit)
SqlSession openSession(ExecutorType execType, Connection connection)
Configuration getConfiguration();
```

默认的 `openSession()` 方法没有参数，它会创建具备如下特性的 `SqlSession`：

- 事务作用域将会开启（也就是不自动提交）。
- 将由当前环境配置的 `DataSource` 实例中获取 `Connection` 对象。
- 事务隔离级别将会使用驱动或数据源的默认设置。
- 预处理语句不会被复用，也不会批量处理更新。

`TransactionIsolationLevel` 表示事务隔离级别，它对应着 JDBC 的五个事务隔离级别。

`ExecutorType` 枚举类型定义了三个值:

- `ExecutorType.SIMPLE`：该类型的执行器没有特别的行为。它为每个语句的执行创建一个新的预处理语句。
- `ExecutorType.REUSE`：该类型的执行器会复用预处理语句。
- `ExecutorType.BATCH`：该类型的执行器会批量执行所有更新语句，如果 SELECT 在多个更新中间执行，将在必要时将多条更新语句分隔开来，以方便理解。

#### SqlSessionFactory 生命周期

`SqlSessionFactory` 应该以单例形式在应用的运行期间一直存在。

### 2.3. SqlSession

#### SqlSession 职责

**MyBatis 的主要 Java 接口就是 `SqlSession`。它包含了所有执行语句，获取映射器和管理事务的方法。**

> 详细内容可以参考：「 [Mybatis 官方文档之 SqlSessions](http://www.mybatis.org/mybatis-3/zh/java-api.html#sqlSessions) 」 。

SqlSession 类的方法可以按照下图进行大致分类：

![](https://raw.githubusercontent.com/dunwu/images/dev/snap/20210510110638.png)

#### SqlSession 生命周期

`SqlSessions` 是由 `SqlSessionFactory` 实例创建的；而 `SqlSessionFactory` 是由 `SqlSessionFactoryBuilder` 创建的。

> 🔔 注意：当 Mybatis 与一些依赖注入框架（如 Spring 或者 Guice）同时使用时，`SqlSessions` 将被依赖注入框架所创建，所以你不需要使用 `SqlSessionFactoryBuilder` 或者 `SqlSessionFactory`。

**每个线程都应该有它自己的 `SqlSession` 实例。**

`SqlSession` 的实例不是线程安全的，因此是不能被共享的，所以它的最佳的作用域是请求或方法作用域。 绝对不能将 `SqlSession` 实例的引用放在一个类的静态域，甚至一个类的实例变量也不行。 也绝不能将 `SqlSession` 实例的引用放在任何类型的托管作用域中，比如 Servlet 框架中的 `HttpSession`。 正确在 Web 中使用 `SqlSession` 的场景是：每次收到的 HTTP 请求，就可以打开一个 `SqlSession`，返回一个响应，就关闭它。

编程模式：

```java
try (SqlSession session = sqlSessionFactory.openSession()) {
  // 你的应用逻辑代码
}
```

### 2.4. 映射器

#### 映射器职责

映射器是一些由用户创建的、绑定 SQL 语句的接口。

`SqlSession` 中的 `insert`、`update`、`delete` 和 `select` 方法都很强大，但也有些繁琐。更通用的方式是使用映射器类来执行映射语句。**一个映射器类就是一个仅需声明与 `SqlSession` 方法相匹配的方法的接口类**。

下面的示例展示了一些方法签名以及它们是如何映射到 `SqlSession` 上的。

```java
public interface AuthorMapper {
  // (Author) selectOne("selectAuthor",5);
  Author selectAuthor(int id);
  // (List<Author>) selectList(“selectAuthors”)
  List<Author> selectAuthors();
  // (Map<Integer,Author>) selectMap("selectAuthors", "id")
  @MapKey("id")
  Map<Integer, Author> selectAuthors();
  // insert("insertAuthor", author)
  int insertAuthor(Author author);
  // updateAuthor("updateAuthor", author)
  int updateAuthor(Author author);
  // delete("deleteAuthor",5)
  int deleteAuthor(int id);
}
```

> **注意**
>
> - 映射器接口不需要去实现任何接口或继承自任何类。只要方法可以被唯一标识对应的映射语句就可以了。
> - 映射器接口可以继承自其他接口。当使用 XML 来构建映射器接口时要保证语句被包含在合适的命名空间中。而且，唯一的限制就是你不能在两个继承关系的接口中拥有相同的方法签名（潜在的危险做法不可取）。

#### 映射器生命周期

映射器接口的实例是从 `SqlSession` 中获得的。因此从技术层面讲，任何映射器实例的最大作用域是和请求它们的 `SqlSession` 相同的。尽管如此，映射器实例的最佳作用域是方法作用域。 也就是说，映射器实例应该在调用它们的方法中被请求，用过之后即可丢弃。

编程模式：

```java
try (SqlSession session = sqlSessionFactory.openSession()) {
  BlogMapper mapper = session.getMapper(BlogMapper.class);
  // 你的应用逻辑代码
}
```

- **映射器注解**

MyBatis 是一个 XML 驱动的框架。配置信息是基于 XML 的，而且映射语句也是定义在 XML 中的。MyBatis 3 以后，支持注解配置。注解配置基于配置 API；而配置 API 基于 XML 配置。

Mybatis 支持诸如 `@Insert`、`@Update`、`@Delete`、`@Select`、`@Result` 等注解。

> 详细内容请参考：[Mybatis 官方文档之 sqlSessions](http://www.mybatis.org/mybatis-3/zh/java-api.html#sqlSessions)，其中列举了 Mybatis 支持的注解清单，以及基本用法。

## 3.1. MyBatis 的架构

![](https://raw.githubusercontent.com/dunwu/images/dev/snap/20210511161809.png)

### 3.2. 接口层

接口层负责和数据库交互的方式

MyBatis 和数据库的交互有两种方式：

- 使用传统的 MyBatis 提供的 API；
- 使用 Mapper 接口

#### 使用传统的 MyBatis 提供的 API

这是传统的传递 Statement Id 和查询参数给 SqlSession 对象，使用 SqlSession 对象完成和数据库的交互；MyBatis 提供了非常方便和简单的 API，供用户实现对数据库的增删改查数据操作，以及对数据库连接信息和 MyBatis 自身配置信息的维护操作。

上述使用 MyBatis 的方法，是创建一个和数据库打交道的 SqlSession 对象，然后根据 Statement Id 和参数来操作数据库，这种方式固然很简单和实用，但是它不符合面向对象语言的概念和面向接口编程的编程习惯。由于面向接口的编程是面向对象的大趋势，MyBatis 为了适应这一趋势，增加了第二种使用 MyBatis 支持接口（Interface）调用方式。

#### 使用 Mapper 接口

MyBatis 将配置文件中的每一个 `<mapper>` 节点抽象为一个 Mapper 接口，而这个接口中声明的方法和跟 `<mapper>` 节点中的 `<select|update|delete|insert>` 节点相对应，即 `<select|update|delete|insert>` 节点的 id 值为 Mapper 接口中的方法名称，parameterType 值表示 Mapper 对应方法的入参类型，而 resultMap 值则对应了 Mapper 接口表示的返回值类型或者返回结果集的元素类型。

<div align="center">
<img src="http://dunwu.test.upcdn.net/cs/java/javaweb/standalone/orm/mybatis/mybatis两种工作方式之一接口模式.png">
</div>

根据 MyBatis 的配置规范配置好后，通过 `SqlSession.getMapper(XXXMapper.class)` 方法，MyBatis 会根据相应的接口声明的方法信息，通过动态代理机制生成一个 Mapper 实例，我们使用 Mapper 接口的某一个方法时，MyBatis 会根据这个方法的方法名和参数类型，确定 Statement Id，底层还是通过`SqlSession.select("statementId",parameterObject);` 或者 `SqlSession.update("statementId",parameterObject);` 等等来实现对数据库的操作。

MyBatis 引用 Mapper 接口这种调用方式，纯粹是为了满足面向接口编程的需要。（其实还有一个原因是在于，面向接口的编程，使得用户在接口上可以使用注解来配置 SQL 语句，这样就可以脱离 XML 配置文件，实现“0 配置”）。

### 3.3. 数据处理层

数据处理层可以说是 MyBatis 的核心，从大的方面上讲，它要完成两个功能：

1. 通过传入参数构建动态 SQL 语句；
2. SQL 语句的执行以及封装查询结果集成 `List<E>`

#### 参数映射和动态 SQL 语句生成

动态语句生成可以说是 MyBatis 框架非常优雅的一个设计，MyBatis 通过传入的参数值，**使用 Ognl 来动态地构造 SQL 语句**，使得 MyBatis 有很强的灵活性和扩展性。

参数映射指的是对于 java 数据类型和 jdbc 数据类型之间的转换：这里有包括两个过程：查询阶段，我们要将 java 类型的数据，转换成 jdbc 类型的数据，通过 `preparedStatement.setXXX()` 来设值；另一个就是对 resultset 查询结果集的 jdbcType 数据转换成 java 数据类型。

#### SQL 语句的执行以及封装查询结果集成 `List<E>`

动态 SQL 语句生成之后，MyBatis 将执行 SQL 语句，并将可能返回的结果集转换成 `List<E>` 列表。MyBatis 在对结果集的处理中，支持结果集关系一对多和多对一的转换，并且有两种支持方式，一种为嵌套查询语句的查询，还有一种是嵌套结果集的查询。

### 3.4. 框架支撑层

#### 事务管理机制

对数据库的事务而言，应该具有以下几点：创建（create）、提交（commit）、回滚（rollback）、关闭（close）。对应地，MyBatis 将事务抽象成了 Transaction 接口。

MyBatis 的事务管理分为两种形式：

1. 使用 JDBC 的事务管理机制：即利用 java.sql.Connection 对象完成对事务的提交（commit()）、回滚（rollback()）、关闭（close()）等。
2. 使用 MANAGED 的事务管理机制：这种机制 MyBatis 自身不会去实现事务管理，而是让程序的容器如（JBOSS，Weblogic）来实现对事务的管理。

#### 连接池管理机制

由于创建一个数据库连接所占用的资源比较大， 对于数据吞吐量大和访问量非常大的应用而言，连接池的设计就显得非常重要，对于连接池管理机制我已经在我的博文《深入理解 mybatis 原理》 Mybatis 数据源与连接池 中有非常详细的讨论，感兴趣的读者可以点击查看。

#### 缓存机制

MyBatis 将数据缓存设计成两级结构，分为一级缓存、二级缓存：

- **一级缓存是 Session 会话级别的缓存**，位于表示一次数据库会话的 SqlSession 对象之中，又被称之为本地缓存。一级缓存是 MyBatis 内部实现的一个特性，用户不能配置，默认情况下自动支持的缓存，用户没有定制它的权利（不过这也不是绝对的，可以通过开发插件对它进行修改）；
- **二级缓存是 Application 应用级别的缓存**，它的是生命周期很长，跟 Application 的声明周期一样，也就是说它的作用范围是整个 Application 应用。

<div align="center">
<img src="http://dunwu.test.upcdn.net/cs/java/javaweb/standalone/orm/mybatis/mybatis缓存架构示意图.png">
</div>

##### 一级缓存的工作机制

一级缓存是 Session 会话级别的，一般而言，一个 SqlSession 对象会使用一个 Executor 对象来完成会话操作，Executor 对象会维护一个 Cache 缓存，以提高查询性能。

<div align="center">
<img src="http://dunwu.test.upcdn.net/cs/java/javaweb/standalone/orm/mybatis/SqlSession一级缓存的工作流程.png">
</div>

##### 二级缓存的工作机制

如上所言，一个 SqlSession 对象会使用一个 Executor 对象来完成会话操作，MyBatis 的二级缓存机制的关键就是对这个 Executor 对象做文章。如果用户配置了 `"cacheEnabled=true"`，那么 MyBatis 在为 SqlSession 对象创建 Executor 对象时，会对 Executor 对象加上一个装饰者：CachingExecutor，这时 SqlSession 使用 CachingExecutor 对象来完成操作请求。CachingExecutor 对于查询请求，会先判断该查询请求在 Application 级别的二级缓存中是否有缓存结果，如果有查询结果，则直接返回缓存结果；如果缓存中没有，再交给真正的 Executor 对象来完成查询操作，之后 CachingExecutor 会将真正 Executor 返回的查询结果放置到缓存中，然后在返回给用户。

<div align="center">
<img src="http://dunwu.test.upcdn.net/cs/java/javaweb/standalone/orm/mybatis/使用与未使用二级缓存的区别.png">
</div>

#### SQL 语句的配置方式

传统的 MyBatis 配置 SQL 语句方式就是使用 XML 文件进行配置的，但是这种方式不能很好地支持面向接口编程的理念，为了支持面向接口的编程，MyBatis 引入了 Mapper 接口的概念，面向接口的引入，对使用注解来配置 SQL 语句成为可能，用户只需要在接口上添加必要的注解即可，不用再去配置 XML 文件了，但是，目前的 MyBatis 只是对注解配置 SQL 语句提供了有限的支持，某些高级功能还是要依赖 XML 配置文件配置 SQL 语句。

### 3.5. 引导层

引导层是配置和启动 MyBatis 配置信息的方式。MyBatis 提供两种方式来引导 MyBatis ：

1. 基于 XML 配置文件的方式
2. 基于 Java API 的方式

### 3.6. 主要组件

从 MyBatis 代码实现的角度来看，MyBatis 的主要组件有以下几个：

- **SqlSession** - 作为 MyBatis 工作的主要顶层 API，表示和数据库交互的会话，完成必要数据库增删改查功能。
- **Executor** - MyBatis 执行器，是 MyBatis 调度的核心，负责 SQL 语句的生成和查询缓存的维护。
- **StatementHandler** - 封装了 JDBC Statement 操作，负责对 JDBC statement 的操作，如设置参数、将 Statement 结果集转换成 List 集合。
- **ParameterHandler** - 负责对用户传递的参数转换成 JDBC Statement 所需要的参数。
- **ResultSetHandler** - 负责将 JDBC 返回的 ResultSet 结果集对象转换成 List 类型的集合。
- **TypeHandler** - 负责 java 数据类型和 jdbc 数据类型之间的映射和转换。
- **MappedStatement** - MappedStatement 维护了一条 `<select|update|delete|insert>` 节点的封装。
- **SqlSource** - 负责根据用户传递的 parameterObject，动态地生成 SQL 语句，将信息封装到 BoundSql 对象中，并返回。
- **BoundSql** - 表示动态生成的 SQL 语句以及相应的参数信息。
- **Configuration** - MyBatis 所有的配置信息都维持在 Configuration 对象之中。

它们的关系如下图所示：

<div align="center">
<img src="http://dunwu.test.upcdn.net/cs/java/javaweb/standalone/orm/mybatis/mybaits流程图2.png">
</div>
## 4. SqlSession 内部工作机制

从前文，我们已经了解了，MyBatis 封装了对数据库的访问，把对数据库的会话和事务控制放到了 SqlSession 对象中。那么具体是如何工作的呢？接下来，我们通过源码解读来进行分析。

`SqlSession` 对于 insert、update、delete、select 的内部处理机制基本上大同小异。所以，接下来，我会以一次完整的 select 查询流程为例讲解 `SqlSession` 内部的工作机制。相信读者如果理解了 select 的处理流程，对于其他 CRUD 操作也能做到一通百通。

### SqlSession 和 Mapper

先来回忆一下 Mybatis 完整示例章节的 测试程序部分的代码。

MybatisDemo.java 文件中的代码片段：

```java
// 2. 创建一个 SqlSession 实例，进行数据库操作
SqlSession sqlSession = factory.openSession();

// 3. Mapper 映射并执行
Long params = 1L;
List<User> list = sqlSession.selectList("io.github.dunwu.spring.orm.mapper.UserMapper.selectByPrimaryKey", params);
for (User user : list) {
    System.out.println("user name: " + user.getName());
}
```

示例代码中，给 sqlSession 对象的传递一个配置的 Sql 语句的 Statement Id 和参数，然后返回结果

`io.github.dunwu.spring.orm.mapper.UserMapper.selectByPrimaryKey` 是配置在 `UserMapper.xml` 的 Statement ID，params 是 SQL 参数。

UserMapper.xml 文件中的代码片段：

```xml
  <select id="selectByPrimaryKey" parameterType="java.lang.Long" resultMap="BaseResultMap">
    select id, name, age, address, email
    from user
    where id = #{id,jdbcType=BIGINT}
  </select>
```

Mybatis 通过方法的全限定名，将 SqlSession 和 Mapper 相互映射起来。

### SqlSession 和 Executor

`org.apache.ibatis.session.defaults.DefaultSqlSession` 中 `selectList` 方法的源码：

```java
@Override
public <E> List<E> selectList(String statement) {
  return this.selectList(statement, null);
}

@Override
public <E> List<E> selectList(String statement, Object parameter) {
  return this.selectList(statement, parameter, RowBounds.DEFAULT);
}

@Override
public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
  try {
    // 1. 根据 Statement Id，在配置对象 Configuration 中查找和配置文件相对应的 MappedStatement
    MappedStatement ms = configuration.getMappedStatement(statement);
    // 2. 将 SQL 语句交由执行器 Executor 处理
    return executor.query(ms, wrapCollection(parameter), rowBounds, Executor.NO_RESULT_HANDLER);
  } catch (Exception e) {
    throw ExceptionFactory.wrapException("Error querying database.  Cause: " + e, e);
  } finally {
    ErrorContext.instance().reset();
  }
}
```

说明：

MyBatis 所有的配置信息都维持在 `Configuration` 对象之中。中维护了一个 `Map<String, MappedStatement>` 对象。其中，key 为 Mapper 方法的全限定名（对于本例而言，key 就是 `io.github.dunwu.spring.orm.mapper.UserMapper.selectByPrimaryKey` ），value 为 `MappedStatement` 对象。所以，传入 Statement Id 就可以从 Map 中找到对应的 `MappedStatement`。

`MappedStatement` 维护了一个 Mapper 方法的元数据信息，其数据组织可以参考下面的 debug 截图：

![](https://raw.githubusercontent.com/dunwu/images/dev/snap/20210511150650.png)

> 小结：
>
> 通过 "SqlSession 和 Mapper" 以及 "SqlSession 和 Executor" 这两节，我们已经知道：
>
> SqlSession 的职能是：根据 Statement ID, 在 `Configuration` 中获取到对应的 `MappedStatement` 对象，然后调用 `Executor` 来执行具体的操作。
>

### Executor 工作流程

继续上一节的流程，`SqlSession` 将 SQL 语句交由执行器 `Executor` 处理。`Executor` 又做了哪些事儿呢？

（1）执行器查询入口

```java
public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
	// 1. 根据传参，动态生成需要执行的 SQL 语句，用 BoundSql 对象表示
    BoundSql boundSql = ms.getBoundSql(parameter);
    // 2. 根据传参，创建一个缓存Key
    CacheKey key = createCacheKey(ms, parameter, rowBounds, boundSql);
    return query(ms, parameter, rowBounds, resultHandler, key, boundSql);
 }
```

执行器查询入口主要做两件事：

- **生成动态 SQL**：根据传参，动态生成需要执行的 SQL 语句，用 BoundSql 对象表示。
- **管理缓存**：根据传参，创建一个缓存 Key。

（2）执行器查询第二入口

```java
  @SuppressWarnings("unchecked")
  @Override
  public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
    // 略
    List<E> list;
    try {
      queryStack++;
      list = resultHandler == null ? (List<E>) localCache.getObject(key) : null;
      // 3. 缓存中有值，则直接从缓存中取数据；否则，查询数据库
      if (list != null) {
        handleLocallyCachedOutputParameters(ms, key, parameter, boundSql);
      } else {
        list = queryFromDatabase(ms, parameter, rowBounds, resultHandler, key, boundSql);
      }
    } finally {
      queryStack--;
    }
    // 略
    return list;
  }
```

实际查询方法主要的职能是判断缓存 key 是否能命中缓存：

- 命中，则将缓存中数据返回；
- 不命中，则查询数据库：

（3）查询数据库

```java
  private <E> List<E> queryFromDatabase(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
    List<E> list;
    localCache.putObject(key, EXECUTION_PLACEHOLDER);
    try {
      // 4. 执行查询，获取 List 结果，并将查询的结果更新本地缓存中
      list = doQuery(ms, parameter, rowBounds, resultHandler, boundSql);
    } finally {
      localCache.removeObject(key);
    }
    localCache.putObject(key, list);
    if (ms.getStatementType() == StatementType.CALLABLE) {
      localOutputParameterCache.putObject(key, parameter);
    }
    return list;
  }
```

`queryFromDatabase` 方法的职责是调用 doQuery，向数据库发起查询，并将返回的结果更新到本地缓存。

（4）实际查询方法

SimpleExecutor 类的 doQuery()方法实现

```java
  @Override
  public <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
    Statement stmt = null;
    try {
      Configuration configuration = ms.getConfiguration();
      // 5. 根据既有的参数，创建StatementHandler对象来执行查询操作
      StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, resultHandler, boundSql);
      // 6. 创建java.Sql.Statement对象，传递给StatementHandler对象
      stmt = prepareStatement(handler, ms.getStatementLog());
      // 7. 调用StatementHandler.query()方法，返回List结果
      return handler.query(stmt, resultHandler);
    } finally {
      closeStatement(stmt);
    }
  }
```

上述的 Executor.query()方法几经转折，最后会创建一个 StatementHandler 对象，然后将必要的参数传递给 StatementHandler，使用 StatementHandler 来完成对数据库的查询，最终返回 List 结果集。
从上面的代码中我们可以看出，Executor 的功能和作用是：

1. 根据传递的参数，完成 SQL 语句的动态解析，生成 BoundSql 对象，供 StatementHandler 使用；

2. 为查询创建缓存，以提高性能

3. 创建 JDBC 的 Statement 连接对象，传递给 StatementHandler 对象，返回 List 查询结果。

prepareStatement() 方法的实现：

```java
  private Statement prepareStatement(StatementHandler handler, Log statementLog) throws SQLException {
    Statement stmt;
    Connection connection = getConnection(statementLog);
    stmt = handler.prepare(connection, transaction.getTimeout());
    //对创建的Statement对象设置参数，即设置SQL 语句中 ? 设置为指定的参数
    handler.parameterize(stmt);
    return stmt;
  }
```

对于JDBC的PreparedStatement类型的对象，创建的过程中，我们使用的是SQL语句字符串会包含 若干个? 占位符，我们其后再对占位符进行设值。

### StatementHandler 工作流程

StatementHandler 对象负责设置 Statement 对象中的查询参数、处理 JDBC 返回的 resultSet，将 resultSet 加工为 List 集合返回：

```java
@Override
public void parameterize(Statement statement) throws SQLException {
  //使用ParameterHandler对象来完成对Statement的设值
  parameterHandler.setParameters((PreparedStatement) statement);
}

  @Override
  public void setParameters(PreparedStatement ps) {
    ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    if (parameterMappings != null) {
      for (int i = 0; i < parameterMappings.size(); i++) {
        ParameterMapping parameterMapping = parameterMappings.get(i);
        if (parameterMapping.getMode() != ParameterMode.OUT) {
          Object value;
          String propertyName = parameterMapping.getProperty();
          if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
            value = boundSql.getAdditionalParameter(propertyName);
          } else if (parameterObject == null) {
            value = null;
          } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
            value = parameterObject;
          } else {
            MetaObject metaObject = configuration.newMetaObject(parameterObject);
            value = metaObject.getValue(propertyName);
          }
            
          // 每一个Mapping都有一个TypeHandler，根据TypeHandler来对preparedStatement进行设置参数
          TypeHandler typeHandler = parameterMapping.getTypeHandler();
          JdbcType jdbcType = parameterMapping.getJdbcType();
          if (value == null && jdbcType == null) {
            jdbcType = configuration.getJdbcTypeForNull();
          }
          try {
            typeHandler.setParameter(ps, i + 1, value, jdbcType);
          } catch (TypeException | SQLException e) {
            throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
          }
        }
      }
    }
  }
```

ParameterHandler的setParameters(Statement)方法负责根据我们输入的参数，对statement对象的 ? 占位符处进行赋值。

```java
@Override
public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
  PreparedStatement ps = (PreparedStatement) statement;
  ps.execute();
  // 使用ResultHandler来处理ResultSet
  return resultSetHandler.handleResultSets(ps);
}
```

### ResultSetHandler

```java
@Override
public List<Object> handleResultSets(Statement stmt) throws SQLException {
  ErrorContext.instance().activity("handling results").object(mappedStatement.getId());

  final List<Object> multipleResults = new ArrayList<>();

  int resultSetCount = 0;
  ResultSetWrapper rsw = getFirstResultSet(stmt);

  List<ResultMap> resultMaps = mappedStatement.getResultMaps();
  int resultMapCount = resultMaps.size();
  validateResultMapsCount(rsw, resultMapCount);
  while (rsw != null && resultMapCount > resultSetCount) {
    ResultMap resultMap = resultMaps.get(resultSetCount);
    handleResultSet(rsw, resultMap, multipleResults, null);
    rsw = getNextResultSet(stmt);
    cleanUpAfterHandlingResultSet();
    resultSetCount++;
  }

  String[] resultSets = mappedStatement.getResultSets();
  if (resultSets != null) {
    while (rsw != null && resultSetCount < resultSets.length) {
      ResultMapping parentMapping = nextResultMaps.get(resultSets[resultSetCount]);
      if (parentMapping != null) {
        String nestedResultMapId = parentMapping.getNestedResultMapId();
        ResultMap resultMap = configuration.getResultMap(nestedResultMapId);
        handleResultSet(rsw, resultMap, null, parentMapping);
      }
      rsw = getNextResultSet(stmt);
      cleanUpAfterHandlingResultSet();
      resultSetCount++;
    }
  }

  return collapseSingleResultList(multipleResults);
}
```

## 5. 参考资料

- **官方**
  - [Mybatis Github](https://github.com/mybatis/mybatis-3)
  - [Mybatis 官网](http://www.mybatis.org/mybatis-3/)
  - [MyBatis Generator](https://github.com/mybatis/generator)
  - [Spring 集成](https://github.com/mybatis/spring)
  - [Spring Boot 集成](https://github.com/mybatis/spring-boot-starter)
- **扩展插件**
  - [mybatis-plus](https://github.com/baomidou/mybatis-plus) - CRUD 扩展插件、代码生成器、分页器等多功能
  - [Mapper](https://github.com/abel533/Mapper) - CRUD 扩展插件
  - [Mybatis-PageHelper](https://github.com/pagehelper/Mybatis-PageHelper) - Mybatis 通用分页插件
- **文章**
  - [深入理解 mybatis 原理](https://blog.csdn.net/luanlouis/article/details/40422941)
  - [mybatis 源码中文注释](https://github.com/tuguangquan/mybatis)
  - [MyBatis Generator 详解](https://blog.csdn.net/isea533/article/details/42102297)
  - [Mybatis 常见面试题](https://juejin.im/post/5aa646cdf265da237e095da1)
  - [Mybatis 中强大的 resultMap](https://juejin.im/post/5cee8b61e51d455d88219ea4)