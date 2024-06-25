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
package com.mybatisflex.kotlin.extensions.kproperty

import com.mybatisflex.annotation.Column
import com.mybatisflex.core.query.*
import com.mybatisflex.core.table.TableInfoFactory
import com.mybatisflex.kotlin.extensions.condition.and
import com.mybatisflex.kotlin.extensions.db.queryTable
import com.mybatisflex.kotlin.extensions.db.tableInfo
import com.mybatisflex.kotlin.extensions.sql.*
import com.mybatisflex.kotlin.scope.*
import com.mybatisflex.kotlin.vec.Order
import org.apache.ibatis.type.UnknownTypeHandler
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.jvmErasure

/*
 * KProperty操作扩展
 * @author KAMOsama,CloudPlayer
 */

/**
 * 根据 [KProperty] 解析 [QueryColumn]，用于弥补 Kotlin 中无法直接获取 Java 中的方法引用的问题。
 *
 * 特指 XXX::getId 这种直接意义上的方法引用。
 *
 * @author CloudPlayer
 * @exception IllegalArgumentException 当此属性没有 backing field 时抛出，或找不到其 [instanceParameter] 时抛出。
 * @exception NoSuchElementException 此属性不是枚举类中的属性，返回值类型不在 [TableInfoFactory.defaultSupportColumnTypes] 之中，且其 [Column.typeHandler] 为 [UnknownTypeHandler] 时。
 */
val KProperty<*>.column: QueryColumn
    get() = if (this is KProperty0<*>) {
        requireNotNull(javaField) {
            "Unable to find the corresponding QueryColumn from `$this` without a java field."
        }.column
    } else {
        requireNotNull(instanceParameter) {
            "Unable to find the entity class in which property $this is located."
        }.type.jvmErasure.tableInfo.getQueryColumnByProperty(name) ?: throwNoSuchElementException()
    }


/**
 * 同 [KProperty.column] ，但是在没有找到对应 [QueryColumn] 时不会抛出异常，而是返回 null。
 *
 * @author CloudPlayer
 * @see KProperty.column
 */
val KProperty<*>.columnOrNull: QueryColumn?
    get() = instanceParameter?.let {
        TableInfoFactory.ofEntityClass(it.type.jvmErasure.java).getQueryColumnByProperty(name)
    } ?: javaField?.columnOrNull

private fun KProperty<*>.throwNoSuchElementException(): Nothing = requireNotNull(javaField) {
    "Unable to find the corresponding QueryColumn from `$this` without a java field."
}.throwNoSuchElementException(toString(), returnType.toString())

/**
 * 通过解析 [Column] 以抛出附上详细信息的 [NoSuchElementException] 。
 *
 * 具体报错原因请见 [KProperty.column] 。
 *
 * @param from 这个 [Column] 从哪里（字段，属性）来。
 * @param typeName 对应的字段或属性的类型签名（即 [Field.getGenericType] 或 [KProperty.returnType]）。
 * @see KProperty.column
 * @author CloudPlayer
 */
private fun Column?.throwNoSuchElementException(from: String, typeName: String): Nothing {
    val baseReason = "The corresponding QueryColumn cannot be found by field `$from` because the field's type `$typeName` is an illegal type"
    if (this === null) {
        throw NoSuchElementException(baseReason)
    }
    if (ignore) {
        throw NoSuchElementException("$baseReason and the value of this property in the `${Column::class.java}` is true.")
    }
    if (typeHandler.java == UnknownTypeHandler::class.java) {
        throw NoSuchElementException("$baseReason and the property of the typeHandler is UnknownTypeHandler.")
    }
    throw NoSuchElementException(baseReason)
}

/**
 * [KProperty1.column] 的内联形式。
 *
 * @see KProperty1.column
 * @author CloudPlayer
 */
inline fun <reified T : Any> KProperty1<T, *>.column(): QueryColumn = column(T::class.java)

