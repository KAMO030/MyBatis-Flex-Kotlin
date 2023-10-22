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
package com.mybatisflex.kotlin.vec

import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryCondition
import com.mybatisflex.core.query.QueryOrderBy
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.kotlin.extensions.kproperty.column
import com.mybatisflex.kotlin.extensions.sql.not
import com.mybatisflex.kotlin.extensions.sql.toOrd
import com.mybatisflex.kotlin.extensions.vec.wrap
import com.mybatisflex.kotlin.vec.annotation.ExperimentalDistinct
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KProperty


/**
 * 过滤查询条件。其作用相当于 SQL 语句中的 where 。
 *
 * 这个例子中，生成的 SQL 语句中的 where 部分将变为 where `name` = 'CloudPlayer'。
 * ```kotlin
 * vec.filter {
 *      it::name eq "CloudPlayer"
 * }
 * ```
 * @param predicate 该闭包的返回值即为对应的查询条件。
 * @author CloudPlayer
 */
@OptIn(ExperimentalContracts::class)
inline fun <E : Any> QueryVector<E>.filter(predicate: (E) -> QueryCondition): QueryVector<E> {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(condition = data.condition?.clone()?.and(predicate(entity)) ?: predicate(entity)))
}

/**
 * 同 [filter] ，不同的是会将闭包返回值取反。
 * @see filter
 * @see not
 * @author CloudPlayer
 */
@OptIn(ExperimentalContracts::class)
inline fun <E : Any> QueryVector<E>.filterNot(predicate: (E) -> QueryCondition): QueryVector<E> {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    return filter { !predicate(entity) }
}

/**
 * 该函数调用后，将不再查询全部的列，转而将返回值中的列存储起来，从而在查询时返回这些列。
 *
 * 此函数可以多次调用以增加需要返回的列。
 *
 * 这个例子中，生成的 SQL 语句中的 select 部分将会变为 select name, id。
 * 返回的结果中将仅包含 name 和 id 两列。
 * ```kotlin
 * vec.filterProperty {
 *     it::name
 * }.filterProperty {
 *     it::id
 * }
 * ```
 * @param propertySelector 该闭包的返回值即为对应要过滤的列。
 * @author CloudPlayer
 */
