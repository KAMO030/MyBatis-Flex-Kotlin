/*
 *  Copyright (c) 2023-Present, Mybatis-Flex-Kotlin (837080904@qq.com).
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.mybatisflex.kotlin.test

import com.mybatisflex.core.activerecord.Model
import com.mybatisflex.core.audit.AuditManager
import com.mybatisflex.core.audit.ConsoleMessageCollector
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.kotlin.extensions.db.filter
import com.mybatisflex.kotlin.extensions.db.mapper
import com.mybatisflex.kotlin.extensions.db.query
import com.mybatisflex.kotlin.extensions.kproperty.*
import com.mybatisflex.kotlin.extensions.model.*
import com.mybatisflex.kotlin.extensions.sql.*
import com.mybatisflex.kotlin.extensions.wrapper.from
import com.mybatisflex.kotlin.scope.buildBootstrap
import com.mybatisflex.kotlin.test.entity.Account
import com.mybatisflex.kotlin.test.mapper.AccountMapper
import org.apache.ibatis.logging.stdout.StdOutImpl
import org.junit.jupiter.api.Test
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import java.time.Instant
import java.util.*
import javax.sql.DataSource
import kotlin.streams.toList


open class KotlinExample {

    val dataSource: DataSource = EmbeddedDatabaseBuilder().run {
        setType(EmbeddedDatabaseType.H2)
        addScript("schema.sql")
        addScript("data-kt.sql")
        build()
    }

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
        AuditManager.setAuditEnable(true)
        AuditManager.setMessageCollector(ConsoleMessageCollector())

    }

    @Test
    fun testDb() {
        // all: 查泛型对应的表的所有数据
        Account::class.all.forEach(::println)
        //或者 all<Account>().forEach(::println)

        // filter: 按条件查泛型对应的表的数据
        // a and (b or c)
        val start = Date.from(Instant.parse("2020-01-10T00:00:00Z"))
        val end = Date.from(Instant.parse("2020-01-12T00:00:00Z"))
        filter<Account> {
            and (Account::id eq 1)
            and (Account::id.isNotNull)
            and (Account::age `in` (17..19) or (Account::birthday between (start to end)))
        }.forEach(::println)
        // query: 较复杂查泛型对应的表的数据,如分组排序等
        query<Account> {
            where(
                (Account::age `in` (17..19) and (Account::birthday between (start to end)))
            ) orderBy -Account::id
        }.forEach(::println)

        // 对比原生：
        // 【原生】
        val queryWrapper = QueryWrapper.create()
            .select(Account::id.column(), Account::userName.column())
            .where(Account::age.column().`in`(17, 18, 19))
            .orderBy(Account::id.column().desc())
        mapper<AccountMapper>().selectListByQuery(queryWrapper)
        // 【扩展后】
        query<Account> {
            select(Account::id, Account::userName)
            where { Account::age `in` (17..19) } orderBy -Account::id
        }

        // 查询表对象对应的实体数据并根据条件过滤
        filter<Account> {
            Account::age eq 12 or
                    // if的第一个参数为true时则会调用花括号类的方法返回一个条件对象与上面那个条件对象相连接
                    `if`(true) { Account::id between (1 to 2) }
            // `if`(false) { Account::id between (1 to 2 }
        }.stream().peek(::println)
            // 过滤后修改id再次保存
            .peek { it.id = it.id.plus(2) }.forEach(Model<*>::save)
        // 使用表对象filter或者DB对象有两个泛型的filter方法时方法体内this为表对象无需XXX.AA调用，直接AA
        filter<Account> {
            Account::age eq 12 or
                    `if`(true) { Account::id `in` listOf(1, 2) }
        }.stream().peek(::println).peek { it.id = it.id.plus(6) }.forEach(Model<Account>::save)

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