/**
 * 通过 [KProperty1] 来构建 [QueryColumn] ，明确指定类来避免父类继承问题。
 *
 * @throws NoSuchElementException 找不到对应的 [QueryColumn] 时。具体规则详见 [KProperty.column]。
 * @author CloudPlayer
 */
fun <T : Any> KProperty1<T, *>.column(entityClass: Class<out T>): QueryColumn =
    TableInfoFactory.ofEntityClass(entityClass).getQueryColumnByProperty(name)
        ?: throw NoSuchElementException("The attribute $this of class $entityClass could not find the corresponding QueryColumn")

val KClass<*>.defaultColumns: Array<out QueryColumn>
    get() = tableInfo.defaultQueryColumn.toTypedArray()

val <T : Any> KClass<T>.allColumns: QueryColumn
    get() = QueryColumn(queryTable, "*")

/**
 * 通过 [Field] 来构建 [QueryColumn] 。用于在 [KProperty] 是 [KProperty0] 时这种已实例化属性特殊情况下获取声明该属性所在的类。
 *
 * @author CloudPlayer
 * @see KProperty.column
 * @throws NoSuchElementException 如果无法通过其获取 [QueryColumn] 。详情请见 [KProperty.column] 。
 * @throws IllegalArgumentException 当字段为静态时。
 */
val Field.column: QueryColumn
    get() = columnOrNull ?: if (Modifier.isStatic(modifiers)) {
        throw IllegalArgumentException("QueryColumn cannot be found via field because field is static.")
    } else {
        throwNoSuchElementException()
    }

/**
 * 同 [Field.column] ，但在其是静态或无法获取 [QueryColumn] 时，返回 null。
 * @see Field.column
 * @author CloudPlayer
 */
val Field.columnOrNull: QueryColumn?
    get() = TableInfoFactory.ofEntityClass(declaringClass).getQueryColumnByProperty(name)


private fun Field.throwNoSuchElementException(from: String? = null, typeName: String? = null): Nothing {
    getAnnotation(Column::class.java).throwNoSuchElementException(from ?: toGenericString(), typeName ?: genericType.typeName)
}

fun <T : KProperty<*>> Array<T>.toQueryColumns(): Array<out QueryColumn> =
    map { it.column }.toTypedArray()

fun <T : KProperty<*>> Iterable<T>.toQueryColumns(): Array<out QueryColumn> =
    map { it.column }.toTypedArray()

// order
fun <T> KProperty<T?>.toOrd(order: Order = Order.ASC): QueryOrderBy = column.toOrd(order)

// comparable
infix fun <T> KProperty<T?>.eq(other: T): QueryCondition = column.eq(other)

@Deprecated("使用 eq 和 null 进行比较可能是个错误。", ReplaceWith("this.isNull"))
infix fun <T> KProperty<T?>.eq(other: Nothing?): QueryCondition = column.eq(other)

infix fun <T> KProperty<T?>.eq(other: QueryColumn): QueryCondition = column.eq(other)

infix fun <T> KProperty<T?>.eq(other: KProperty<T?>): QueryCondition = column.eq(other.column)

infix fun <T> KProperty<T?>.ne(other: T): QueryCondition = column.ne(other)

@Deprecated("使用 ne 和 null 进行比较可能是个错误。", ReplaceWith("this.isNotNull"))
infix fun <T> KProperty<T?>.ne(other: Nothing?): QueryCondition = column.ne(other)

infix fun <T> KProperty<T?>.ne(other: QueryColumn): QueryCondition = column.ne(other)

infix fun <T> KProperty<T?>.ne(other: KProperty<T?>): QueryCondition = column.ne(other.column)

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

// between
infix fun <T : Comparable<T>> KProperty<T?>.between(other: ClosedRange<T>): QueryCondition =
    column.between(other)

infix fun <T : Comparable<T>> KProperty<T?>.between(other: Pair<T, T>): QueryCondition =
    column.between(other)

