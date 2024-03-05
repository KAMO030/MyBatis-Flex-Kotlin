package com.mybatisflex.kotlin.example.entity

import com.mybatisflex.annotation.Id
import com.mybatisflex.annotation.Table
import com.mybatisflex.core.activerecord.Model
import com.mybatisflex.kotlin.extensions.condition.and
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
    @Id var id: Int = -1,
    var userName: String? = null,
    var age: Int? = null,
    var birthday: Date? = null,
) : Model<Account>() {
    companion object {
        fun findByAge(age: Int, vararg ids: Int): List<Account> = query {
            whereWith {
                (Account::age eq age).and(ids.isNotEmpty()) {
                    Account::id `in` ids.asList()
                }
            }
        }
    }
}

