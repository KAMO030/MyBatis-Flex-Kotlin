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

import com.mybatisflex.core.query.*
import com.mybatisflex.core.row.Db
import com.mybatisflex.core.row.Row
import com.mybatisflex.kotlin.extensions.kproperty.column
import com.mybatisflex.kotlin.extensions.sql.not
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
    val column = selector(entity).column
    val maxValue = QueryWrapper().select(QueryFunctions.max(column)).from(queryTable)
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
    val column = selector(entity).column
    val minValue = QueryWrapper().select(QueryFunctions.min(column)).from(queryTable)
    return find { column.eq(minValue) }
}

@OptIn(ExperimentalContracts::class)
inline fun <reified E : Any, reified R : Number> QueryVector<E>.avgOf(selector: (E) -> KProperty<R?>): BigDecimal {
    contract {
        callsInPlace(selector, InvocationKind.EXACTLY_ONCE)
    }
    val column = selector(entity).column
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
    val column = selector(entity).column
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
    wrapper.select(QueryFunctions.selector(column.column))
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

inline operator fun <reified E : Any> QueryVector<E>.get(range: IntRange): List<E> =
    limit(range.first.toLong(), (range.last + 1).toLong()).toList()

fun <E : Any> QueryVector<E>.isEmpty(): Boolean = count() == 0L

fun <E : Any> QueryVector<E>.isNotEmpty(): Boolean = !isEmpty()

inline fun <reified E : Any> QueryVector<E>.push(entity: E, vararg columns: KProperty1<E, *>): Int {
    if (entity is Row) {
        return Db.insert(queryTable.schema, queryTable.name, entity)
    }
    return if (columns.isEmpty()) {
        mapper.insert(entity, false)
    } else {
        val row = columns.associateTo(Row()) {
            it.column.name to it(entity)
        }
        Db.insert(queryTable.schema, queryTable.name, row)
    }
}

@OptIn(ExperimentalContracts::class)
inline fun <reified E : Any> QueryVector<E>.removeIf(predicate: (E) -> QueryCondition): Int {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    val condition = predicate(entity)
    return mapper.deleteByCondition(condition)
}