infix fun <T : Comparable<T>> KProperty<T?>.notBetween(other: ClosedRange<T>): QueryCondition =
    column.notBetween(other)

infix fun <T : Comparable<T>> KProperty<T?>.notBetween(other: Pair<T, T>): QueryCondition =
    column.notBetween(other)

// like
infix fun KProperty<String?>.like(other: Any): QueryCondition = column.like(other)

infix fun KProperty<String?>.likeRaw(other: Any): QueryCondition = column.likeRaw(other)

infix fun KProperty<String?>.likeLeft(other: Any): QueryCondition = column.likeLeft(other)

infix fun KProperty<String?>.likeRight(other: Any): QueryCondition = column.likeRight(other)

infix fun KProperty<String?>.contains(other: Any): QueryCondition = column.like(other)

infix fun KProperty<String?>.startsWith(other: Any): QueryCondition = column.likeLeft(other)

infix fun KProperty<String?>.endsWith(other: Any): QueryCondition = column.likeRight(other)

infix fun KProperty<String?>.notLike(other: Any): QueryCondition = column.notLike(other)

infix fun KProperty<String?>.notLikeRaw(other: Any): QueryCondition = column.notLikeRaw(other)

infix fun KProperty<String?>.notLikeLeft(other: Any): QueryCondition = column.notLikeLeft(other)

infix fun KProperty<String?>.notLikeRight(other: Any): QueryCondition = column.notLikeRight(other)

// in

/**
 * @since 1.1.0
 */
infix fun <T : Comparable<T>> KProperty<T?>.notIn(value: Collection<Any?>): QueryCondition = column.notIn(value)

/**
 * @since 1.1.0
 */
infix fun <T : Comparable<T>> KProperty<T?>.notIn(values: Array<Any?>): QueryCondition = column.notIn(values)

/**
 * @since 1.1.0
 */
fun <T : Comparable<T>> KProperty<T?>.notIn(vararg values:Any): QueryCondition = column.notIn(values)

/**
 * @since 1.1.0
 */
inline fun <T : Comparable<T>> KProperty<T?>.notIn(scope: QueryScope.()->Unit): QueryCondition = column.notIn(queryScope(init = scope))

infix fun <T : Comparable<T>> KProperty<T?>.`in`(queryWrapper: QueryWrapper): QueryCondition = column.`in`(queryWrapper)

infix fun <T : Comparable<T>> KProperty<T?>.`in`(values: ClosedRange<T>): QueryCondition = this inRange values

infix fun <T : Comparable<T>> KProperty<T?>.`in`(values: Collection<T>): QueryCondition = this inList values

fun <T : Comparable<T>> KProperty<T?>.`in`(vararg values: T): QueryCondition = this inArray values

/**
 * @since 1.1.0
 */
inline fun <T> KProperty<T?>.`in`(scope: QueryScope.()->Unit): QueryCondition = column.`in`(queryScope(init = scope))

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

/**
 * @since 1.1.0
 */
infix fun <T : Comparable<T>> KProperty<T?>.inList(other: Collection<T>): QueryCondition = column.inList(other)

/**
 * @since 1.1.0
 */
infix fun <T : Comparable<T>> KProperty<T?>.inArray(other: Array<out T>): QueryCondition  = column.inArray(other)

/**
 * @since 1.1.0
 */
infix fun <T : Comparable<T>> KProperty<T?>.inRange(other: ClosedRange<out T>): QueryCondition  = column.inRange(other)

// as
infix fun <T> KProperty<T?>.alias(other: String): QueryColumn = column.`as`(other)

infix fun <T> KProperty<T?>.`as`(other: String): QueryColumn = column.`as`(other)

// operator
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

// is
val KProperty<*>.isNull: QueryCondition get() = column.isNull

val KProperty<*>.isNotNull: QueryCondition get() = column.isNotNull

