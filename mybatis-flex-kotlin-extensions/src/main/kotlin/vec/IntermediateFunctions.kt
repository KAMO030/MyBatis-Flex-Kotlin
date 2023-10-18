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
import com.mybatisflex.kotlin.vec.annotation.ExperimentalDistinct
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KProperty

@OptIn(ExperimentalContracts::class)
inline fun <E : Any> QueryVector<E>.filter(predicate: (E) -> QueryCondition): QueryVector<E> {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(condition = data.condition?.clone()?.and(predicate(entity)) ?: predicate(entity)))
}

@OptIn(ExperimentalContracts::class)
inline fun <E : Any> QueryVector<E>.filterNot(predicate: (E) -> QueryCondition): QueryVector<E> {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    return filter { !predicate(entity) }
}

@OptIn(ExperimentalContracts::class)
inline fun <E : Any> QueryVector<E>.filterProperty(predicate: (E) -> KProperty<*>): QueryVector<E> {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(columns = data.columns + predicate(entity).column))
}

@OptIn(ExperimentalContracts::class)
inline fun <E : Any> QueryVector<E>.filterProperties(predicate: (E) -> Iterable<KProperty<*>>): QueryVector<E> {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(columns = data.columns + predicate(entity).map { it.column }))
}

@OptIn(ExperimentalContracts::class)
inline fun <E : Any> QueryVector<E>.groupBy(groupBy: (E) -> KProperty<*>): QueryVector<E> {
    contract {
        callsInPlace(groupBy, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(groupBy = data.groupBy + groupBy(entity).column))
}

@OptIn(ExperimentalContracts::class, ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@JvmName("groupByIter")
inline fun <E : Any> QueryVector<E>.groupBy(groupBy: (E) -> Iterable<KProperty<*>>): QueryVector<E> {
    contract {
        callsInPlace(groupBy, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(groupBy = data.groupBy + groupBy(entity).map { it.column }))
}

@OptIn(ExperimentalContracts::class)
inline fun <E : Any, V : Comparable<V>> QueryVector<E>.sortedBy(
    order: Order = Order.ASC,
    sortedBy: (E) -> KProperty<V?>
): QueryVector<E> {
    contract {
        callsInPlace(sortedBy, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(orderBy = data.orderBy + sortedBy(entity).column.toOrd(order)))
}

@OptIn(ExperimentalContracts::class, ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@JvmName("sortedByIter")
inline fun <E : Any> QueryVector<E>.sortedBy(sortedBy: (E) -> Iterable<QueryOrderBy>): QueryVector<E> {
    contract {
        callsInPlace(sortedBy, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(orderBy = data.orderBy + sortedBy(entity)))
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
 * 去重。请注意，去重目前的实现仅仅是将[QueryData.distinct]修改为true，然后在包装成[QueryWrapper]时根据bool值选择对应的类。
 *
 * 当distinct值为true时，将使用[DistinctQueryWrapper]，它会在初始化的时候直接使用匿名类和[QueryFunctions.distinct]
 * 包装父类[QueryWrapper]底层中的selectColumns，来达到去重的目的。
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
inline fun <E : Any> QueryVector<E>.filterColumn(aggregateFun: QueryFunctions.(E) -> QueryColumn): QueryVector<E> {
    contract {
        callsInPlace(aggregateFun, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(columns = data.columns + QueryFunctions.aggregateFun(entity)))
}

@OptIn(ExperimentalContracts::class)
inline fun <E : Any> QueryVector<E>.filterColumns(aggregateFun: QueryFunctions.(E) -> Iterable<QueryColumn>): QueryVector<E> {
    contract {
        callsInPlace(aggregateFun, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(columns = data.columns + QueryFunctions.aggregateFun(entity)))
}

