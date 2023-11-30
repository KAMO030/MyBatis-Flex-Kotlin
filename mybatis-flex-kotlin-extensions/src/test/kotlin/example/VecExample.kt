package com.mybatisflex.kotlin.example

import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.kotlin.example.entity.Account
import com.mybatisflex.kotlin.example.mapper.AccountMapper
import com.mybatisflex.kotlin.extensions.db.mapper
import com.mybatisflex.kotlin.extensions.kproperty.`as`
import com.mybatisflex.kotlin.extensions.kproperty.column
import com.mybatisflex.kotlin.extensions.kproperty.eq
import com.mybatisflex.kotlin.extensions.kproperty.ge
import com.mybatisflex.kotlin.extensions.vec.vecOf
import com.mybatisflex.kotlin.extensions.wrapper.from
import com.mybatisflex.kotlin.extensions.wrapper.select
import com.mybatisflex.kotlin.scope.runFlex
import com.mybatisflex.kotlin.vec.*
import org.apache.ibatis.logging.stdout.StdOutImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import java.time.Instant
import java.util.*

class VecExample {

    private val accountMapper: AccountMapper get() = mapper()

    init {
        runFlex {
            +AccountMapper::class
            +EmbeddedDatabaseBuilder().run {
                setType(EmbeddedDatabaseType.H2)
                addScript("schema.sql")
                addScript("data-kt.sql")
                build()
            }
            logImpl = StdOutImpl::class
        }
    }

    @Test
    fun first() {
        val vec = vecOf<Account>()
        val date = Date.from(Instant.parse("2020-01-10T16:00:00Z"))
        val vecAccount = vec.first { it::age eq 18 }
        val cmp = Account(id = 1, userName = "张三", age = 18, birthday = date)
        Assertions.assertEquals(vecAccount, cmp)
    }

    @Test
    fun filter() {
        val vec = vecOf<Account>()
        val filter = vec.filter { it::id ge 100 }

        val query = QueryWrapper()
        query.where(Account::id ge 100).from(Account::class)

        Assertions.assertEquals(filter.sql, query.toSQL())
        Assertions.assertEquals(filter.toList(), accountMapper.selectListByQuery(query))
    }

    @Test
    fun select() {
        val vec = vecOf<Account>()
        val filterColumn = vec.filterProperties { listOf(it::id, it::userName) }

        val query = QueryWrapper().also {
            it.select { listOf(Account::id, Account::userName) } from Account::class
        }

        Assertions.assertEquals(filterColumn.sql, query.toSQL())
        Assertions.assertEquals(filterColumn.toList(), accountMapper.selectListByQuery(query))
        Assertions.assertEquals(filterColumn.toRows(), accountMapper.selectRowsByQuery(query))
    }

    @Test
    fun selectAs() {
        val vec = vecOf<Account>("a")
        val aggregation = vec.filterColumns { listOf(it::id `as` "accountId", it::userName.column) }
        val query: QueryWrapper = QueryWrapper()
            .select(
                Account::id.`as`("accountId"), Account::userName.column
            )
            .from(Account::class).`as`("a")

        Assertions.assertEquals(aggregation.sql, query.toSQL())

    }
}