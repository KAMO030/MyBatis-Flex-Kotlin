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

import com.mybatisflex.core.MybatisFlexBootstrap
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.kotlin.extensions.kproperty.`as`
import com.mybatisflex.kotlin.extensions.kproperty.eq
import com.mybatisflex.kotlin.extensions.kproperty.ge
import com.mybatisflex.kotlin.extensions.vec.vecOf
import com.mybatisflex.kotlin.scope.buildBootstrap
import com.mybatisflex.kotlin.test.entity.Account
import com.mybatisflex.kotlin.test.entity.table.AccountTableDef
import com.mybatisflex.kotlin.test.mapper.AccountMapper
import com.mybatisflex.kotlin.vec.*
import org.apache.ibatis.logging.stdout.StdOutImpl
import org.junit.jupiter.api.Test
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals


class VecExample {
    val accountMapper: AccountMapper get() = MybatisFlexBootstrap.getInstance().getMapper(AccountMapper::class.java)

    init {
        buildBootstrap {
            it.addMapper(AccountMapper::class.java)
            it.dataSource = EmbeddedDatabaseBuilder().run {
                setType(EmbeddedDatabaseType.H2)
                addScript("schema.sql")
                addScript("data-kt.sql")
                build()
            }
            it.logImpl = StdOutImpl::class.java
        }.start()
    }

    @Test
    fun first() {
        val vec = vecOf<Account>()
        val date = Date.from(Instant.parse("2020-01-10T16:00:00Z"))
        val vecAccount = vec.first { it::age eq 18 }
        val cmp = Account(id = 1, userName = "张三", age = 18, birthday = date)
        assertEquals(vecAccount, cmp)
    }

    @Test
    fun filter(): Unit = with(AccountTableDef.ACCOUNT) {
        val vec = vecOf<Account>()
        val filter = vec.filter { it::id ge 100 }

        val query = QueryWrapper()
        query.where(ID.ge(100)).from(this)

        assertEquals(filter.sql, query.toSQL())
        assertEquals(filter.toList(), accountMapper.selectListByQuery(query))
    }

    @Test
    fun select(): Unit = with(AccountTableDef.ACCOUNT) {
        val vec = vecOf<Account>()
        val filterColumn = vec.filterProperties { listOf(it::id, it::userName) }

        val query = QueryWrapper()
        query.select(ID, USER_NAME).from(this)

        assertEquals(filterColumn.sql, query.toSQL())
        assertEquals(filterColumn.toList(), accountMapper.selectListByQuery(query))
        assertEquals(filterColumn.toRows(), accountMapper.selectRowsByQuery(query))
    }

    @Test
    fun selectAs(): Unit = with(AccountTableDef.ACCOUNT) {
        val vec = vecOf<Account>("a")
        val aggregation = vec.filterColumns { listOf(it::id `as`  "accountId", USER_NAME) }
        val query: QueryWrapper = QueryWrapper()
            .select(
                ID.`as`("accountId"), USER_NAME
            )
            .from(`as`("a"))

        assertEquals(aggregation.sql, query.toSQL())

    }
}