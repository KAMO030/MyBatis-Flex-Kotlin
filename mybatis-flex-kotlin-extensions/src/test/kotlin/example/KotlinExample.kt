package com.mybatisflex.kotlin.example

import com.mybatisflex.core.activerecord.Model
import com.mybatisflex.core.audit.AuditManager
import com.mybatisflex.core.audit.ConsoleMessageCollector
import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.kotlin.example.entity.Account
import com.mybatisflex.kotlin.example.mapper.AccountMapper
import com.mybatisflex.kotlin.extensions.condition.allAnd
import com.mybatisflex.kotlin.extensions.condition.and
import com.mybatisflex.kotlin.extensions.condition.or
import com.mybatisflex.kotlin.extensions.db.*
import com.mybatisflex.kotlin.extensions.kproperty.*
import com.mybatisflex.kotlin.extensions.mapper.*
import com.mybatisflex.kotlin.extensions.model.batchDeleteById
import com.mybatisflex.kotlin.extensions.model.batchInsert
import com.mybatisflex.kotlin.extensions.model.batchUpdateById
import com.mybatisflex.kotlin.extensions.sql.orderBy
import com.mybatisflex.kotlin.extensions.wrapper.*
import com.mybatisflex.kotlin.scope.runFlex
import org.apache.ibatis.logging.stdout.StdOutImpl
import org.junit.jupiter.api.Test
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import java.time.Instant
import java.util.*
import javax.sql.DataSource
import kotlin.streams.toList

class KotlinExample {

    private val dataSource: DataSource = EmbeddedDatabaseBuilder().run {
        setType(EmbeddedDatabaseType.H2)
        addScript("schema.sql")
        addScript("data-kt.sql")
        build()
    }

    private val start: Date = Date.from(Instant.parse("2020-01-10T00:00:00Z"))
    private val end: Date = Date.from(Instant.parse("2020-01-12T00:00:00Z"))

    init {
        runFlex {
//            此方法体 it 是 MybatisFlexBootstrap 实例
//            配置Mapper
//            1.通过+（重写自增）的方式
            +AccountMapper::class
//            2.通过原始的方式
//            it.addMapper(AccountMapper::class.java)
//            3.通过扫描包路径自动注册
//            接口需要继承BaseMapper或打上@Mapper注解, 开启@Mapper注解扫描需要修改第二个参数（needScanAnnotated）为true
//            scanPackages("com.mybatisflex.kotlin.example.mapper")

//            配置单dataSource
//            1.通过+（重写自增）的方式
            +dataSource
//            2.通过原始的方式
//            it.setDataSource(dataSource)
//            3.通过dsl的方式配置简易的内置数据源
//            defaultPooledDataSources {
//                driver可以不写，默认为第一个注册的驱动
//                driver = com.mysql.cj.jdbc.Driver::class
//                url = "xxx"
//                username = "xxx"
//                password = "xxx"
//            }

//            配置多dataSource
//            1.通过of（中缀）的方式
//            FlexConsts.NAME of dataSource
//            "dataSource1" of dataSource
//            "dataSource2" of dataSource
//            2.通过dsl的方式配置简易的内置数据源
//            defaultPooledDataSources("name") {
//                driver可以不写，默认为第一个注册的驱动
//                driver = com.mysql.cj.jdbc.Driver::class
//                url = "xxx"
//                username = "xxx"
//                password = "xxx"
//            }
//          3.通过原始的方式
//            it.addDataSource(FlexConsts.NAME, dataSource)

//          配置日志打印在控制台
            logImpl = StdOutImpl::class
        }

        // 开启sql审计，设置为打印在控制台
        AuditManager.setAuditEnable(true)
        AuditManager.setMessageCollector(ConsoleMessageCollector())
    }

