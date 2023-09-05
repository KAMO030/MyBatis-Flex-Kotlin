# 基于 Kotlin 扩展 Mybatis-Flex

## 特点

- 本模块基于 [Mybatis-Flex](https://mybatis-flex.com) 核心库 ，只做扩展不做改变
- 结合 Kotlin 特性、DSL让数据库操作更简单

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
    implementation("com.mybatis-flex:mybatis-flex-kotlin:$version")
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
        <version>${mybatis-flex-kotlin.version}</version>
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
- 功能 3：[矢量查询](docs/vecSimple.md)

[comment]: <> (###### TODO ...)
