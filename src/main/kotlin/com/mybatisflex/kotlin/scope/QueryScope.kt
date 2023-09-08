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
package com.mybatisflex.kotlin.scope

import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryCondition
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.kotlin.extensions.kproperty.column
import kotlin.reflect.KProperty

/**
 * 查询作用域
 * @author 卡莫sama
 * @date 2023/8/7
 */
class QueryScope : QueryWrapper() {
    companion object CurrentQueryScope : ThreadLocal<QueryScope>()

    operator fun String.get(name: String): QueryColumn = QueryColumn(this, name)

    operator fun String.unaryMinus(): QueryColumn = QueryColumn(this)

    fun select(vararg properties:KProperty<*>): QueryWrapper =
        this.select(*(properties.map { it.column }.toTypedArray()))

    fun where(build: QueryWrapper.() -> QueryCondition): QueryWrapper = where(this.build())
}


fun queryScope(vararg columns: QueryColumn?, init: (QueryScope.() -> Unit)? = null): QueryWrapper {
    val builder = QueryScope()

    if (columns.isNotEmpty()) {
        builder.select(*columns)
    }
    //用于嵌套查询拿到上层查询包装对象
    init?.also {
        val prentQueryScope = QueryScope.get()
        QueryScope.set(builder)
        it(builder)
        QueryScope.set(prentQueryScope)
    }

    return builder
}