    /**
     * 对比原生
     */
    @Test
    fun contrastOriginal() {
        // 【原生】
        val queryWrapper = QueryWrapper.create()
            .select(QueryColumn("id"), QueryColumn("user_name"))
            .where(QueryColumn("age").isNotNull()).and(QueryColumn("age").ge(17))
            .orderBy(QueryColumn("id").desc())
        mapper<AccountMapper>().selectListByQuery(queryWrapper)
        // 【扩展后】
        // 无需注册Mapper与APT/KSP即可查询操作
        query<Account> {
            select(Account::id, Account::userName)
            where(Account::age.isNotNull) and { Account::age ge 17 } orderBy -Account::id
        }
    }

    /**
     * all: 查泛型对应的表的所有数据
     */
    @Test
    fun testAll() {
        val accounts: List<Account> = all()
        accounts.forEach(::println)
        // 或者 Account::class.all.forEach(::println) (需要注册Mapper接口)
    }

    @Test
    fun testInsert() {
        save<Account> {
            id = 3
            userName = "kamo"
            age = 20
            birthday = Date()
        }
//        insert(Account(3, "kamo", 20, Date()))
//        Account::class.baseMapper.insert(Account(3, "kamo", 20, Date()))
        filterOne<Account> { Account::id eq 3 }?.also(::println)
    }

    @Test
    fun testUpdate() {
        // 通过条件查询到后更新
        filterOne<Account> { Account::id eq 2 }?.apply { age = 20 }?.update {
            Account::userName eq it.userName and (Account::age le 18)
        }
        // 通过id更新
        // filterOne<Account> { Account::id eq 2 }?.apply { age = 20 }?.updateById()
        filterOne<Account> { Account::id eq 2 }?.also(::println)
    }

    @Test
    fun testUpdate2() {
        println("更新前: ${all<Account>().first()}")
        filterOne<Account>(Account::age) {
            Account::age `in` (19..20)
        }
        update<Account> {
            Account::id set 5
//            Account::age setRaw {
//                select(Account::age)
//                from(Account::class)
//                this.and(Account::age `in` (19..20))
//                limit(1)
//            }
//            或者写成:
            Account::age.setRaw(Account::age) {
                from(Account::class)
                and(Account::age `in` (19..20))
            }
//            或者写成:
//            Account::age set queryScope(Account::age.column){
//                from(Account::class)
//                and(Account::age `in` (19..20))
//                limit(1)
//            }
//            或者写成 (此时会执行两次sql):
//            Account::age set filterOne<Account>(Account::age){
//                Account::age `in` (19..20)
//            }?.age
            whereWith { Account::id eq 1 and (Account::userName eq "张三") }
        }
//        val account = Account(
//            id = 5,
//            // 此时会执行一次sql
//            age = filterOne<Account>(Account::age) {
//                Account::age `in` (19..20)
//            }?.age
//        )
//        Account::class.baseMapper.updateByCondition(account){
//            Account::age `in` (19..20)
//        }
        println("更新后: ${all<Account>().first()}")
    }

    @Test
    fun testDelete() {
        // 根据返回的条件删除
        deleteWith<Account> { Account::id eq 2 }
        // 根据主键删除
        deleteById<Account>(2)
        // 通过map的key对应的字段比较删除
        deleteByMap(Account::id to 2)
        // 根据aseMapper删除 (需要注册Mapper接口))
        //  mapper<AccountMapper>().deleteByCondition { Account::id eq 2 }
        // 根据Model的id删除 (需要注册Mapper接口))
        // Account(id = 2).removeById()
        all<Account>().forEach(::println)
    }

    /**
     * filter: 按条件查泛型对应的表的数据
     */
    @Test
    fun testFilter() {
        val accounts: List<Account> = filter {
            allAnd(
                Account::id.isNotNull,
                (Account::id to Account::userName to Account::age).inTriple(
                    1 to "张三" to 18,
                    2 to "李四" to 19
                ),
                Account::age.`in`(17..19)
            ) or { Account::birthday between (start to end) }
        }
        accounts.forEach(::println)
    }

