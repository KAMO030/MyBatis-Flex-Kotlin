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
package com.mybatisflex.kotlin.extensions.mapper

import com.mybatisflex.core.BaseMapper
import com.mybatisflex.core.query.QueryCondition
import com.mybatisflex.kotlin.scope.QueryScope
import com.mybatisflex.kotlin.scope.queryScope

/*
 * 映射器操作扩展
 * @author 卡莫sama
 */
inline fun <T> BaseMapper<T>.selectListByQuery(init: QueryScope.() -> Unit): List<T> =
    queryScope(init = init).let(::selectListByQuery)

inline fun <T> BaseMapper<T>.selectListBCondition(init: () -> QueryCondition): List<T> =
    init().let(::selectListByCondition)

inline fun <T> BaseMapper<T>.updateByQuery(entity: T, init: QueryScope.() -> Unit): Int =
    this.updateByQuery(entity, queryScope(init = init))

inline fun <T> BaseMapper<T>.updateByCondition(entity: T, init: () -> QueryCondition): Int =
    this.updateByCondition(entity, init())

inline fun <T> BaseMapper<T>.deleteByQuery(init: QueryScope.() -> Unit): Int =
    queryScope(init = init).let(::deleteByQuery)

inline fun <T> BaseMapper<T>.deleteByCondition(init: () -> QueryCondition): Int =
    init().let(::deleteByCondition)