@OptIn(ExperimentalContracts::class)
inline fun <E : Any> QueryVector<E>.filterProperty(propertySelector: (E) -> KProperty<*>): QueryVector<E> {
    contract {
        callsInPlace(propertySelector, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(columns = data.columns + propertySelector(entity).column))
}

/**
 * 同 [filterProperty] ，但是闭包的返回值变为了 [Iterable] ，用于一次性指定多个要返回的列。
 *
 * 此函数同样可以多次调用以增加需要返回的列。
 *
 * 换言之，以下两个代码片段是等效的：
 * ```kotlin
 * vec.filterProperty {
 *     it::name
 * }.filterProperty {
 *     it::id
 * }
 *
 * vec.filterProperties {
 *     listOf(it::name, it::id)
 * }
 * ```
 * @param propertySelector 该闭包的返回值即为对应要过滤的多个列所组成的可迭代对象。
 * @author CloudPlayer
 */
@OptIn(ExperimentalContracts::class)
inline fun <E : Any> QueryVector<E>.filterProperties(propertySelector: (E) -> Iterable<KProperty<*>>): QueryVector<E> {
    contract {
        callsInPlace(propertySelector, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(columns = data.columns + propertySelector(entity).map { it.column }))
}

/**
 * 同 [filterProperties] ，但闭包的返回值改为了 [Sequence] 以兼容语义。
 *
 * @see filterProperties
 * @author CloudPlayer
 */
@OptIn(ExperimentalContracts::class, ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@JvmName("filterPropertiesBySequence")
inline fun <E : Any> QueryVector<E>.filterProperties(propertySelector: (E) -> Sequence<KProperty<*>>): QueryVector<E> {
    contract {
        callsInPlace(propertySelector, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(columns = data.columns + propertySelector(entity).map { it.column }))
}

/**
 * 同 [filterProperties] ，但闭包的返回值改为了 [Array] 以兼容语义。
 *
 * @see filterProperties
 * @author CloudPlayer
 */
@OptIn(ExperimentalContracts::class, ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@JvmName("filterPropertiesByArray")
inline fun <E : Any> QueryVector<E>.filterProperties(propertySelector: (E) -> Array<out KProperty<*>>): QueryVector<E> {
    contract {
        callsInPlace(propertySelector, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(columns = data.columns + propertySelector(entity).map { it.column }))
}

/**
 * 分组查询。调用此函数时会将返回值存储起来，并在查询时
 * 自动将 [keySelector] 指定的属性作为分组依据。
 *
 * @param keySelector 分组依据的列。
 * @author CloudPlayer
 */
@OptIn(ExperimentalContracts::class)
inline fun <E : Any> QueryVector<E>.groupBy(keySelector: (E) -> KProperty<*>): QueryVector<E> {
    contract {
        callsInPlace(keySelector, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(groupBy = data.groupBy + keySelector(entity).column))
}

/**
 * [keySelector] 的复数形式，其可以通过 [Iterable] 对象来一次性指定分组的多个列。
 *
 * @author CloudPlayer
 * @see keySelector
 */
@OptIn(ExperimentalContracts::class)
inline fun <E : Any> QueryVector<E>.groupsBy(keySelector: (E) -> Iterable<KProperty<*>>): QueryVector<E> {
    contract {
        callsInPlace(keySelector, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(groupBy = data.groupBy + keySelector(entity).map { it.column }))
}

@OptIn(ExperimentalContracts::class)
inline fun <E : Any, V : Comparable<V>> QueryVector<E>.sortedBy(
    order: Order = Order.ASC,
    selector: (E) -> KProperty<V?>
): QueryVector<E> {
    contract {
        callsInPlace(selector, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(orderBy = data.orderBy + selector(entity).column.toOrd(order)))
}

@OptIn(ExperimentalContracts::class, ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@JvmName("sortedByIter")
inline fun <E : Any> QueryVector<E>.sortedBy(selector: (E) -> Iterable<QueryOrderBy>): QueryVector<E> {
    contract {
        callsInPlace(selector, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(orderBy = data.orderBy + selector(entity)))
}

fun <E : Any> QueryVector<E>.drop(index: Long): QueryVector<E> {
    return copy(data = data.copy(offset = index))
}

fun <E : Any> QueryVector<E>.take(index: Long): QueryVector<E> {
    return copy(data = data.copy(rows = index))
}

fun <E : Any> QueryVector<E>.limit(offset: Long, rows: Long): QueryVector<E> {
    return copy(data = data.copy(offset = offset, rows = rows))
}

/**
 * 去重。请注意，去重目前的实现仅仅是将 [QueryData.distinct] 修改为true，然后在包装成 [QueryWrapper] 时根据 bool 值选择对应的类。
 *
 * 它无法保证用户自己扩展的每一个终端操作都能去重。
 *
 * @see [QueryData.wrap]
 * @see [DistinctQueryWrapper]
 */
@ExperimentalDistinct
fun <E : Any> QueryVector<E>.distinct(): QueryVector<E> {
    return copy(data = data.copy(distinct = true))
}

@OptIn(ExperimentalContracts::class)
inline fun <E : Any> QueryVector<E>.filterColumn(columnSelector: QueryFunctions.(E) -> QueryColumn): QueryVector<E> {
    contract {
        callsInPlace(columnSelector, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(columns = data.columns + QueryFunctions.columnSelector(entity)))
}

@OptIn(ExperimentalContracts::class)
inline fun <E : Any> QueryVector<E>.filterColumns(columnSelector: QueryFunctions.(E) -> Iterable<QueryColumn>): QueryVector<E> {
    contract {
        callsInPlace(columnSelector, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(columns = data.columns + QueryFunctions.columnSelector(entity)))
}

