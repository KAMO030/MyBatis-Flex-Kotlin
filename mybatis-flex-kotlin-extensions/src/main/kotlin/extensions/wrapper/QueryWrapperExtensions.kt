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
package com.mybatisflex.kotlin.extensions.wrapper

import com.mybatisflex.core.query.*
import com.mybatisflex.core.util.MapperUtil
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

/*
 * QueryWrapper操作扩展
 * @author KAMOsama
 */

// --from
inline fun QueryWrapper.from(init: QueryScope.() -> Unit = {}): QueryWrapper = this.from(queryScope(init = init))

fun QueryWrapper.from(vararg entities: KClass<*>): QueryWrapper = this.from(*entities.map { it.java }.toTypedArray())

infix fun QueryWrapper.from(entity: KClass<*>): QueryWrapper = this.from(entity.java)

inline fun <reified T> QueryWrapper.from(): QueryWrapper = this.from(T::class)

// --join
inline fun <reified Q : QueryWrapper> QueryWrapper.join(enable: Boolean = true): Joiner<Q> =
    this.join(Q::class.java, enable)

inline fun <Q : QueryWrapper> QueryWrapper.join(enable: Boolean = true, init: QueryScope.() -> Unit): Joiner<Q> =
    this.join(queryScope(init = init), enable)

inline fun <reified Q : QueryWrapper> QueryWrapper.innerJoin(enable: Boolean = true): Joiner<Q> =
    this.innerJoin(Q::class.java, enable)

inline fun <Q : QueryWrapper> QueryWrapper.innerJoin(enable: Boolean = true, init: QueryScope.() -> Unit): Joiner<Q> =
    this.innerJoin(queryScope(init = init), enable)

inline fun <reified Q : QueryWrapper> QueryWrapper.crossJoin(enable: Boolean = true): Joiner<Q> =
    this.crossJoin(Q::class.java, enable)

inline fun <Q : QueryWrapper> QueryWrapper.crossJoin(enable: Boolean = true, init: QueryScope.() -> Unit): Joiner<Q> =
    this.crossJoin(queryScope(init = init), enable)

inline fun <reified Q : QueryWrapper> QueryWrapper.leftJoin(enable: Boolean = true): Joiner<Q> =
    this.leftJoin(Q::class.java, enable)

inline fun <Q : QueryWrapper> QueryWrapper.leftJoin(enable: Boolean = true, init: QueryScope.() -> Unit): Joiner<Q> =
    this.leftJoin(queryScope(init = init), enable)

inline fun <reified Q : QueryWrapper> QueryWrapper.rightJoin(enable: Boolean = true): Joiner<Q> =
    this.rightJoin(Q::class.java, enable)

inline fun <Q : QueryWrapper> QueryWrapper.rightJoin(enable: Boolean = true, init: QueryScope.() -> Unit): Joiner<Q> =
    this.rightJoin(queryScope(init = init), enable)

// --select
/**
 * 带范型约束的select，约束只能是某个实体类的属性
 */
inline fun <reified T> QueryWrapper.selectFrom(vararg properties: KProperty1<T, *>): QueryWrapper =
    this.select(*properties.toQueryColumns()).from<T>()

/**
 * 将子查询作为select的字段
 * @since 1.1.0
 */
val QueryWrapper.selectColumn: QueryColumn
    get() = SelectQueryColumn(this)

inline fun QueryWrapper.select(properties: () -> Iterable<KProperty<*>>): QueryWrapper =
    this.select(*properties().toQueryColumns())

fun QueryWrapper.selectProperties(vararg properties: KProperty<*>): QueryWrapper =
    this.select(*properties.toQueryColumns())

fun QueryWrapper.select(entityType: KClass<*>): QueryWrapper =
    this.select(*entityType.defaultColumns)

//as
infix fun QueryWrapper.`as`(alias: String?): QueryWrapper = this.`as`(alias)

// orderBy
infix fun QueryWrapper.orderBy(orderBys: Collection<QueryOrderBy?>): QueryWrapper =
    this.orderBy(*orderBys.toTypedArray())

infix fun QueryWrapper.orderBy(orderBy: QueryOrderBy): QueryWrapper = this.orderBy(orderBy)

// limit
infix fun QueryWrapper.limit(rows: Number): QueryWrapper = this.limit(rows)

infix fun QueryWrapper.limit(pair: Pair<Number?, Number?>): QueryWrapper = this.limit(pair.first, pair.second)

infix fun QueryWrapper.limit(range: IntRange): QueryWrapper = this.limit(range.first, range.last)

// --condition
inline fun QueryWrapper.and(isEffective: Boolean, predicate: () -> QueryCondition): QueryWrapper =
    if (isEffective) and(predicate()) else this

inline fun QueryWrapper.or(isEffective: Boolean, predicate: () -> QueryCondition): QueryWrapper =
    if (isEffective) this.or(predicate()) else this

inline infix fun QueryWrapper.and(predicate: () -> QueryCondition): QueryWrapper = this.and(predicate())

inline infix fun QueryWrapper.or(predicate: () -> QueryCondition): QueryWrapper = this.or(predicate())

