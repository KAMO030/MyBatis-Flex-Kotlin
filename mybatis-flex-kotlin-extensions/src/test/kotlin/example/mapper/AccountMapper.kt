package com.mybatisflex.kotlin.example.mapper

import com.mybatisflex.core.BaseMapper
import com.mybatisflex.kotlin.example.entity.Account
import com.mybatisflex.kotlin.extensions.condition.and
import com.mybatisflex.kotlin.extensions.kproperty.eq
import com.mybatisflex.kotlin.extensions.kproperty.`in`
import com.mybatisflex.kotlin.extensions.mapper.query
import com.mybatisflex.kotlin.extensions.wrapper.whereWith


@JvmDefaultWithCompatibility
interface AccountMapper : BaseMapper<Account> {


    fun findByAge(age: Int, vararg ids: Int): List<Account> = query {
        whereWith {
            (Account::age eq age).and(ids.isNotEmpty()) {
                Account::id `in` ids.asList()
            }
        }
    }

}

