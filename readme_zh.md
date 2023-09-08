# 基于 Kotlin 扩展 Mybatis-Flex

> MyBatis-Flex-Kotlin 是一个 [Mybatis-Flex](https://mybatis-flex.com) 框架的扩展模块，
> 它继承了 Mybatis-Flex 轻量的特性，同时拥有 Kotlin 特有的扩展方法、中缀表达式与DSL等语法支持，
> 使其拥有了更高的灵活性。让我们可以更加轻松的在 Kotlin 中使用 Mybaits-Flex 所带来的开发效率和开发体验。

## 特点

- 轻量：只基于 Mybatis-Flex 核心库 ，只做扩展不做改变
- 简明：使用 DSL 让查询语句更加简单明了
- 快捷：结合 Kotlin 特性快速对数据库进行操作

## 亮点

- 快速构建启动：通过DSL➕重载运算符，快速配置 MybatisFlexBootstrap 实例并启动：
    ```kotlin
    buildBootstrap {
        // 配置数据源 相当于 setDataSource(dataSource)
        +dataSource
        // 配置Mapper 相当于 addMapper(AccountMapper::class.java)
        +AccountMapper::class
        // 配置日志输出 相当于 setLogImpl(StdOutImpl::class.java)
        logImpl = StdOutImpl::class
      }.start()
    ```
- 快速查询数据：通过DSL➕泛型快速编写查询语句并查询:  (快速查询提供两个函数：all, filter 和 query )
  - `all<Account>()` 查泛型对应的表的所有数据
  - `filter<Account>(vararg columns: QueryColumn?, condition: ()->QueryCondition)` 按条件查泛型对应的表的数据
  - `query<Account>(queryScope: QueryScope.()->Unit)` 较复杂查泛型对应的表的数据,如分组排序等
- 简明地构建条件：通过中缀表达式➕扩展方法能更加简单明了的构建条件:
  * **【原生方式】**
    ```kotlin
    val queryWrapper = QueryWrapper.create()
            .select(Account::id.column(), Account::userName.column())
            .where(Account::age.column().`in`(17, 18, 19))
            .orderBy(Account::id.column().desc())
    mapper<AccountMapper>().selectListByQuery(queryWrapper)
    ```
  * **【扩展方式】**
    ```kotlin
    query<Account> {
      select { listOf(Account::id, Account::userName) }
      where { Account::age `in` (17..19) } orderBy -Account::id
    }
    ```
- 摆脱APT: 使用扩展方法摆脱对 APT(注解处理器) 的使用,直接使用属性引用让代码更加灵活优雅:
  >  使用APT: `ACCOUNT.ID eq 1` ,使用属性引用: `Account::id eq 1`
  >  (少依赖一个模块且不用开启注解处理器功能)
- 属性类型约束：使用泛型➕扩展方法对操作的属性进行类型约束:
  > 如: Account 中 age 属性为 Int 类型,那么 `Account::age between (17 to 19)`而 `Account::age between ("17" to "19")`则会报错提醒

## 快速开始

在开始之前，我们假定您已经：

- 熟悉 Kotlin 环境配置及其开发
- 熟悉 关系型 数据库，比如 MySQL
- 熟悉 Kotlin 构建工具，比如 Gradle、Maven

> 本文档涉及到的 [演示源码](https://gitee.com/mybatis-flex/mybatis-flex-kotlin/tree/main/src/test/kotlin/com/mybatisflex/kotlin/test) 已经全部上传
> 在开始之前，您也可以先下载到本地，导入到 idea 开发工具后，在继续看文档。
>
> 在 [Mybatis-Flex源项目](https://mybatis-flex.com) 中所介绍的功能本文档不再过多赘述建议，本文档只对 Mybatis-Flex 在 Kotlin 中特有的用法进行介绍

### Hello World 文档

**第 1 步：创建 Kotlin 项目，并添加 Kotlin 的扩展依赖**

>如何创建 Kotlin 项目可参考 [Kotlin官方文档](https://www.kotlincn.net/docs/tutorials/jvm-get-started.html)

需要添加的主要依赖：

**【Kotlin】**
```kotlin
dependencies {
    //kt扩展库
    implementation("com.mybatis-flex:mybatis-flex-kotlin:1.0")
    //核心库
    implementation("com.mybatis-flex:mybatis-flex-core:$version")
}
```

**【Maven】**

```xml
<dependencies>
    <!--kt扩展库-->
    <dependency>
        <groupId>com.mybatis-flex</groupId>
        <artifactId>mybatis-flex-kotlin</artifactId>
        <version>1.0</version>
    </dependency>
    <!--核心库-->
    <dependency>
        <groupId>com.mybatis-flex</groupId>
        <artifactId>mybatis-flex-core</artifactId>
        <version>${mybatis-flex-core.version}</version>
    </dependency>
</dependencies>
```

**第 2 步：创建数据库表与配置数据源**

> 请参考 [源项目快速开始](https://mybatis-flex.com/zh/intro/getting-started.html) 创建数据库表与配置数据源，
> 或者使用演示源码中的内嵌数据库快速体验

**第 3 步：编写实体类**

```kotlin
@Table("tb_account")
class Account {

     @Id
     var id: Long
     var userName: String
     var age: Integer
     var birthday: Date

}
```

- 使用 `@Table("tb_account")` 设置实体类与表名的映射关系
- 使用 `@Id` 标识主键

**第 4 步：开始使用**

添加测试类，进行功能测试：

```kotlin
fun main() {
  //加载数据源(为了方便演示这里使用了演示源码中的内嵌数据源)
  val dataSource: DataSource = EmbeddedDatabaseBuilder().run {
    setType(EmbeddedDatabaseType.H2)
    addScript("schema.sql")
    addScript("data-kt.sql")
    build()
  }
  //启动并配入数据源
  buildBootstrap { +dataSource }.start()
  val start = Date.from(Instant.parse("2020-01-10T00:00:00Z"))
  val end = Date.from(Instant.parse("2020-01-12T00:00:00Z"))
  //条件过滤查询并打印
  filter<Account> {
    Account::id eq 1 and
            (Account::age `in` (17..19) or (Account::birthday between (start to end)))
  }.forEach(::println)
  //查询全部数据并打印
  //all<Account>().forEach(::println)
}
```
执行的SQL：
```sql
SELECT * FROM `tb_account` WHERE `id` = 1 AND (`age` IN (17, 18, 19) OR `birthday` BETWEEN  '2020-01-10 08:00:00' AND '2020-01-12 08:00:00' )
```
控制台输出：

```txt
Account(id=1, userName=张三, birthday=2020-01-11 00:00:00.0, age=18)
```

## 更多使用

- 功能 1：[Bootstrap简化配置](docs/bootstrapExt.md)
- 功能 2：[简单查询与扩展]()
- 功能 3：[矢量查询](docs/vecSimple.md) (实验性)

[comment]: <> (###### TODO ...)
