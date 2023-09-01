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
package com.mybatisflex.kotlin.extensions.mapper

import com.mybatisflex.core.BaseMapper
import com.mybatisflex.core.query.QueryCondition
import com.mybatisflex.kotlin.scope.QueryScope
import com.mybatisflex.kotlin.scope.queryScope

/*
 * 映射器操作扩展
 * @author 卡莫sama
 */
fun <T> BaseMapper<T>.queryList(init: (QueryScope.() -> Unit)?): List<T> =
    queryScope(init = init).let(this::selectListByQuery)

fun <T> BaseMapper<T>.updateByCondition(entity: T, init: () -> QueryCondition): Int =
    this.updateByCondition(entity, init())

fun <T> BaseMapper<T>.delete(init: (QueryScope.() -> Unit)?): Int =
    queryScope(init = init).let(this::deleteByQuery)


