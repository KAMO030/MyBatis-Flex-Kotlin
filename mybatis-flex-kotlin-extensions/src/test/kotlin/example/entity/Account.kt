package com.mybatisflex.kotlin.example.entity

import com.mybatisflex.annotation.Id
import com.mybatisflex.annotation.Table
import com.mybatisflex.core.activerecord.Model
import com.mybatisflex.kotlin.example.mapper.AccountMapper
import com.mybatisflex.kotlin.extensions.condition.allAnd
import com.mybatisflex.kotlin.extensions.db.mapper
import com.mybatisflex.kotlin.extensions.db.query
import com.mybatisflex.kotlin.extensions.kproperty.eq
import com.mybatisflex.kotlin.extensions.kproperty.`in`
import com.mybatisflex.kotlin.extensions.wrapper.whereWith
import java.util.*

/**
 * 测试用数据类
 *
 * （最好不要写成 data class ，否则没有无参构造某些情况下会报错）
 *
 * （如有需要可以安装官方 noArg 插件）
 *
 * @author KAMOsama
 */

@Table("tb_account")
data class Account(
    @Id var id: Int,
    var userName: String?,
    var age: Int?,
    var birthday: Date?,
) : Model<Account>() {
    companion object : AccountMapper by mapper() {
        fun findByAge2(age: Int, vararg ids: Int): List<Account> = query {
            whereWith {
                allAnd(
                    Account::age eq age,
                    (Account::id `in` ids.asList()).`when`(ids.isNotEmpty())
                )
            }
        }
    }

    override fun toString(): String {
        return "Account(id=$id, userName=$userName, age=$age, birthday=$birthday)"
    }
}

