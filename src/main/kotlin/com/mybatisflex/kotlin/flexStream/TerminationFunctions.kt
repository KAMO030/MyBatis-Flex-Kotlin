@file:[Suppress("unused")]

package com.example.springbootdemo.flexStream

import com.mybatisflex.annotation.Table
import com.mybatisflex.core.query.*
import com.mybatisflex.core.row.Db
import com.mybatisflex.core.row.Row
import com.mybatisflex.kotlin.flexStream.*
import java.math.BigDecimal
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1


@OptIn(ExperimentalFlexStream::class)
typealias FS<T, O> = FlexStream<T, O>

// !---------------------------------------------- 终端操作 ----------------------------------------------!
@ExperimentalFlexStream
inline fun <T, O : Any, R> FS<T, O>.map(fn: (O) -> R): List<R> {
    return queryChain.list().map(fn)
}

@ExperimentalFlexStream
@OptIn(ExperimentalContracts::class)
inline fun <T, reified O : Any> FS<T, O>.find(fn: (O) -> QueryCondition): O? {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    return filter(fn).firstOrNull()
}

@ExperimentalFlexStream
@OptIn(ExperimentalContracts::class)
inline fun <T, O : Any> FS<T, O>.sumOf(fn: (O) -> KProperty<Number?>): BigDecimal {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    val column = fn(entity).toQueryColumn()
    return queryChain.select(QueryMethods.sum(column)).objAs(BigDecimal::class.java)
}

@ExperimentalFlexStream
fun <T, O : Any> FS<T, O>.sumOf(property: KProperty1<O, Number?>): BigDecimal = sumOf { property }

@ExperimentalFlexStream
@OptIn(ExperimentalContracts::class)
inline fun <T, O : Any> FS<T, O>.avgOf(fn: (O) -> KProperty<Number?>): BigDecimal {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    val column = fn(entity).toQueryColumn()
    return queryChain.select(QueryMethods.avg(column)).objAs(BigDecimal::class.java)
}

@ExperimentalFlexStream
fun <T, O : Any> FS<T, O>.avgOf(property: KProperty1<O, Number?>): BigDecimal = avgOf { property }

@OptIn(ExperimentalContracts::class)
@ExperimentalFlexStream
inline fun <T, O : Any, reified R : Comparable<R>> FS<T, O>.maxOf(fn: (O) -> KProperty<R?>): R {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    val column = fn(entity).toQueryColumn()
    return queryChain.select(QueryFunctions.max(column)).objAs(R::class.java)
}

@ExperimentalFlexStream
inline fun <T, O : Any, reified R : Comparable<R>> FS<T, O>.maxOf(property: KProperty1<O, R?>): R = maxOf { property }

@ExperimentalFlexStream
@OptIn(ExperimentalContracts::class)
inline fun <T, reified O : Any> FS<T, O>.maxBy(fn: (O) -> KProperty<Comparable<*>?>): O? {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }

    val column = fn(entity).toQueryColumn()
    val maxValue = QueryWrapper().select(QueryFunctions.max(column)).from(tableDef)
    return find { column.eq(maxValue) }
}

@ExperimentalFlexStream
inline fun <T, reified O : Any> FS<T, O>.maxBy(property: KProperty1<O, Comparable<*>?>): O? = maxBy { property }

@ExperimentalFlexStream
@OptIn(ExperimentalContracts::class)
inline fun <T, O : Any, reified R : Comparable<R>> FS<T, O>.minOf(fn: (O) -> KProperty<R?>): R {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    val column = fn(entity).toQueryColumn()
    return queryChain.select(QueryFunctions.min(column)).objAs(R::class.java)
}

@ExperimentalFlexStream
inline fun <T, O : Any, reified R : Comparable<R>> FS<T, O>.minOf(property: KProperty1<O, R?>): R = minOf { property }

@ExperimentalFlexStream
@OptIn(ExperimentalContracts::class)
inline fun <T, reified O : Any> FS<T, O>.minBy(fn: (O) -> KProperty<Comparable<*>?>): O? {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    val column = fn(entity).toQueryColumn()
    val maxValue = QueryWrapper().select(QueryFunctions.min(column)).from(tableDef)
    return find { column.eq(maxValue) }
}

@ExperimentalFlexStream
inline fun <T, reified O : Any> FS<T, O>.minBy(property: KProperty1<O, Comparable<*>?>): O? = minBy { property }

@ExperimentalFlexStream
@Suppress("UNCHECKED_CAST")
fun <T, O : Any> FS<T, O>.toList(): List<O> =
    if (Row::class.java.isAssignableFrom(entity.javaClass))
        Db.selectListByQuery(queryChain.from(tableDef)) as List<O>
    else queryChain.list() as List<O>

@ExperimentalFlexStream
@Suppress("UNCHECKED_CAST")
fun <T, O : Any, R> FS<T, O>.toList(asType: Class<R>): List<R> = queryChain.listAs(asType)

@ExperimentalFlexStream
inline fun <T, reified O : Any> FS<T, O>.single(): O =
    if (Row::class.java.isAssignableFrom(O::class.java)) Db.selectOneByQuery(queryChain.from(tableDef)) as O
    else queryChain.one()
        ?: throw IllegalArgumentException("Cannot be converted to ${O::class.java}, because it is null")


