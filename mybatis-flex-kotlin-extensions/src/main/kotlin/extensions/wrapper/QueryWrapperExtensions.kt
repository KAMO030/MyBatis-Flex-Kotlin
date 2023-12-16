package com.mybatisflex.kotlin.extensions.wrapper

import com.mybatisflex.core.query.*
import com.mybatisflex.core.util.MapperUtil
import com.mybatisflex.kotlin.extensions.kproperty.defaultColumns
import com.mybatisflex.kotlin.extensions.kproperty.toQueryColumns
import com.mybatisflex.kotlin.scope.QueryScope
import com.mybatisflex.kotlin.scope.queryScope
import java.util.function.Consumer
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/*
 * QueryWrapper操作扩展
 * @author KAMOsama
 */

inline fun QueryWrapper.from(init: QueryScope.() -> Unit = {}): QueryWrapper = this.from(queryScope(init = init))

fun QueryWrapper.from(vararg entities: KClass<*>): QueryWrapper = this.from(*entities.map { it.java }.toTypedArray())

infix fun QueryWrapper.from(entity: KClass<*>): QueryWrapper = this.from(entity.java)

inline fun QueryWrapper.select(properties: () -> Iterable<KProperty<*>>): QueryWrapper =
    this.select(*properties().toQueryColumns())

fun QueryWrapper.select(vararg properties: KProperty<*>): QueryWrapper =
    this.select(*properties.toQueryColumns())

fun QueryWrapper.select(entityType: KClass<*>): QueryWrapper =
    this.select(*entityType.defaultColumns)

val QueryWrapper.self
    get() = QueryWrapperDevelopEntry(this)

fun QueryWrapper.and(isEffective: Boolean, predicate: () -> QueryCondition): QueryWrapper =
    if (isEffective) and(predicate()) else this

fun QueryWrapper.or(isEffective: Boolean, predicate: () -> QueryCondition): QueryWrapper =
    if (isEffective) this.or(predicate()) else this

inline infix fun QueryWrapper.and(predicate: () -> QueryCondition): QueryWrapper = this.and(predicate())

inline infix fun QueryWrapper.or(predicate: () -> QueryCondition): QueryWrapper = this.or(predicate())

infix fun QueryWrapper.and(queryColumn: QueryCondition): QueryWrapper = this.and(queryColumn)

infix fun QueryWrapper.or(queryColumn: QueryCondition): QueryWrapper = this.or(queryColumn)

@Deprecated("Use `KtWhere` instead.", ReplaceWith("KtWhere{ queryCondition }"))
fun QueryWrapper.where(queryCondition: QueryCondition, consumer: Consumer<QueryWrapper>): QueryWrapper =
    and(queryCondition).where(consumer)

inline fun QueryWrapper.ktWhere(queryCondition: () -> QueryCondition): QueryWrapper = where(queryCondition())

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
value class QueryWrapperDevelopEntry(val wrapper: QueryWrapper) {
    var selectColumns: List<QueryColumn>
        get() = CPI.getSelectColumns(wrapper) ?: emptyList()
        set(value) = CPI.setSelectColumns(wrapper, value)

    var queryTables: List<QueryTable>
        get() = CPI.getQueryTables(wrapper) ?: emptyList()
        set(value) = CPI.setQueryTable(wrapper, value)

    var where: QueryCondition?
        get() = CPI.getWhereQueryCondition(wrapper)
        set(value) = CPI.setWhereQueryCondition(wrapper, requireNotNull(value) {
            "An error occurred while setting `where`, QueryCondition must not be empty."
        })

    var groupBy: List<QueryColumn>
        get() = CPI.getGroupByColumns(wrapper) ?: emptyList()
        set(value) = CPI.setGroupByColumns(wrapper, value)

    var having: QueryCondition?
        get() = CPI.getHavingQueryCondition(wrapper)
        set(value) = CPI.setHavingQueryCondition(wrapper, value)

    val isDistinct: Boolean get() = MapperUtil.hasDistinct(selectColumns)

    var orderBys: List<QueryOrderBy>
        get() = CPI.getOrderBys(wrapper) ?: emptyList()
        set(value) = CPI.setOrderBys(wrapper, value)

    var context: Map<String, Any>
        get() = CPI.getContext(wrapper)
        set(value) = CPI.setContext(wrapper, value)

    val childSelect: List<QueryWrapper>
        get() = CPI.getChildSelect(wrapper)

    val valueArray: Array<out Any?> get() = CPI.getValueArray(wrapper)

    val joinValue: Array<out Any?> get() = CPI.getJoinValueArray(wrapper)

    val conditionValue: Array<out Any?> get() = CPI.getConditionValueArray(wrapper)

    var dataSource: String
        get() = CPI.getDataSource(wrapper)
        set(value) = CPI.setDataSource(wrapper, value)

    var unions: List<UnionWrapper>
        get() = CPI.getUnions(wrapper)
        set(value) = CPI.setUnions(wrapper, value)

    var offset: Long
        get() = CPI.getLimitOffset(wrapper)
        set(value) = CPI.setLimitOffset(wrapper, value)

    var rows: Long
        get() = CPI.getLimitRows(wrapper)
        set(value) = CPI.setLimitRows(wrapper, value)
}

@OptIn(ExperimentalContracts::class)
inline fun QueryWrapper.having(predicate: () -> QueryCondition): QueryWrapper {
    contract {
        callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
    }

    return having(predicate())
}
