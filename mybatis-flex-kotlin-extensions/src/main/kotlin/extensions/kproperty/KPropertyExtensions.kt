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
package com.mybatisflex.kotlin.extensions.kproperty

import com.mybatisflex.core.constant.SqlConsts
import com.mybatisflex.core.query.Brackets
import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryCondition
import com.mybatisflex.core.query.QueryOrderBy
import com.mybatisflex.core.table.TableInfoFactory
import com.mybatisflex.kotlin.extensions.condition.and
import com.mybatisflex.kotlin.extensions.db.tableInfo
import com.mybatisflex.kotlin.extensions.sql.*
import com.mybatisflex.kotlin.vec.Order
import java.lang.reflect.Field
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaField

/*
 * KProperty操作扩展
 * @author 卡莫sama,CloudPlayer
 */

/**
 * 实例引用时只能用此属性，（如：it::id.column）
 */
val KProperty<*>.column: QueryColumn
    get() = requireNotNull(javaField) {
        "Cannot convert to ${Field::class.java.canonicalName} because the property has no backing field."
    }.toQueryColumn()

/**
 * 类型直接引用用此方法更好，（如：Account::id.column()）
 */
inline fun <reified T, V> KProperty1<T, V>.column(): QueryColumn =
    TableInfoFactory.ofEntityClass(T::class.java).getQueryColumnByProperty(name) ?: throw NoSuchElementException(
        "The attribute $this of class ${T::class.java} could not find the corresponding QueryColumn"
    )

val KClass<*>.defaultColumns: Array<out QueryColumn>
    get() = tableInfo.defaultQueryColumn.toTypedArray()

val <T: Any> KClass<T>.allColumns: QueryColumn
    get() = tableInfo.buildQueryColumn("*")

fun Field.toQueryColumn(): QueryColumn {
    val from = declaringClass
    val tableInfo = TableInfoFactory.ofEntityClass(from)
    return tableInfo.getQueryColumnByProperty(name) ?: throw NoSuchElementException(
        "The attribute $this of class $from could not find the corresponding QueryColumn"
    )
}

fun <T : KProperty<*>> Array<T>.toQueryColumns(): Array<QueryColumn> =
    map { it.column }.toTypedArray()

fun <T : KProperty<*>> Iterable<T>.toQueryColumns(): Array<QueryColumn> =
    map { it.column }.toTypedArray()

fun <T> KProperty<T?>.toOrd(order: Order = Order.ASC): QueryOrderBy = column.toOrd(order)

infix fun <T> KProperty<T?>.eq(other: T): QueryCondition = column.eq(other)

infix fun <T> KProperty<T?>.eq(other: QueryColumn): QueryCondition = column.eq(other)

infix fun <T> KProperty<T?>.eq(other: KProperty<T?>): QueryCondition = column.eq(other.column)

infix fun <T : Comparable<T>> KProperty<T?>.gt(other: T): QueryCondition = column.gt(other)

infix fun <T : Comparable<T>> KProperty<T?>.gt(other: QueryColumn): QueryCondition = column.gt(other)

infix fun <T : Comparable<T>> KProperty<T?>.gt(other: KProperty<T?>): QueryCondition = column.gt(other)

infix fun <T : Comparable<T>> KProperty<T?>.ge(other: T): QueryCondition = column.ge(other)

infix fun <T : Comparable<T>> KProperty<T?>.ge(other: QueryColumn): QueryCondition = column.ge(other)

infix fun <T : Comparable<T>> KProperty<T?>.ge(other: KProperty<T?>): QueryCondition = column.ge(other)

infix fun <T : Comparable<T>> KProperty<T?>.lt(other: T): QueryCondition = column.lt(other)

infix fun <T : Comparable<T>> KProperty<T?>.lt(other: QueryColumn): QueryCondition = column.lt(other)

infix fun <T : Comparable<T>> KProperty<T?>.lt(other: KProperty<T?>): QueryCondition = column.lt(other)

infix fun <T : Comparable<T>> KProperty<T?>.le(other: T): QueryCondition = column.le(other)

infix fun <T : Comparable<T>> KProperty<T?>.le(other: QueryColumn): QueryCondition = column.le(other)

infix fun <T : Comparable<T>> KProperty<T?>.le(other: KProperty<T?>): QueryCondition = column.le(other)

infix fun <T : Comparable<T>> KProperty<T?>.between(other: ClosedRange<T>): QueryCondition =
    column.between(other.start, other.endInclusive)

infix fun <T : Comparable<T>> KProperty<T?>.between(other: Pair<T, T>): QueryCondition =
    column.between(other.first, other.second)

infix fun <T : Comparable<T>> KProperty<T?>.notBetween(other: ClosedRange<T>): QueryCondition =
    column.notBetween(other.start, other.endInclusive)

infix fun <T : Comparable<T>> KProperty<T?>.notBetween(other: Pair<T, T>): QueryCondition =
    column.notBetween(other.first, other.second)

infix fun KProperty<String?>.like(other: String): QueryCondition = column.likeRaw(other)

