/*
 *  Copyright (c) 2023-Present, Mybatis-Flex-Kotlin (kamosama@qq.com).
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
@file:Suppress("unused")
package com.mybatisflex.kotlin.extensions.condition

import com.mybatisflex.core.query.QueryCondition

/*
 * QueryCondition连接逻辑操作扩展
 * @author KAMOsama
 */

inline fun QueryCondition.and(isEffective: Boolean, condition: () -> QueryCondition): QueryCondition =
    if (isEffective) this.and(condition()) else this


inline fun QueryCondition.or(isEffective: Boolean, condition: () -> QueryCondition): QueryCondition =
    if (isEffective) this.or(condition()) else this

inline infix fun QueryCondition.and(condition: () -> QueryCondition): QueryCondition = this.and(condition())

inline infix fun QueryCondition.or(condition: () -> QueryCondition): QueryCondition = this.or(condition())

infix fun QueryCondition.and(other: QueryCondition): QueryCondition = this.and(other)

infix fun QueryCondition.or(other: QueryCondition): QueryCondition = this.or(other)

inline fun `if`(test: Boolean, block: () -> QueryCondition): QueryCondition =
    if (test) block() else emptyCondition()

fun QueryCondition.andAll(vararg conditions: QueryCondition?): QueryCondition = this and allAnd(*conditions)

fun QueryCondition.orAll(vararg conditions: QueryCondition?): QueryCondition = this or allOr(*conditions)

fun allAnd(vararg conditions: QueryCondition?): QueryCondition =
    conditions.asSequence().filterNotNull().reduce(QueryCondition::and)

fun allOr(vararg conditions: QueryCondition?): QueryCondition =
    conditions.asSequence().filterNotNull().reduce(QueryCondition::or)

/**
 * 创建一个空的QueryCondition
 */
fun emptyCondition(): QueryCondition = QueryCondition.createEmpty()
