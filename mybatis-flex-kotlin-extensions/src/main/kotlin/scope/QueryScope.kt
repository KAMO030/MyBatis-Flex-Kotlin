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
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.kotlin.extensions.kproperty.column
import kotlin.reflect.KProperty

/**
 * 查询作用域
 * @author KAMOsama
 * @date 2023/8/7
 */
class QueryScope : QueryWrapper() {

    operator fun String.get(name: String): QueryColumn = QueryColumn(this, name)

    operator fun String.unaryMinus(): QueryColumn = QueryColumn(this)

    fun select(vararg properties: KProperty<*>): QueryWrapper =
        this.select(*(properties.map { it.column }.toTypedArray()))

    fun hasSelect(): Boolean = this.selectColumns != null && this.selectColumns.isNotEmpty()

}


inline fun queryScope(vararg columns: QueryColumn, init: QueryScope.() -> Unit = {}): QueryWrapper =
    QueryScope().apply(init).apply { if (columns.isNotEmpty() && !hasSelect()) select(*columns) }



