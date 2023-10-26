package com.mybatisflex.kotlin.example

import com.mybatisflex.core.activerecord.Model
import com.mybatisflex.core.audit.AuditManager
import com.mybatisflex.core.audit.ConsoleMessageCollector
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.kotlin.example.entity.Account
import com.mybatisflex.kotlin.example.mapper.AccountMapper
import com.mybatisflex.kotlin.extensions.condition.and
import com.mybatisflex.kotlin.extensions.condition.or
import com.mybatisflex.kotlin.extensions.db.all
import com.mybatisflex.kotlin.extensions.db.filter
import com.mybatisflex.kotlin.extensions.db.mapper
import com.mybatisflex.kotlin.extensions.db.query
import com.mybatisflex.kotlin.extensions.kproperty.*
import com.mybatisflex.kotlin.extensions.model.batchDeleteById
import com.mybatisflex.kotlin.extensions.model.batchInsert
import com.mybatisflex.kotlin.extensions.model.batchUpdateById
import com.mybatisflex.kotlin.extensions.sql.orderBy
import com.mybatisflex.kotlin.extensions.wrapper.and
import com.mybatisflex.kotlin.extensions.wrapper.from
import com.mybatisflex.kotlin.scope.buildBootstrap
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
        buildBootstrap {
//            此方法体 it 是 MybatisFlexBootstrap 实例
//            配置Mapper
//            1.通过+（重写自增）的方式
            +AccountMapper::class
//            2.通过原始的方式
//            it.addMapper(AccountMapper::class.java)

//            配置单dataSource
//            1.通过+（重写自增）的方式
            +dataSource
//            2.通过原始的方式
//            it.setDataSource(dataSource)

//            配置多dataSource
//            1.通过of（中缀）的方式
//            FlexConsts.NAME of dataSource
//            "dataSource1" of dataSource
//            "dataSource2" of dataSource
//            2.通过dsl（中缀）的方式
            dataSources {
//            dataSource(FlexConsts.NAME,dataSource)
//            dataSource("dataSource1",dataSource)
//            dataSource("dataSource2",dataSource)
            }
//          3.通过原始的方式
//          it.addDataSource(FlexConsts.NAME,dataSource)

//          配置日志打印在控制台
            logImpl = StdOutImpl::class
        }.start()

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
            .select(Account::id.column(), Account::userName.column())
            .where(Account::age.column().isNotNull()).and(Account::age.column().ge(17))
            .orderBy(Account::id.column().desc())
        mapper<AccountMapper>().selectListByQuery(queryWrapper)
        // 【扩展后】
        // 无需注册Mapper即可查询操作
        val accountList: List<Account> = query {
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

    /**
     * filter: 按条件查泛型对应的表的数据
     */
    @Test
    fun testFilter() {
        val accounts: List<Account> = filter(Account::class.allColumns) {
            (Account::id.isNotNull)
                .and {
                    (Account::id to Account::userName to Account::age).inTriple(
                        1 to "张三" to 18,
                        2 to "李四" to 19,
                    )
                }
                .and(Account::age.`in`(17..19) or { Account::birthday between (start to end) })
        }
        accounts.forEach(::println)
    }

    /**
     * query: 较复杂查泛型对应的表的数据,如分组排序等
     */
    @Test
    fun testQuery() {
        val accounts: List<Account> = query {
            select(Account::id, Account::userName)
            where {
                and(Account::age `in` (17..19))
                and(Account::birthday between (start to end))
            } orderBy -Account::id
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

}