package com.mybatisflex.kotlin.vec

import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryCondition
import com.mybatisflex.core.query.QueryOrderBy
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.kotlin.extensions.sql.not
import com.mybatisflex.kotlin.extensions.sql.toOrd
import com.mybatisflex.kotlin.extensions.sql.toQueryColumn
import com.mybatisflex.kotlin.flexStream.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
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
inline fun <E: Any> QueryVector<E>.filterProperty(predicate: (E) -> KProperty<*>): QueryVector<E> {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(columns = data.columns + predicate(entity).toQueryColumn()))
}

@OptIn(ExperimentalContracts::class)
inline fun <E: Any> QueryVector<E>.filterProperties(predicate: (E) -> Iterable<KProperty<*>>): QueryVector<E> {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(columns = data.columns + predicate(entity).map { it.toQueryColumn() }))
}

@OptIn(ExperimentalContracts::class)
inline fun <E: Any> QueryVector<E>.groupBy(groupBy: (E) -> KProperty<*>): QueryVector<E> {
    contract {
        callsInPlace(groupBy, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(groupBy = data.groupBy + groupBy(entity).toQueryColumn()))
}

@OptIn(ExperimentalContracts::class)
inline fun <E: Any> QueryVector<E>.groupByIter(groupBy: (E) -> Iterable<KProperty<*>>): QueryVector<E> {
    contract {
        callsInPlace(groupBy, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(groupBy = data.groupBy + groupBy(entity).map { it.toQueryColumn() }))
}

@OptIn(ExperimentalContracts::class)
inline fun <E: Any, V: Comparable<V>> QueryVector<E>.sortedBy(order: Order = Order.ASC, sortedBy: (E) -> KProperty<V?>): QueryVector<E> {
    contract {
        callsInPlace(sortedBy, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(orderBy = data.orderBy + sortedBy(entity).toQueryColumn().toOrd(order)))
}

@OptIn(ExperimentalContracts::class)
inline fun <E: Any> QueryVector<E>.sortedByIter(sortedBy: (E) -> Iterable<QueryOrderBy>): QueryVector<E> {
    contract {
        callsInPlace(sortedBy, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(orderBy = data.orderBy + sortedBy(entity)))
}

fun <E: Any> QueryVector<E>.drop(index: Long): QueryVector<E> {
    return copy(data = data.copy(offset = index))
}

fun <E: Any> QueryVector<E>.take(index: Long): QueryVector<E> {
    return copy(data = data.copy(rows = index))
}

fun <E: Any> QueryVector<E>.limit(offset: Long, rows: Long): QueryVector<E> {
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
fun <E: Any> QueryVector<E>.distinct(): QueryVector<E> {
    return copy(data = data.copy(distinct = true))
}

@OptIn(ExperimentalContracts::class)
inline fun <E: Any> QueryVector<E>.filterColumn(aggregateFun: QueryFunctions.(E) -> QueryColumn): QueryVector<E> {
    contract {
        callsInPlace(aggregateFun, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(columns = data.columns + QueryFunctions.aggregateFun(entity)))
}

@OptIn(ExperimentalContracts::class)
inline fun <E: Any> QueryVector<E>.filterColumns(aggregateFun: QueryFunctions.(E) -> Iterable<QueryColumn>): QueryVector<E> {
    contract {
        callsInPlace(aggregateFun, InvocationKind.EXACTLY_ONCE)
    }
    return copy(data = data.copy(columns = data.columns + QueryFunctions.aggregateFun(entity)))
}