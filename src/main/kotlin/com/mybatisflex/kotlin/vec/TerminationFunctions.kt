package com.mybatisflex.kotlin.vec

import com.mybatisflex.core.query.CPI
import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryCondition
import com.mybatisflex.core.query.QueryMethods
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.core.row.Db
import com.mybatisflex.core.row.Row
import com.mybatisflex.kotlin.extensions.vec.isRow
import com.mybatisflex.kotlin.flexStream.QueryFunctions
import com.mybatisflex.kotlin.extensions.sql.not
import com.mybatisflex.kotlin.extensions.sql.toQueryColumn
import java.math.BigDecimal
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

fun <E : Any> QueryVector<E>.toList(): List<E> = mapper.selectListByQuery(wrapper)

fun <E : Any> QueryVector<E>.toRows(): List<Row> = mapper.selectRowsByQuery(wrapper)

inline val <E : Any> QueryVector<E>.values: List<Any> get() = toRows().flatMap { it.values }

inline val <E : Any> QueryVector<E>.keys: Set<String> get() = toRows().flatMapTo(HashSet()) { it.keys }

@OptIn(ExperimentalContracts::class)
inline fun <reified E : Any, reified R : Comparable<R>> QueryVector<E>.maxOf(selector: (E) -> KProperty<R?>): R {
    contract {
        callsInPlace(selector, InvocationKind.EXACTLY_ONCE)
    }
    val column = selector(entity)
    return funOf(column) {
        max(it)
    }
}

@OptIn(ExperimentalContracts::class)
inline fun <reified E : Any, reified R : Comparable<R>> QueryVector<E>.maxBy(selector: (E) -> KProperty<R?>): E? {
    contract {
        callsInPlace(selector, InvocationKind.EXACTLY_ONCE)
    }
    val column = selector(entity).toQueryColumn()
    val maxValue = QueryWrapper().select(QueryFunctions.max(column)).from(tableDef)
    return find { column.eq(maxValue) }
}

@OptIn(ExperimentalContracts::class)
inline fun <reified E : Any, reified R : Comparable<R>> QueryVector<E>.minOf(selector: (E) -> KProperty<R?>): R {
    contract {
        callsInPlace(selector, InvocationKind.EXACTLY_ONCE)
    }
    val column = selector(entity)
    return funOf(column) {
        min(it)
    }
}

@OptIn(ExperimentalContracts::class)
inline fun <reified E : Any, reified R : Comparable<R>> QueryVector<E>.minBy(selector: (E) -> KProperty<R?>): E? {
    contract {
        callsInPlace(selector, InvocationKind.EXACTLY_ONCE)
    }
    val column = selector(entity).toQueryColumn()
    val minValue = QueryWrapper().select(QueryFunctions.min(column)).from(tableDef)
    return find { column.eq(minValue) }
}

@OptIn(ExperimentalContracts::class)
inline fun <reified E : Any, reified R : Number> QueryVector<E>.avgOf(selector: (E) -> KProperty<R?>): BigDecimal {
    contract {
        callsInPlace(selector, InvocationKind.EXACTLY_ONCE)
    }
    val column = selector(entity).toQueryColumn()
    val wrapper = wrapper
    CPI.setSelectColumns(wrapper, mutableListOf())
    wrapper.select(QueryMethods.avg(column))
    return mapper.selectObjectByQueryAs(wrapper, BigDecimal::class.java)
}

@OptIn(ExperimentalContracts::class)
inline fun <reified E : Any, reified R : Number> QueryVector<E>.sumOf(selector: (E) -> KProperty<R?>): BigDecimal {
    contract {
        callsInPlace(selector, InvocationKind.EXACTLY_ONCE)
    }
    val column = selector(entity).toQueryColumn()
    val wrapper = wrapper
    CPI.setSelectColumns(wrapper, mutableListOf())
    wrapper.select(QueryMethods.sum(column))
    return mapper.selectObjectByQueryAs(wrapper, BigDecimal::class.java)
}

fun <E : Any> QueryVector<E>.count() = mapper.selectCountByQuery(QueryWrapper().from(wrapper).`as`(data.tableAlias))

@OptIn(ExperimentalContracts::class)
inline fun <E : Any> QueryVector<E>.count(predicate: (E) -> QueryCondition): Long {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    return filter(predicate).count()
}

@OptIn(ExperimentalContracts::class)
inline fun <E : Any> QueryVector<E>.all(predicate: (E) -> QueryCondition): Boolean {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    return none { !predicate(entity) }
}

