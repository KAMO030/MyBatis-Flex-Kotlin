package com.mybatisflex.kotlin.extensions.wrapper

import com.mybatisflex.core.query.*
import com.mybatisflex.kotlin.extensions.condition.allAnd
import com.mybatisflex.kotlin.extensions.condition.allOr
import com.mybatisflex.kotlin.extensions.kproperty.defaultColumns
import com.mybatisflex.kotlin.extensions.kproperty.toQueryColumns
import com.mybatisflex.kotlin.scope.QueryScope
import com.mybatisflex.kotlin.scope.queryScope
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

// --from
inline fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.from(init: QueryScope.() -> Unit = {}): M =
    this.from(queryScope(init = init))

fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.from(vararg entities: KClass<*>): M =
    this.from(*entities.map { it.java }.toTypedArray())

infix fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.from(entity: KClass<*>): M =
    this.from(entity.java)

inline fun <reified E, M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.from(): M = this.from(E::class)

// --join
inline fun <reified E, M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.join(enable: Boolean = true): Joiner<M> =
    this.join(E::class.java, enable)

inline fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.join(
    enable: Boolean = true,
    init: QueryScope.() -> Unit,
): Joiner<M> =
    this.join(queryScope(init = init), enable)

inline fun <reified E, M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.innerJoin(enable: Boolean = true): Joiner<M> =
    this.innerJoin(E::class.java, enable)

inline fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.innerJoin(
    enable: Boolean = true,
    init: QueryScope.() -> Unit,
): Joiner<M> =
    this.innerJoin(queryScope(init = init), enable)

inline fun <reified E, M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.crossJoin(enable: Boolean = true): Joiner<M> =
    this.crossJoin(E::class.java, enable)

inline fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.crossJoin(
    enable: Boolean = true,
    init: QueryScope.() -> Unit,
): Joiner<M> =
    this.crossJoin(queryScope(init = init), enable)

inline fun <reified E, M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.leftJoin(enable: Boolean = true): Joiner<M> =
    this.leftJoin(E::class.java, enable)

inline fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.leftJoin(
    enable: Boolean = true,
    init: QueryScope.() -> Unit,
): Joiner<M> =
    this.leftJoin(queryScope(init = init), enable)

inline fun <reified E, M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.rightJoin(enable: Boolean = true): Joiner<M> =
    this.rightJoin(E::class.java, enable)

inline fun <reified M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.rightJoin(
    enable: Boolean = true,
    init: QueryScope.() -> Unit,
): Joiner<M> =
    this.rightJoin(queryScope(init = init), enable)

// --select
/**
 * 带范型约束的select，约束只能是某个实体类的属性
 */
inline fun <reified E, M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.selectFrom(vararg properties: KProperty1<E, *>): QueryWrapper =
    this.select(*properties.toQueryColumns()).from<E>()

inline fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.select(properties: () -> Iterable<KProperty<*>>): QueryWrapper =
    this.select(*properties().toQueryColumns())

fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.selectProperties(vararg properties: KProperty<*>): QueryWrapper =
    this.select(*properties.toQueryColumns())

fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.select(entityType: KClass<*>): QueryWrapper =
    this.select(*entityType.defaultColumns)

//as
infix fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.`as`(alias: String?): M = this.`as`(alias)

// orderBy
infix fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.orderBy(orderBys: Collection<QueryOrderBy?>): M =
    this.orderBy(*orderBys.toTypedArray())

infix fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.orderBy(orderBy: QueryOrderBy): M =
    this.orderBy(orderBy)

// limit
infix fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.limit(rows: Number): M = this.limit(rows)

infix fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.limit(pair: Pair<Number?, Number?>): M =
    this.limit(pair.first, pair.second)

infix fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.limit(range: IntRange): M =
    this.limit(range.first, range.last)

// --condition
@Suppress("UNCHECKED_CAST")
inline fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.and(
    isEffective: Boolean,
    predicate: () -> QueryCondition,
): M =
    if (isEffective) and(predicate()) else this as M

@Suppress("UNCHECKED_CAST")
inline fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.or(
    isEffective: Boolean,
    predicate: () -> QueryCondition,
): M =
    if (isEffective) this.or(predicate()) else this as M

inline infix fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.and(predicate: () -> QueryCondition): M =
    this.and(predicate())

inline infix fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.or(predicate: () -> QueryCondition): M =
    this.or(predicate())

infix fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.and(queryColumn: QueryCondition): M =
    this.and(queryColumn)

infix fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.or(queryColumn: QueryCondition): M =
    this.or(queryColumn)

fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.whereBy(consumer: (QueryWrapper) -> Unit): M =
    where(consumer)

inline fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.whereWith(queryCondition: () -> QueryCondition): M =
    where(queryCondition())

@OptIn(ExperimentalContracts::class)
inline fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.having(predicate: () -> QueryCondition): M {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    return having(predicate())
}


fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.andAll(vararg conditions: QueryCondition): M =
    this and allAnd(*conditions)

fun <M : QueryWrapperAdapter<M>> QueryWrapperAdapter<M>.orAll(vararg conditions: QueryCondition): M =
    this or allOr(*conditions)