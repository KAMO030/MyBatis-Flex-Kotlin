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

