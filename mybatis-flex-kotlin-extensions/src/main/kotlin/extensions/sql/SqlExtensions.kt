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

package com.mybatisflex.kotlin.extensions.sql

import com.mybatisflex.core.query.*
import com.mybatisflex.kotlin.extensions.condition.and
import com.mybatisflex.kotlin.extensions.condition.emptyCondition
import com.mybatisflex.kotlin.extensions.kproperty.column
import com.mybatisflex.kotlin.scope.QueryScope
import com.mybatisflex.kotlin.scope.queryScope
import com.mybatisflex.kotlin.vec.Order
import java.util.function.Consumer
import kotlin.reflect.KProperty

/*
 * sql操作扩展
 * @author KAMOsama
 * @date 2023/8/7
 */

//like------
infix fun QueryColumn.like(other: Any?): QueryCondition = this.like(other)

infix fun QueryColumn.likeRaw(other: Any?): QueryCondition = this.likeRaw(other)

infix fun QueryColumn.likeLeft(other: Any?): QueryCondition = this.likeLeft(other)

infix fun QueryColumn.likeRight(other: Any?): QueryCondition = this.likeRight(other)

infix fun QueryColumn.notLike(other: Any?): QueryCondition = this.notLike(other)

infix fun QueryColumn.notLikeRaw(other: Any?): QueryCondition = this.notLikeRaw(other)

infix fun QueryColumn.notLikeLeft(other: Any?): QueryCondition = this.notLikeLeft(other)

infix fun QueryColumn.notLikeRight(other: Any?): QueryCondition = this.notLikeRight(other)

//comparable
infix fun QueryColumn.eq(other: Any?): QueryCondition = this.eq(other)

@Deprecated("使用 eq 和 null 进行比较可能是个错误。", ReplaceWith("this.isNull"))
infix fun QueryColumn.eq(other: Nothing?): QueryCondition = this.eq(other)

infix fun QueryColumn.ne(other: Any?): QueryCondition = this.ne(other)

@Deprecated("使用 ne 和 null 进行比较可能是个错误。", ReplaceWith("this.isNull"))
infix fun QueryColumn.ne(other: Nothing?): QueryCondition = this.ne(other)

infix fun QueryColumn.gt(other: Any?): QueryCondition = this.gt(other)

infix fun QueryColumn.ge(other: Any?): QueryCondition = this.ge(other)

infix fun QueryColumn.le(other: Any?): QueryCondition = this.le(other)

infix fun QueryColumn.lt(other: Any?): QueryCondition = this.lt(other)

//range
infix fun QueryColumn.between(pair: Pair<Any?, Any?>): QueryCondition = this.between(pair.first, pair.second)

infix fun QueryColumn.between(range: ClosedRange<*>): QueryCondition = this.between(range.start, range.endInclusive)

infix fun QueryColumn.notBetween(pair: Pair<Any?, Any?>): QueryCondition = this.notBetween(pair.first, pair.second)

infix fun QueryColumn.notBetween(range: ClosedRange<*>): QueryCondition =
    this.notBetween(range.start, range.endInclusive)

infix fun QueryColumn.notIn(others: Collection<Any?>): QueryCondition = this.notIn(others)

infix fun QueryColumn.notIn(others: Array<Any?>): QueryCondition = this.notIn(others)

/**
 * @since 1.1.0
 */
inline fun QueryColumn.notIn(scope: QueryScope.() -> Unit): QueryCondition = this.notIn(queryScope(init = scope))

infix fun QueryColumn.`in`(others: Collection<Any?>): QueryCondition = this.`in`(others)

infix fun QueryColumn.`in`(others: Array<Any?>): QueryCondition = this.`in`(others.toList())

infix fun QueryColumn.`in`(range: IntRange): QueryCondition = this.`in`(range.toList())

/**
 * @since 1.1.0
 */
infix fun QueryColumn.inList(others: Collection<Comparable<*>>): QueryCondition {
    if (others.isEmpty()) {
        return emptyCondition()
    }
    return if (others.size == 1) this.eq(others.first()) else this.`in`(others)
}

/**
 * @since 1.1.0
 */