infix fun QueryWrapper.and(queryColumn: QueryCondition): QueryWrapper = this.and(queryColumn)

infix fun QueryWrapper.or(queryColumn: QueryCondition): QueryWrapper = this.or(queryColumn)

fun QueryWrapper.whereBy(consumer: (QueryWrapper) -> Unit): QueryWrapper = where(consumer)

inline fun QueryWrapper.whereWith(queryCondition: () -> QueryCondition): QueryWrapper = where(queryCondition())

@OptIn(ExperimentalContracts::class)
inline fun QueryWrapper.having(predicate: () -> QueryCondition): QueryWrapper {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }
    return having(predicate())
}


fun QueryWrapper.andAll(vararg conditions: QueryCondition): QueryWrapper = this and allAnd(*conditions)

fun QueryWrapper.orAll(vararg conditions: QueryCondition): QueryWrapper = this or allOr(*conditions)

val <T : QueryWrapper> T.self: QueryWrapperDevelopEntry<T>
    get() = QueryWrapperDevelopEntry(this)

/**
 * wrapper的内部实现的访问，基于官方CPI而编写。其目的用于简化开发时的
 * 代码编写，避免在开发时频繁地调用官方的API，
 * 同时，也避免在开发时，因为使用官方的API而造成代码的混乱。
 *
 * 因其使用value class进行了内联，故使用时可直接wrapper.self访问而不必担心性能问题。
 *
 * 不直接设计成[QueryWrapper]的扩展方法的原因是，官方在一开始时便为了保证 [QueryWrapper] 方法对于用户的纯净性，
 * 而 framework 又可以通过 [CPI] 来调用 [QueryWrapper] 的其他方法，隐藏设计细节
 * 故而，[QueryWrapperDevelopEntry] 设计为 [QueryWrapper] 的内部实现，
 * 其可以访问 [QueryWrapper] 的所有方法，同时，又可以避免 [QueryWrapper] 方法被污染。
 *
 * @author CloudPlayer
 */
@JvmInline
value class QueryWrapperDevelopEntry<out T : QueryWrapper>(val wrapper: T) {
    var selectColumns: List<QueryColumn>
        get() = CPI.getSelectColumns(wrapper) ?: emptyList()
        set(value) = CPI.setSelectColumns(wrapper, value)

    var queryTables: List<QueryTable>
        get() = CPI.getQueryTables(wrapper) ?: emptyList()
        set(value) = CPI.setQueryTable(wrapper, value)

    var where: QueryCondition?
        get() = CPI.getWhereQueryCondition(wrapper)
        set(value) = CPI.setWhereQueryCondition(wrapper, value)

    var groupBy: List<QueryColumn>
        get() = CPI.getGroupByColumns(wrapper) ?: emptyList()
        set(value) = CPI.setGroupByColumns(wrapper, value)

    var having: QueryCondition?
        get() = CPI.getHavingQueryCondition(wrapper)
        set(value) = CPI.setHavingQueryCondition(wrapper, value)

    val isDistinct: Boolean get() = MapperUtil.hasDistinct(selectColumns)

    var joins: List<Join>
        get() = CPI.getJoins(wrapper) ?: emptyList()
        set(value) = CPI.setJoins(wrapper, value)

    var hint: String?
        get() = CPI.getHint(wrapper)
        set(value) = CPI.setHint(wrapper, value)

    var endFragments: List<String>
        get() = CPI.getEndFragments(wrapper) ?: emptyList()
        set(value) = CPI.setEndFragments(wrapper, value)

    var orderBys: List<QueryOrderBy>
        get() = CPI.getOrderBys(wrapper) ?: emptyList()
        set(value) = CPI.setOrderBys(wrapper, value)

    var context: Map<String, Any>
        get() = CPI.getContext(wrapper) ?: emptyMap()
        set(value) = CPI.setContext(wrapper, value)

    val childSelect: List<QueryWrapper>
        get() = CPI.getChildSelect(wrapper) ?: emptyList()

    val valueArray: Array<out Any?>
        get() = CPI.getValueArray(wrapper) ?: emptyArray()

    val joinValue: Array<out Any?>
        get() = CPI.getJoinValueArray(wrapper) ?: emptyArray()

    val conditionValue: Array<out Any?>
        get() = CPI.getConditionValueArray(wrapper) ?: emptyArray()

    var dataSource: String?
        get() = CPI.getDataSource(wrapper)
        set(value) = CPI.setDataSource(wrapper, value)

    var unions: List<UnionWrapper>
        get() = CPI.getUnions(wrapper) ?: emptyList()
        set(value) = CPI.setUnions(wrapper, value)

    var offset: Long
        get() = CPI.getLimitOffset(wrapper)
        set(value) = CPI.setLimitOffset(wrapper, value)

    var rows: Long
        get() = CPI.getLimitRows(wrapper)
        set(value) = CPI.setLimitRows(wrapper, value)

    fun setFromIfNecessary(schema: String, tableName: String) = CPI.setFromIfNecessary(wrapper, schema, tableName)

}