infix fun KProperty<String?>.startsWith(other: String): QueryCondition = column.likeLeft(other)

infix fun KProperty<String?>.endsWith(other: String): QueryCondition = column.likeRight(other)

infix fun KProperty<String?>.contains(other: String): QueryCondition = column.like(other)

infix fun KProperty<String?>.notLike(other: String): QueryCondition =
    QueryCondition.create(column, SqlConsts.NOT_LIKE, other)

infix fun <T : Comparable<T>> KProperty<T?>.`in`(other: ClosedRange<T>): QueryCondition = this inRange other

infix fun <T : Comparable<T>> KProperty<T?>.`in`(other: Collection<T>): QueryCondition = this inList other

fun <T : Comparable<T>> KProperty<T?>.`in`(vararg other: T): QueryCondition = this inArray other

fun <T : Comparable<T>, E : Comparable<E>> Pair<KProperty<T?>, KProperty<E?>>.inPair(vararg others: Pair<T, E>): QueryCondition =
    this inPair others.toList()

infix fun <T : Comparable<T>, E : Comparable<E>> Pair<KProperty<T?>, KProperty<E?>>.inPair(others: Iterable<Pair<T, E>>): QueryCondition =
    others.map { this.first.eq(it.first) and this.second.eq(it.second) }
        .reduceIndexed { i, c1, c2 -> (if (i == 1) Brackets(c1) else c1).or(c2) }


fun <A : Comparable<A>, B : Comparable<B>, C : Comparable<C>> Pair<Pair<KProperty<A?>, KProperty<B?>>, KProperty<C?>>.inTriple(
    vararg others: Pair<Pair<A, B>, C>
): QueryCondition =
    this inTriple others.toList()

infix fun <A : Comparable<A>, B : Comparable<B>, C : Comparable<C>> Pair<Pair<KProperty<A?>, KProperty<B?>>, KProperty<C?>>.inTriple(
    others: Iterable<Pair<Pair<A, B>, C>>
): QueryCondition =
    others.map { this.first.first.eq(it.first.first) and this.first.second.eq(it.first.second) and this.second.eq(it.second) }
        .reduceIndexed { i, c1, c2 -> (if (i == 1) Brackets(c1) else c1).or(c2) }

infix fun <T : Comparable<T>> KProperty<T?>.inList(other: Collection<T>): QueryCondition {
    require(other.isNotEmpty()) {
        "The collection must not be empty."
    }
    val queryColumn = column
    return if (other.size == 1) queryColumn.eq(other.first()) else queryColumn.`in`(other)
}

infix fun <T : Comparable<T>> KProperty<T?>.inArray(other: Array<out T>): QueryCondition {
    require(other.isNotEmpty()) {
        "The array must not be empty."
    }
    val queryColumn = column
    return if (other.size == 1) queryColumn.eq(other[0]) else queryColumn.`in`(other)
}

infix fun <T : Comparable<T>> KProperty<T?>.inRange(other: ClosedRange<out T>): QueryCondition {
    val queryColumn = column
    return if (other.endInclusive == other.start) queryColumn.eq(other.start) else queryColumn.between(
        other.start,
        other.endInclusive
    )
}

infix fun <T> KProperty<T?>.alias(other: String): QueryColumn = column.`as`(other)

infix fun <T> KProperty<T?>.`as`(other: String): QueryColumn = column.`as`(other)

operator fun <T : Number> KProperty<T?>.plus(other: QueryColumn): QueryColumn = column + other

operator fun <T : Number> KProperty<T?>.plus(other: KProperty<T?>): QueryColumn = column + other.column

operator fun <T : Number> KProperty<T?>.plus(other: T): QueryColumn = column + other

operator fun <T : Number> KProperty<T?>.minus(other: QueryColumn): QueryColumn = column - other

operator fun <T : Number> KProperty<T?>.minus(other: KProperty<T?>): QueryColumn = column - other.column

operator fun <T : Number> KProperty<T?>.minus(other: T): QueryColumn = column - other

operator fun <T : Number> KProperty<T?>.times(other: QueryColumn): QueryColumn = column * other

operator fun <T : Number> KProperty<T?>.times(other: KProperty<T?>): QueryColumn = column * other.column

operator fun <T : Number> KProperty<T?>.times(other: T): QueryColumn = column * other

operator fun <T : Number> KProperty<T?>.div(other: QueryColumn): QueryColumn = column / other

operator fun <T : Number> KProperty<T?>.div(other: KProperty<T?>): QueryColumn = column / other.column

operator fun <T : Number> KProperty<T?>.div(other: T): QueryColumn = column / other

operator fun <T : Comparable<T>> KProperty<T?>.unaryPlus(): QueryOrderBy = this.column.asc()

operator fun <T : Comparable<T>> KProperty<T?>.unaryMinus(): QueryOrderBy = this.column.desc()

val KProperty<*>.isNull: QueryCondition get() = column.isNull

val KProperty<*>.isNotNull: QueryCondition get() = column.isNotNull
