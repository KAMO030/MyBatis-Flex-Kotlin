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
import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryCondition
import com.mybatisflex.core.query.QueryOrderBy
import com.mybatisflex.core.table.TableInfoFactory
import com.mybatisflex.kotlin.extensions.sql.*
import com.mybatisflex.kotlin.vec.Order
import java.lang.reflect.Field
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

fun <T> KProperty<T?>.toQueryColumn(): QueryColumn = requireNotNull(javaField) {
    "Cannot convert to ${Field::class.java.canonicalName} because the property has no backing field."
}.toQueryColumn()

fun Field.toQueryColumn(): QueryColumn {
    val from = declaringClass
    val tableInfo = TableInfoFactory.ofEntityClass(from)
    return tableInfo.getQueryColumnByProperty(name) ?: throw NoSuchElementException(
        "The attribute $this of class $from could not find the corresponding QueryColumn"
    )
}

fun <T> KProperty<T?>.toOrd(order: Order = Order.ASC): QueryOrderBy = toQueryColumn().toOrd(order)

infix fun <T : Comparable<T>> KProperty<T?>.eq(other: T): QueryCondition = toQueryColumn().eq(other)

infix fun <T : Comparable<T>> KProperty<T?>.gt(other: T): QueryCondition = toQueryColumn().gt(other)

infix fun <T : Comparable<T>> KProperty<T?>.ge(other: T): QueryCondition = toQueryColumn().ge(other)

infix fun <T : Comparable<T>> KProperty<T?>.lt(other: T): QueryCondition = toQueryColumn().lt(other)

infix fun <T : Comparable<T>> KProperty<T?>.le(other: T): QueryCondition = toQueryColumn().le(other)

infix fun <T : Comparable<T>> KProperty<T?>.between(other: ClosedRange<T>): QueryCondition =
    toQueryColumn().between(other.start, other.endInclusive)

infix fun <T : Comparable<T>> KProperty<T?>.between(other: Pair<T, T>): QueryCondition =
    toQueryColumn().between(other.first, other.second)

infix fun <T : Comparable<T>> KProperty<T?>.notBetween(other: ClosedRange<T>): QueryCondition =
    toQueryColumn().notBetween(other.start, other.endInclusive)

infix fun <T : Comparable<T>> KProperty<T?>.notBetween(other: Pair<T, T>): QueryCondition =
    toQueryColumn().notBetween(other.first, other.second)

infix fun KProperty<String?>.like(other: String): QueryCondition = toQueryColumn().likeRaw(other)

infix fun KProperty<String?>.startsWith(other: String): QueryCondition = toQueryColumn().likeLeft(other)

infix fun KProperty<String?>.endsWith(other: String): QueryCondition = toQueryColumn().likeRight(other)

infix fun KProperty<String?>.contains(other: String): QueryCondition = toQueryColumn().like(other)

infix fun KProperty<String?>.notLike(other: String): QueryCondition =
    QueryCondition.create(toQueryColumn(), SqlConsts.NOT_LIKE, other)

infix fun KProperty<Int?>.`in`(other: IntRange): QueryCondition = this inList other.toList()

infix fun <T : Comparable<T>> KProperty<T?>.`in`(other: Collection<T>): QueryCondition = this inList other

infix fun <T : Comparable<T>> KProperty<T?>.`in`(other: Array<out T>): QueryCondition = this inArray other

infix fun <T : Comparable<T>> KProperty<T?>.inList(other: Collection<T>): QueryCondition {
    require(other.isNotEmpty()) {
        "The collection must not be empty."
    }
    val queryColumn = toQueryColumn()
    return if (other.size == 1) queryColumn.eq(other.first()) else queryColumn.`in`(other)
}

infix fun <T : Comparable<T>> KProperty<T?>.inArray(other: Array<out T>): QueryCondition {
    require(other.isNotEmpty()) {
        "The array must not be empty."
    }
    val queryColumn = toQueryColumn()
    return if (other.size == 1) queryColumn.eq(other[0]) else queryColumn.`in`(other)
}

infix fun <T> KProperty<T?>.alias(other: String): QueryColumn = toQueryColumn().`as`(other)

infix fun <T> KProperty<T?>.`as`(other: String): QueryColumn = toQueryColumn().`as`(other)

operator fun <T : Number> KProperty<T?>.plus(other: QueryColumn): QueryColumn = toQueryColumn() + other

operator fun <T : Number> KProperty<T?>.plus(other: KProperty<T?>): QueryColumn =
    toQueryColumn() + other.toQueryColumn()

operator fun <T : Number> KProperty<T?>.plus(other: T): QueryColumn = toQueryColumn() + other

operator fun <T : Number> KProperty<T?>.minus(other: QueryColumn): QueryColumn = toQueryColumn() - other

operator fun <T : Number> KProperty<T?>.minus(other: KProperty<T?>): QueryColumn =
    toQueryColumn() - other.toQueryColumn()

operator fun <T : Number> KProperty<T?>.minus(other: T): QueryColumn = toQueryColumn() - other

operator fun <T : Number> KProperty<T?>.times(other: QueryColumn): QueryColumn = toQueryColumn() * other

operator fun <T : Number> KProperty<T?>.times(other: KProperty<T?>): QueryColumn =
    toQueryColumn() * other.toQueryColumn()

operator fun <T : Number> KProperty<T?>.times(other: T): QueryColumn = toQueryColumn() * other

operator fun <T : Number> KProperty<T?>.div(other: QueryColumn): QueryColumn = toQueryColumn() / other

operator fun <T : Number> KProperty<T?>.div(other: KProperty<T?>): QueryColumn = toQueryColumn() / other.toQueryColumn()

operator fun <T : Number> KProperty<T?>.div(other: T): QueryColumn = toQueryColumn() / other

fun <T> KProperty<T?>.isNull(): QueryCondition = toQueryColumn().isNull

fun <T> KProperty<T?>.isNotNull(): QueryCondition = toQueryColumn().isNotNull

