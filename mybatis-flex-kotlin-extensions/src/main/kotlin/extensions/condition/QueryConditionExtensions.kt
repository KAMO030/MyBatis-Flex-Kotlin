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
package com.mybatisflex.kotlin.extensions.condition

import com.mybatisflex.core.query.QueryCondition

/*
 * QueryCondition连接逻辑操作扩展
 * @author 卡莫sama
 */

fun QueryCondition.and(isEffective: Boolean, predicate: () -> QueryCondition): QueryCondition =
    if (isEffective) and(predicate()) else this


fun QueryCondition.or(isEffective: Boolean, predicate: () -> QueryCondition): QueryCondition =
    if (isEffective) this.or(predicate()) else this

inline infix fun QueryCondition.and(predicate: () -> QueryCondition): QueryCondition = this.and(predicate())

inline infix fun QueryCondition.or(predicate: () -> QueryCondition): QueryCondition = this.or(predicate())

infix fun QueryCondition.and(other: QueryCondition): QueryCondition = this.and(other)

infix fun QueryCondition.or(other: QueryCondition): QueryCondition = this.or(other)

inline fun `if`(test: Boolean, block: () -> QueryCondition): QueryCondition =
    if (test) block() else QueryCondition.createEmpty()

