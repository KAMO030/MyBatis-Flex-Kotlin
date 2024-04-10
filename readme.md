# 基于 Kotlin 扩展 Mybatis-Flex
**MyBatis-Flex-Kotlin 基于 [Mybatis-Flex](https://mybatis-flex.com) 的 Kotlin 扩展模块，方便 Kotlin 开发者使用 MyBatis-Flex 进行开发**
> 它继承了 Mybatis-Flex 轻量的特性，同时拥有 Kotlin 特有的扩展方法、中缀表达式与DSL等语法支持，
> 使其拥有了更高的灵活性。让我们可以更加轻松的在 Kotlin 中使用 Mybaits-Flex 所带来的开发效率和开发体验。

## 特征

- 轻量：只基于 Mybatis-Flex 核心库 ，只做扩展不做改变
- 简明：使用 DSL 让查询语句更加简单明了
- 快捷：结合 Kotlin 特性快速对数据库进行操作

## 亮点

- 快速构建启动：通过DSL➕重载运算符，快速配置 MybatisFlexBootstrap 实例并启动：
    ```kotlin
    runFlex {
        // 配置数据源 相当于 setDataSource(dataSource)
        +dataSource
        // 配置Mapper 相当于 addMapper(AccountMapper::class.java)
        +AccountMapper::class
        // 配置日志输出 相当于 setLogImpl(StdOutImpl::class.java)
        logImpl = StdOutImpl::class
      }
    ```
- 快速查询数据：通过DSL➕泛型快速编写查询语句并查询:  (快速查询提供三个函数：all, filter 和 query )
  >- `all<实体类>()` 查泛型对应的表的所有数据
  >- `filter<实体类>(vararg KProperty<*>, ()->QueryCondition)` 按条件查泛型对应的表的数据
  >- `query<实体类>(QueryScope.()->Unit)` 较复杂查泛型对应的表的数据 (如: 分组,排序等)

- 简明地构建查询：通过中缀表达式➕扩展方法能更加简单明了的构建条件:

  * **【对比原生】**
    * **原生**
      ```kotlin
        val queryWrapper = QueryWrapper.create()
            .select(QueryColumn("id"), QueryColumn("user_name"))
            .where(QueryColumn("age").isNotNull()).and(QueryColumn("age").ge(17))
            .orderBy(QueryColumn("id").desc())
        mapper<AccountMapper>().selectListByQuery(queryWrapper)
      ```

    * **扩展后**
      ```kotlin
        // 无需注册Mapper与APT/KSP即可查询操作
        val accountList: List<Account> = query {
            select(Account::id, Account::userName)
            where(Account::age.isNotNull) and { Account::age ge 17 } orderBy -Account::id
        }
      ```
    执行的SQL:
    ```sql
     SELECT `id`, `user_name` FROM `tb_account` WHERE `age` IS NOT NULL  AND `age` >= 17 ORDER BY `id` DESC
    ```

  * **【条件优化】**
    - 例如: 查询属性是否在一个连续的区间时，会自动将 IN 转为 BETWEEN 调用
      ```kotlin
       filter<Account> { Account::age `in` (17..19) }
      ```
      执行的SQL:
      ```sql
      SELECT * FROM `tb_account` WHERE `age` BETWEEN  17 AND 19
      ```
    - 例如: 构建多属性组合 IN (最多支持三个属性)
      ```kotlin
      filter<Account> {
        (Account::id to Account::userName to Account::age).inTriple(
            1 to "张三" to 18,
            2 to "李四" to 19,
        )
      }
      ```
      执行的SQL:
      ```sql
      SELECT * FROM `tb_account`
      WHERE (`id` = 1 AND `user_name` = '张三' AND `age` = 18) 
         OR (`id` = 2 AND `user_name` = '李四' AND `age` = 19)
      ```
- 摆脱APT: 使用扩展方法摆脱对 APT(注解处理器) 的使用,直接使用属性引用让代码更加灵活优雅:
  >  使用APT: `ACCOUNT.ID eq 1` ,使用属性引用: `Account::id eq 1`
  >
  >  (少依赖一个模块且不用开启注解处理器功能)
- 属性类型约束：使用泛型➕扩展方法对操作的属性进行类型约束:
  > 如: Account 中 age 属性为 Int 类型
  >
  > 那么使用between时后续参数也必须是Int： `Account::age between (17 to 19)`
  >
  > 而如果写成String：`Account::age between ("17" to "19")`则会报错提醒


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
  //kotlin扩展库
  implementation("com.mybatis-flex:mybatis-flex-kotlin-extensions:1.0.8")
  //核心库
  implementation("com.mybatis-flex:mybatis-flex-core:$version")
}
```

**【Maven】**

```xml
<dependencies>
  <!--kotlin扩展库-->
  <dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-kotlin-extensions</artifactId>
    <version>1.0.7</version>
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
data class Account(
  @Id var id: Int = -1,
  var userName: String? = null,
  var age: Int? = null,
  var birthday: Date? = null,
)
```

- 使用 `@Table("tb_account")` 设置实体类与表名的映射关系
- 使用 `@Id` 标识主键

> ⚠️ 最好不要写成 data class ，否则没有无参构造某些情况下会报错，例如属性的顺序与数据库字段不一致会导致报错；
>
> 如有需要可以安装官方 [noArg](https://kotlinlang.org/docs/no-arg-plugin.html) 插件

**第 4 步：开始使用**

添加测试类，进行功能测试：

```kotlin
  fun main() {
  // 加载数据源(为了方便演示这里使用了演示源码中的内嵌数据源)
  val dataSource: DataSource = EmbeddedDatabaseBuilder().run {
    setType(EmbeddedDatabaseType.H2)
    addScript("schema.sql")
    addScript("data-kt.sql")
    build()
  }
  // 启动并配入数据源
  runFlex { +dataSource }
  val start = Date.from(Instant.parse("2020-01-10T00:00:00Z"))
  val end = Date.from(Instant.parse("2020-01-12T00:00:00Z"))
  // 查泛型对应的表的所有数据
  all<Account>().forEach(::println)
  // 条件过滤查询并打印
  filter<Account> {
    (Account::id.isNotNull)
      .and { (Account::id to Account::userName).inPair(1 to "张三", 2 to "李四") }
      .and(Account::age.`in`(17..19) or { Account::birthday between (start to end) })
  }.forEach(::println)
}
```
执行的SQL：
```sql
  SELECT `age`, `birthday`, `id`, `user_name`
  FROM `tb_account`
```
```sql
  SELECT `age`, `birthday`, `id`, `user_name`
  FROM `tb_account`
  WHERE `id` IS NOT NULL
    AND (`id` = 1 AND `user_name` = '张三' OR (`id` = 2 AND `user_name` = '李四'))
    AND (`age` BETWEEN 17 AND 19 OR `birthday` BETWEEN '2020-01-10 08:00:00' AND '2020-01-12 08:00:00')
```
控制台输出：

```js
Account(id=1, userName="张三", birthday="2020-01-11 00:00:00.0", age=18)
Account(id=2, userName="李四", birthday="2021-03-21 00:00:00.0", age=19)
```

## 更多使用

- 功能 1：[Bootstrap简化配置](docs/bootstrapExt.md)
- 功能 2：[简单查询与扩展](docs/extensions.md)
- 功能 3：[向量查询](docs/vecSimple.md) (实验性)
- 功能 4：[KSP](docs/ksp.md)

[comment]: <> (###### TODO ...)