infix fun QueryColumn.inArray(others: Array<out Comparable<*>>): QueryCondition {
    if (others.isEmpty()) {
        return emptyCondition()
    }
    return if (others.size == 1) this.eq(others[0]) else this.`in`(others)
}

/**
 * @since 1.1.0
 */
infix fun QueryColumn.inRange(others: ClosedRange<out Comparable<*>>): QueryCondition =
    if (others.endInclusive == others.start) this.eq(others.start) else this.between(
        others.start,
        others.endInclusive
    )

/**
 * @since 1.1.1
 */
infix fun QueryColumn.`as`(alias: String): QueryColumn = this.`as`(alias)

/**
 * @since 1.1.0
 */
inline fun QueryColumn.`in`(scope: QueryScope.() -> Unit): QueryCondition = this.`in`(queryScope(init = scope))

fun <C : QueryColumn, A : Any> Pair<C, C>.inPair(vararg others: Pair<A, A>): QueryCondition =
    this inPair others.toList()

infix fun <C : QueryColumn, A : Any> Pair<C, C>.inPair(others: Iterable<Pair<A, A>>): QueryCondition =
    others.map { this.first.eq(it.first) and this.second.eq(it.second) }
        .reduceIndexed { i, c1, c2 -> (if (i == 1) Brackets(c1) else c1).or(c2) }

fun <C : QueryColumn, A : Any> Pair<Pair<C, C>, C>.inTriple(vararg others: Pair<Pair<A, A>, A>): QueryCondition =
    this inTriple others.toList()

infix fun <C : QueryColumn, A : Any> Pair<Pair<C, C>, C>.inTriple(others: Iterable<Pair<Pair<A, A>, A>>): QueryCondition =
    others.map { this.first.first.eq(it.first.first) and this.first.second.eq(it.first.second) and this.second.eq(it.second) }
        .reduceIndexed { i, c1, c2 -> (if (i == 1) Brackets(c1) else c1).or(c2) }

//join
@Deprecated(
    "核心库已废除 Joiner.as 方法。详情请看官方库描述。",
    replaceWith = ReplaceWith("this.`as`(alias)"),
    level = DeprecationLevel.ERROR,
)
infix fun <W : QueryWrapper> Joiner<W>.`as`(alias: String?): Joiner<W> = this.`as`(alias)

infix fun <W : QueryWrapper> Joiner<W>.on(on: String?): W = this.on(on)

infix fun <W : QueryWrapper> Joiner<W>.on(on: QueryCondition?): W = this.on(on)

infix fun <W : QueryWrapper> Joiner<W>.on(consumer: Consumer<QueryWrapper?>): W = this.on(consumer)

operator fun QueryColumn.unaryPlus(): QueryOrderBy = this.asc()

operator fun QueryColumn.unaryMinus(): QueryOrderBy = this.desc()

// operator
operator fun QueryColumn.plus(other: QueryColumn): QueryColumn = add(other)

operator fun QueryColumn.plus(other: Number): QueryColumn = add(other)

operator fun QueryColumn.plus(other: KProperty<Number?>): QueryColumn = add(other.column)

operator fun QueryColumn.minus(other: QueryColumn): QueryColumn = subtract(other)

operator fun QueryColumn.minus(other: Number): QueryColumn = subtract(other)

operator fun QueryColumn.minus(other: KProperty<Number?>): QueryColumn = subtract(other.column)

operator fun QueryColumn.times(other: QueryColumn): QueryColumn = multiply(other)

operator fun QueryColumn.times(other: Number): QueryColumn = multiply(other)

operator fun QueryColumn.times(other: KProperty<Number?>): QueryColumn = multiply(other.column)

operator fun QueryColumn.div(other: QueryColumn): QueryColumn = divide(other)

operator fun QueryColumn.div(other: Number): QueryColumn = divide(other)

operator fun QueryColumn.div(other: KProperty<Number?>): QueryColumn = divide(other.column)

// order
fun QueryColumn.toOrd(order: Order = Order.ASC): QueryOrderBy = when (order) {
    Order.ASC -> asc()
    Order.DESC -> desc()
}

operator fun QueryCondition.not(): QueryCondition = QueryMethods.not(this)