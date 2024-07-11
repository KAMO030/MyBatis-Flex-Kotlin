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

package com.mybatisflex.kotlin.scope

import com.mybatisflex.core.query.*
import com.mybatisflex.core.util.LambdaGetter
import com.mybatisflex.kotlin.extensions.kproperty.column
import com.mybatisflex.kotlin.extensions.sql.`as`
import com.mybatisflex.kotlin.extensions.wrapper.selectProperties
import java.util.function.Consumer
import kotlin.reflect.KProperty

/**
 * 查询作用域
 * @author KAMOsama
 * @date 2023/8/7
 */
class QueryScope : QueryWrapperAdapter<QueryScope>() {

    operator fun String.get(name: String): QueryColumn = QueryColumn(this, name)

    operator fun String.unaryMinus(): QueryColumn = QueryColumn(this)

    fun hasSelect(): Boolean = this.selectColumns != null && this.selectColumns.isNotEmpty()

    @Deprecated("", level = DeprecationLevel.HIDDEN)
    override fun <T : Any?> select(vararg lambdaGetters: LambdaGetter<T>?): QueryScope = super.select(*lambdaGetters)

    fun select(vararg properties: KProperty<*>): QueryScope = this.apply { selectProperties(*properties) }

    @Deprecated("", level = DeprecationLevel.HIDDEN)
    override fun <T : Any?> groupBy(column: LambdaGetter<T>?): QueryScope = super.groupBy(column)

    fun <T : Any?> groupBy(column: KProperty<T>): QueryScope = groupBy(column.column)

    @Deprecated("", level = DeprecationLevel.HIDDEN)
    override fun <T : Any?> where(fn: LambdaGetter<T>?): QueryConditionBuilder<QueryScope> = super.where(fn)

    fun <T : Any?> where(column: KProperty<T>): QueryScope = where(column.column.name)

    @Deprecated(
        "", level = DeprecationLevel.WARNING,
        replaceWith = ReplaceWith("whereBy(consumer)", "com.mybatisflex.kotlin.extensions.wrapper.whereBy")
    )
    override fun where(consumer: Consumer<QueryWrapper>): QueryScope = super.where(consumer)

    @Deprecated(
        "", level = DeprecationLevel.WARNING,
        replaceWith = ReplaceWith("whereWith(consumer)", "com.mybatisflex.kotlin.extensions.wrapper.whereWith")
    )
    fun where(consumer: () -> QueryCondition): QueryScope = super.where(consumer())

}


inline fun queryScope(vararg columns: QueryColumn, init: QueryScope.() -> Unit = {}): QueryScope =
    QueryScope().apply(init).apply {
        if (columns.isNotEmpty() && !hasSelect()) select(*columns)
    }

/**
 * 构建子查询作为select的字段
 * @param alias 别名
 * @since 1.1.1
 */
fun selectQueryColumn(alias: String? = null, subQuery: QueryScope.() -> Unit): QueryColumn =
    SelectQueryColumn(queryScope(init = subQuery)).run {
        alias?.let { this `as` it } ?: this
    }