@OptIn(ExperimentalContracts::class)
inline fun <E : Any> QueryVector<E>.any(predicate: (E) -> QueryCondition): Boolean {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    return count(predicate) > 0L
}

@OptIn(ExperimentalContracts::class)
inline fun <E : Any> QueryVector<E>.none(predicate: (E) -> QueryCondition): Boolean {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    return count(predicate) == 0L
}

@OptIn(ExperimentalContracts::class)
inline fun <reified E : Any, reified R : Any> QueryVector<E>.funOf(
    column: KProperty<R?>,
    selector: QueryFunctions.(QueryColumn) -> QueryColumn
): R {
    contract {
        callsInPlace(selector, InvocationKind.EXACTLY_ONCE)
    }
    val wrapper = wrapper
    CPI.setSelectColumns(wrapper, mutableListOf())
    wrapper.select(QueryFunctions.selector(column.toQueryColumn()))
    return mapper.selectObjectByQueryAs(wrapper, R::class.java)
}

@OptIn(ExperimentalContracts::class)
inline fun <reified E : Any> QueryVector<E>.find(predicate: (E) -> QueryCondition): E? {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    return firstOrNull(predicate)
}

@OptIn(ExperimentalContracts::class)
inline fun <reified E : Any> QueryVector<E>.findLast(predicate: (E) -> QueryCondition): E? {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    return lastOrNull(predicate)
}

@OptIn(ExperimentalContracts::class)
inline fun <reified E : Any> QueryVector<E>.first(predicate: (E) -> QueryCondition): E {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    return requireNotNull(firstOrNull(predicate))
}

@OptIn(ExperimentalContracts::class)
inline fun <reified E : Any> QueryVector<E>.firstOrNull(predicate: (E) -> QueryCondition): E? {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    return filter(predicate).elementAt(0)
}

inline fun <reified E : Any> QueryVector<E>.first(): E = requireNotNull(firstOrNull())

inline fun <reified E : Any> QueryVector<E>.firstOrNull(): E? = elementAt(0)

@OptIn(ExperimentalContracts::class)
inline fun <reified E : Any> QueryVector<E>.last(predicate: (E) -> QueryCondition): E {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    return requireNotNull(lastOrNull(predicate))
}

@OptIn(ExperimentalContracts::class)
inline fun <reified E : Any> QueryVector<E>.lastOrNull(predicate: (E) -> QueryCondition): E? {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    val filter = filter(predicate)
    val rows = filter.data.rows
    return filter.elementAt((if (rows < 1) Db.selectCountByQuery(filter.wrapper) else rows) - 1)
}

inline fun <reified E : Any> QueryVector<E>.last(): E = requireNotNull(lastOrNull())

inline fun <reified E : Any> QueryVector<E>.lastOrNull(): E? =
    elementAt((if (data.rows < 1) Db.selectCountByQuery(wrapper) else data.rows) - 1)

inline fun <reified E : Any> QueryVector<E>.elementAt(index: Long): E? {
    val idx = data.offset + index
    val wrapper = wrapper.limit(idx, 1)
    return try {
        mapper.selectOneByQuery(wrapper)
    } catch (_: Throwable) {
        null
    }
}

inline operator fun <reified E : Any> QueryVector<E>.get(index: Long): E = requireNotNull(elementAt(index))

inline operator fun <reified E : Any> QueryVector<E>.get(range: IntRange): List<E> = limit(range.first.toLong(), (range.last + 1).toLong()).toList()

fun <E : Any> QueryVector<E>.isEmpty(): Boolean = count() == 0L

fun <E : Any> QueryVector<E>.isNotEmpty(): Boolean = !isEmpty()

inline fun <reified E : Any> QueryVector<E>.push(entity: E, vararg columns: KProperty1<E, *>): Int {
    if (entity is Row) {
        return Db.insert(tableDef.schema, tableDef.tableName, entity)
    }
    return if (columns.isEmpty()) {
        mapper.insert(entity, false)
    } else {
        val row = columns.associateTo(Row()) {
            it.toQueryColumn().name to it(entity)
        }
        println(row)
        Db.insert(tableDef.schema, tableDef.tableName, row)
    }
}

@OptIn(ExperimentalContracts::class)
inline fun <reified E : Any> QueryVector<E>.removeIf(predicate: (E) -> QueryCondition): Int {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    val condition = predicate(entity)
    return if (isRow<E>()) {
        Db.deleteByCondition(tableDef.schema, tableDef.tableName, condition)
    } else {
        mapper.deleteByCondition(condition)
    }
}
