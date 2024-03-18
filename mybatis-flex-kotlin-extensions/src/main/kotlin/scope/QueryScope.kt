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
package com.mybatisflex.kotlin.scope

import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryWrapperAdapter
import com.mybatisflex.core.util.LambdaGetter
import com.mybatisflex.kotlin.extensions.kproperty.column
import kotlin.reflect.KProperty

/**
 * 查询作用域
 * @author KAMOsama
 * @date 2023/8/7
 */
class QueryScope : QueryWrapperAdapter<QueryScope>() {

    operator fun String.get(name: String): QueryColumn = QueryColumn(this, name)

    operator fun String.unaryMinus(): QueryColumn = QueryColumn(this)

    fun select(vararg properties: KProperty<*>): QueryScope =
        select(*properties.map { it.column }.toTypedArray())

    @Deprecated("", level = DeprecationLevel.HIDDEN)
    override fun <T : Any?> select(vararg lambdaGetters: LambdaGetter<T>?): QueryScope {
        return super.select(*lambdaGetters)
    }

    fun hasSelect(): Boolean = this.selectColumns != null && this.selectColumns.isNotEmpty()

    @Deprecated("", level = DeprecationLevel.HIDDEN)
    override fun <T : Any?> groupBy(column: LambdaGetter<T>?): QueryScope {
        return super.groupBy(column)
    }

    fun <T : Any?> groupBy(column: KProperty<T>): QueryScope = groupBy(column.column)
}


inline fun queryScope(vararg columns: QueryColumn, init: QueryScope.() -> Unit = {}): QueryScope =
    QueryScope().apply(init).apply { if (columns.isNotEmpty() && !hasSelect()) select(*columns) }



