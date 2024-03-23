package com.mybatisflex.kotlin.test

import com.mybatisflex.core.query.CPI
import com.mybatisflex.core.query.QueryCondition
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.kotlin.example.entity.Account
import com.mybatisflex.kotlin.extensions.condition.EmptyCondition
import com.mybatisflex.kotlin.extensions.condition.and
import com.mybatisflex.kotlin.extensions.condition.annotation.ExperimentalEmptyCondition
import com.mybatisflex.kotlin.extensions.condition.or
import com.mybatisflex.kotlin.extensions.kproperty.eq
import org.junit.jupiter.api.Test

@OptIn(ExperimentalEmptyCondition::class)
class EmptyConditionTest {

    @Test
    fun test() {
        val cond1 = Account::id eq 1
        val cond2 = Account::id eq 114514
        val condition =
            EmptyCondition and cond2 and EmptyCondition or cond1 and EmptyCondition
        println(condition.contentToString())
        println(cond1.prev() to cond1.next())
        println(cond2.prev() to cond2.next())
        println(condition.prev() to condition.next())
    }

    private val flexEmptyCondition: QueryCondition
        get() = QueryCondition.createEmpty()

    private fun QueryCondition.contentToString(): String {
        return QueryWrapper().where(this).toSQL()
    }

    private fun QueryCondition.prev(): QueryCondition? = CPI.getPrevEffectiveCondition(this)

    private fun QueryCondition.next(): QueryCondition? = CPI.getNextCondition(this)

    @Test
    fun test2() {
        val condition =
            flexEmptyCondition and (Account::id eq 114514) and flexEmptyCondition or (Account::id eq 1)
        println(condition.contentToString())
    }

    @Test
    fun test3() {
        val condition =
            EmptyCondition and EmptyCondition and (Account::id eq 114514) and EmptyCondition and EmptyCondition
        println(condition.contentToString())
    }

    @Test
    fun test4() {
        val condition = EmptyCondition and EmptyCondition and EmptyCondition and (Account::id eq 114514)
        println(condition.contentToString())
    }

    @Test
    fun test5() {
        var condition = EmptyCondition and flexEmptyCondition and EmptyCondition
        println(condition.contentToString())
        condition = flexEmptyCondition and EmptyCondition and flexEmptyCondition
        println(condition.contentToString())
    }

    @Test
    fun test6() {
        Account::id eq 114514 and EmptyCondition
        println(EmptyCondition.prev())
        Account::id eq 1919810 and EmptyCondition
        println(EmptyCondition.prev())
    }
}