@ExperimentalFlexStream
@OptIn(ExperimentalContracts::class)
inline fun <T, reified O : Any> FS<T, O>.single(fn: (O) -> QueryCondition): O {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    return filter(fn).single()
}

@ExperimentalFlexStream
fun <T, O : Any> FS<T, O>.count(): Long = Db.selectCountByQuery(queryChain.from(tableDef))

@ExperimentalFlexStream
@OptIn(ExperimentalContracts::class)
inline fun <T, O : Any> FS<T, O>.count(fn: (O) -> QueryCondition): Long {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    return filter(fn).count()
}

@ExperimentalFlexStream
fun <T, O : Any> FS<T, O>.first(): O = requireNotNull(firstOrNull())

@ExperimentalFlexStream
fun <T, O : Any> FS<T, O>.firstOrNull(): O? = elementAt(0)

@ExperimentalFlexStream
fun <T, O : Any> FS<T, O>.lastOrNull(): O? = elementAt((if (rows < 1) queryChain.count() else rows) - 1)

@ExperimentalFlexStream
fun <T, O : Any> FS<T, O>.last(): O = requireNotNull(lastOrNull())

@ExperimentalFlexStream
fun <T, O : Any> FS<T, O>.elementAt(index: Long): O? {
    val idx = offset + index
    val queryChain: QueryChain<O?>? = queryChain.from(tableDef).limit(idx, 1)
    return if (Row::class.java.isAssignableFrom(entity::class.java)) {
        @Suppress("UNCHECKED_CAST")
        Db.selectOneByQuery(queryChain) as O?
    } else try {
        queryChain?.one()
    } catch (_: Throwable) {
        null
    }
}

@ExperimentalFlexStream
inline operator fun <T, reified O : Any> FS<T, O>.get(index: Long): O = requireNotNull(elementAt(index))

@OptIn(ExperimentalContracts::class)
@ExperimentalFlexStream
inline fun <T, O : Any> FS<T, O>.update(
    entity: O,
    nullable: Boolean = false,
    fn: (O) -> List<KProperty<Any?>>
): Int {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    val properties = fn(entity)
    val schema = tableDef.schema
    val tableName = tableDef.tableName
    return Db.updateByQuery(schema, tableName, Row().apply {
        if (properties.isNotEmpty()) for (it in properties) {
            val value = it.call(entity)
            if (!nullable && value === null) continue
            this[it.toQueryColumn().name] = value
        }
        else for (it in entity::class.java.fields) {
            val value = it[entity]
            if (!nullable && value === null) continue
            this[it.toQueryColumn().name] = it[entity]
        }
    }, queryChain)
}

@ExperimentalFlexStream
fun <T, O : Any> FS<T, O>.update(
    entity: O,
    vararg properties: KProperty1<O, Any?>
): Int = update(entity) { listOf(*properties) }

@ExperimentalFlexStream
@OptIn(ExperimentalContracts::class)
inline fun <T, O : Any> FS<T, O>.add(entity: O, fn: (O) -> List<KProperty<Any?>> = { emptyList() }): Int {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    val properties = fn(entity)
    val schema = tableDef.schema
    val tableName = tableDef.tableName
    return Db.insert(schema, tableName, Row().apply {
        if (properties.isNotEmpty()) properties.forEach {
            this[it.toQueryColumn().name] = it.call(entity)
        }
        else entity::class.java.fields.forEach {
            this[it.toQueryColumn().name] = it[entity]
        }
    })
}

@ExperimentalFlexStream
fun <T, O : Any> FS<T, O>.add(entity: O, vararg properties: KProperty1<O, Any?>): Int =
    add(entity) { listOf(*properties) }

@ExperimentalFlexStream
@OptIn(ExperimentalContracts::class)
inline fun <T, O : Any> FS<T, O>.removeIf(fn: (O) -> QueryCondition): Int {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    val condition = fn(entity)
    val mapper = mapper
    if (mapper != null) {
        return mapper.deleteByCondition(condition)
    }
    val table = entity.javaClass.getAnnotation(Table::class.java) ?: throw IllegalStateException(
        "Mapper is empty and annotation tables are not annotated on entity classes. Therefore, we have no way of knowing which table to delete data from."
    )
    return Db.deleteByCondition(table.schema, table.value, condition)
}

@ExperimentalFlexStream
inline fun <T, O : Any> FS<T, O>.any(fn: (O) -> QueryCondition): Boolean = count(fn) > 0L

@ExperimentalFlexStream
inline fun <T, O : Any> FS<T, O>.none(fn: (O) -> QueryCondition): Boolean = count(fn) == 0L

@ExperimentalFlexStream
inline fun <T, O : Any> FS<T, O>.all(fn: (O) -> QueryCondition): Boolean = none { !fn(entity) }

@ExperimentalFlexStream
inline fun <T, O : Any> FS<T, O>.findLast(fn: (O) -> QueryCondition): O? = filter(fn).lastOrNull()

@ExperimentalFlexStream
fun <T, O : Any> FS<T, O>.isEmpty() = count() == 0L

@ExperimentalFlexStream
fun <T, O : Any> FS<T, O>.isNotEmpty() = !isEmpty()
