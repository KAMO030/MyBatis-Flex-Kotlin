package com.mybatisflex.kotlin.flexStream

import com.example.springbootdemo.flexStream.FS
import com.mybatisflex.core.query.*
import com.mybatisflex.core.row.Row
import com.mybatisflex.core.table.TableDef
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KProperty

// !---------------------------------------------- 中间操作 ----------------------------------------------!
@ExperimentalFlexStream
@OptIn(ExperimentalContracts::class)
inline fun <T, O : Any> FS<T, O>.filter(fn: (O) -> QueryCondition): FS<T, O> {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    return copyIf(queryChain = queryChain.where(fn(entity)))
}

@ExperimentalFlexStream
@OptIn(ExperimentalContracts::class)
inline fun <T, O : Any> FS<T, O>.filters(fn: (O) -> List<QueryCondition>): FS<T, O> {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    val al = fn(entity)
    if (al.isEmpty()) return copyIf(queryChain = queryChain)
    val res = al.reduce { acc, queryCondition ->
        acc.and(queryCondition)
    }
    return copyIf(queryChain = queryChain.where(res))
}

@ExperimentalFlexStream
@OptIn(ExperimentalContracts::class)
inline fun <T, O : Any> FS<T, O>.filterColumns(fn: (O) -> List<KProperty<*>>): FS<T, O> {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    val al = fn(entity)
    return copyIf(queryChain = queryChain.select(*Array(al.size) { al[it].toQueryColumn() }))
}

@ExperimentalFlexStream
@OptIn(ExperimentalContracts::class)
inline fun <T, O : Any> FS<T, O>.filterColumn(fn: (O) -> KProperty<*>): FS<T, O> {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    return copyIf(queryChain = queryChain.select(fn(entity).toQueryColumn()))
}

@ExperimentalFlexStream
@OptIn(ExperimentalContracts::class)
inline fun <T, O : Any> FS<T, O>.groupBy(fn: (O) -> KProperty<*>): FS<T, O> {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    return copyIf(queryChain = queryChain.groupBy(fn(entity).toQueryColumn()))
}

@ExperimentalFlexStream
@OptIn(ExperimentalContracts::class)
inline fun <T, O : Any, R : Comparable<R>> FS<T, O>.sortedBy(
    orderBy: Order = Order.ASC,
    fn: (O) -> KProperty<R>
): FS<T, O> {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    val column = fn(entity).toQueryColumn()
    val queryChain = when (orderBy) {
        Order.ASC -> queryChain.orderBy(column.asc())
        Order.DESC -> queryChain.orderBy(column.desc())
    }
    return copyIf(queryChain = queryChain)
}

@ExperimentalFlexStream
@OptIn(ExperimentalContracts::class)
@Suppress("UNCHECKED_CAST")
inline fun <T : TableDef, O : Any> FS<T, O>.aggregation(fn: QueryFunctions.(T) -> QueryColumn): FS<T, Row> {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    val queryChain = this.queryChain
    return FlexStream(
        Row(),
        tableDef as T,
        null,
        queryChain.select(QueryFunctions.fn(tableDef as T)) as QueryChain<Row>,
        copy
    )
}

@ExperimentalFlexStream
@OptIn(ExperimentalContracts::class)
@Suppress("UNCHECKED_CAST")
inline fun <T : TableDef, O : Any> FS<T, O>.aggregations(fn: QueryFunctions.(T) -> List<QueryColumn>): FS<T, Row> {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    val queryChain = this.queryChain.clone()
    return FlexStream(
        Row(),
        tableDef as T,
        null,
        queryChain.select(*QueryFunctions.fn(tableDef as T).toTypedArray()) as QueryChain<Row>,
        copy
    )
}

@ExperimentalFlexStream
fun <T1: TableDef, O1 : Any, T2: TableDef, O2: Any> FS<T1, O1>.zip(
    other: FS<T2, O2>,
    unionAll: Boolean = false
): FS<Pair<T1, T2>, O1> {
    val queryChain = queryChain.clone()
    if (unionAll) queryChain.unionAll(other.queryChain.from(other.tableDef))
    else queryChain.union(other.queryChain.from(other.tableDef))
    @Suppress("UNCHECKED_CAST")
    return FlexStream(entity, (tableDef to other.tableDef) as Pair<T1, T2>, mapper, queryChain, copy)
}

