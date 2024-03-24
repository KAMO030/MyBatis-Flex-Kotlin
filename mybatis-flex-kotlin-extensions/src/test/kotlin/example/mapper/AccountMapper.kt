package com.mybatisflex.kotlin.example.mapper

import com.mybatisflex.core.BaseMapper
import com.mybatisflex.kotlin.example.entity.Account
import com.mybatisflex.kotlin.extensions.condition.allAnd
import com.mybatisflex.kotlin.extensions.kproperty.eq
import com.mybatisflex.kotlin.extensions.kproperty.`in`
import com.mybatisflex.kotlin.extensions.mapper.filter


@JvmDefaultWithCompatibility
interface AccountMapper : BaseMapper<Account> {
    fun findByAge(age: Int, vararg ids: Int): List<Account> = filter {
        allAnd(
            Account::age eq age,
            (Account::id `in` ids.asList()).`when`(ids.isNotEmpty())
        )
    }
}