    /**
     * filter: 按条件查泛型对应的表的一条数据
     */
    @Test
    fun testFilterOne() {
        val account: Account? = filterOne(Account::age) {
            allAnd(
                Account::id.isNotNull,
                (Account::id to Account::userName to Account::age).inTriple(
                    1 to "张三" to 18,
                    2 to "李四" to 19
                ),
                Account::age.`in`(17..19)
            ) or { Account::birthday between (start to end) }
        }
        println(account)
    }

    /**
     * query: 较复杂查泛型对应的表的数据,如分组排序等
     */
    @Test
    fun testQuery() {
        val accounts: List<Account> = query {
            selectFrom(Account::id, Account::userName)
            whereWith {
                Account::age `in` (17..19) and (Account::birthday between (start to end))
            } orderBy -Account::id
            limit(2)
        }
        accounts.forEach(::println)
    }

    @Test
    fun testDb() {
        // 查询表对象对应的实体数据并根据条件过滤
        filter<Account> {
            (Account::age eq 12)
                // or第一个参数为true时则会调用花括号类的方法返回一个条件对象与上面那个条件对象相连接
                .or(true) { Account::id between (1 to 2) }
            // 可以用以下方法替代
            // or(`if`(true) { Account::id between (1 to 2 })
        }.stream().peek(::println)
            // 过滤后修改id再次保存
            .peek { it.id = it.id.plus(2) }.forEach(Model<*>::save)


        println("保存后————————")
        // 获得mapper实例通过自定义的默认方法查，并将查到的删除
        mapper<AccountMapper>().findByAge(18, 1).stream().peek { println(it) }.forEach { it.removeById() }

        println("删除后————————")
        all<Account>().stream().peek { println(it) }.map {
            it.userName = "kamo"
            it
        }.forEach { it.updateById() }
        println("更新后————————")

        all<Account>().stream().peek { println(it) }.map {
            it.id = it.id.plus(5)
            it.userName = "akino"
            it
        }.toList().batchInsert()

        println("批量插入后————————")
        all<Account>().stream().peek { println(it) }.toList().filter { it.id.rem(2) == 0 }.batchDeleteById()

        println("批量删除后————————")
        // 直接使用函数查询时需指定from表
        query<Account> { from(Account::class) }.stream().peek { println(it) }.toList().filter { it.id.rem(3) == 0 }
            .map {
                it.userName = "cloud-player"
                it
            }.batchUpdateById()

        println("批量更新后————————")
        all<Account>().forEach(::println)
    }

    @Test
    fun testPaginate() {
        paginate<Account>(1, 10) {
            select(Account::id, Account::userName)
            orderBy(-Account::id)
        }.let {
            println("${it.pageNumber} - ${it.pageSize} - ${it.totalRow}")
            it.records.forEach(::println)
        }
//        paginateWith<Account>(1, 10) {
//            Account::id between (1 to 2)
//        }.let {
//            println("pageNumber: ${it.pageNumber} - pageSize: ${it.pageSize} - totalRow: ${it.totalRow}")
//            it.records.forEach(::println)
//        }
    }

    @Test
    fun testModelQuery() {
        // from AccountMapper
        Account.findByAge(18, 1).forEach(::println)
        // from Account
        Account.findByAge2(18, 1).forEach(::println)
        // from BaseMapper
        Account.selectListByCondition(Account::age eq 18 and Account::id.`in`(1)).forEach(::println)
    }

    @Test
    fun testAllCondition() {
        query<Account> {
//          andAll:
            andAll(
                Account::id eq 1,
                Account::age eq 18,
                Account::userName eq "张三",
            )
//            or
//            (Account::id eq 1).andAll(
//                Account::age eq 18,
//                Account::userName eq "张三",
//            )

//            orAll:
//            orAll(
//                Account::id eq 1,
//                Account::age `in` (17..20),
//                Account::userName eq "张三",
//            )
//            or
//            (Account::id eq 1).orAll(
//                Account::age `in` (17..20),
//                Account::userName eq "张三",
//            )

        }.also { println(it) }
    }


}
