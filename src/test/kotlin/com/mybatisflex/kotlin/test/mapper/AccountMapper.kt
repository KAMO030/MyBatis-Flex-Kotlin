/*
 *  Copyright (c) 2022-2023, Mybatis-Flex-Kotlin (837080904@qq.com).
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
package com.mybatisflex.kotlin.test.mapper

import com.mybatisflex.core.BaseMapper
import com.mybatisflex.kotlin.extensions.mapper.queryList
import com.mybatisflex.kotlin.extensions.sql.*
import com.mybatisflex.kotlin.test.entity.Account


@JvmDefaultWithCompatibility
interface AccountMapper : BaseMapper<Account> {


    fun findByAge(age: Int, vararg ids: Int): List<Account> = queryList {
        select(Account.ALL_COLUMNS)
        from(Account)
        where(Account) {
            (AGE `=` age) and `if`(ids.isNotEmpty()) {
                ID `in` ids.asList()
            }
        }
    }

}

