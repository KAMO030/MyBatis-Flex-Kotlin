package com.mybatisflex.kotlin.flexStream

import com.mybatisflex.core.constant.SqlConsts
import com.mybatisflex.core.query.OperatorQueryCondition
import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryCondition
import com.mybatisflex.core.query.QueryOrderBy
import com.mybatisflex.core.table.TableDefs
import com.mybatisflex.core.table.TableInfoFactory
import java.lang.reflect.Field
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
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

fun QueryColumn.toOrd(order: Order): QueryOrderBy = when (order) {
    Order.ASC -> asc()
    Order.DESC -> desc()
}

infix fun <T : Comparable<T>> KProperty<T?>.eq(other: T): QueryCondition {
    val queryColumn = toQueryColumn()
    return queryColumn.eq(other)
}

infix fun <T : Comparable<T>> KProperty<T?>.gt(other: T): QueryCondition {
    val queryColumn = toQueryColumn()
    return queryColumn.gt(other)
}

infix fun <T : Comparable<T>> KProperty<T?>.ge(other: T): QueryCondition {
    val queryColumn = toQueryColumn()
    return queryColumn.ge(other)
}

infix fun <T : Comparable<T>> KProperty<T?>.lt(other: T): QueryCondition {
    val queryColumn = toQueryColumn()
    return queryColumn.lt(other)
}

infix fun <T : Comparable<T>> KProperty<T?>.le(other: T): QueryCondition {
    val queryColumn = toQueryColumn()
    return queryColumn.le(other)
}

infix fun <T : Comparable<T>> KProperty<T?>.between(other: ClosedRange<T>): QueryCondition {
    val queryColumn = toQueryColumn()
    return queryColumn.between(other.start, other.endInclusive)
}

infix fun <T : Comparable<T>> KProperty<T?>.notBetween(other: ClosedRange<T>): QueryCondition {
    val queryColumn = toQueryColumn()
    return queryColumn.notBetween(other.start, other.endInclusive)
}

infix fun KProperty<String?>.like(other: String): QueryCondition {
    val queryColumn = toQueryColumn()
    return queryColumn.likeRaw(other)
}

infix fun KProperty<String?>.startsWith(other: String): QueryCondition {
    val queryColumn = toQueryColumn()
    return queryColumn.likeLeft(other)
}

infix fun KProperty<String?>.endsWith(other: String): QueryCondition {
    val queryColumn = toQueryColumn()
    return queryColumn.likeRight(other)
}

infix fun KProperty<String?>.contains(other: String): QueryCondition {
    val queryColumn = toQueryColumn()
    return queryColumn.like(other)
}

infix fun KProperty<String?>.notLike(other: String): QueryCondition {
    val queryColumn = toQueryColumn()
    return QueryCondition.create(queryColumn, SqlConsts.NOT_LIKE, other)
}

infix fun <T : Comparable<T>> KProperty<T?>.inList(other: Collection<T>): QueryCondition {
    val queryColumn = toQueryColumn()
    require(other.isNotEmpty()) {
        "The collection must not be empty."
    }
    return if (other.size == 1) queryColumn.eq(other.first()) else queryColumn.`in`(other)
}

infix fun <T : Comparable<T>> KProperty0<T?>.inArray(other: Array<out T>): QueryCondition {
    val queryColumn = toQueryColumn()
    require(other.isNotEmpty()) {
        "The array must not be empty."
    }
    return if (other.size == 1) queryColumn.eq(other[0]) else queryColumn.`in`(other)
}

infix fun <T> KProperty0<T?>.alias(other: String): QueryColumn = toQueryColumn().`as`(other)

operator fun <T: Number> KProperty<T?>.plus(other: QueryColumn): QueryColumn = toQueryColumn() + other

operator fun <T: Number> KProperty<T?>.plus(other: KProperty<T?>): QueryColumn = toQueryColumn() + other.toQueryColumn()

operator fun <T: Number> KProperty<T?>.plus(other: T): QueryColumn = toQueryColumn() + other

operator fun <T: Number> KProperty<T?>.minus(other: QueryColumn): QueryColumn = toQueryColumn() - other

operator fun <T: Number> KProperty<T?>.minus(other: KProperty<T?>): QueryColumn = toQueryColumn() - other.toQueryColumn()

operator fun <T: Number> KProperty<T?>.minus(other: T): QueryColumn = toQueryColumn() - other

operator fun <T: Number> KProperty<T?>.times(other: QueryColumn): QueryColumn = toQueryColumn() * other

operator fun <T: Number> KProperty<T?>.times(other: KProperty<T?>): QueryColumn = toQueryColumn() * other.toQueryColumn()

operator fun <T: Number> KProperty<T?>.times(other: T): QueryColumn = toQueryColumn() * other

operator fun <T: Number> KProperty<T?>.div(other: QueryColumn): QueryColumn = toQueryColumn() / other

operator fun <T: Number> KProperty<T?>.div(other: KProperty<T?>): QueryColumn = toQueryColumn() / other.toQueryColumn()

operator fun <T: Number> KProperty<T?>.div(other: T): QueryColumn = toQueryColumn() / other

fun <T> KProperty<T?>.isNull(): QueryCondition = toQueryColumn().isNull

fun <T> KProperty<T?>.isNotNull(): QueryCondition = toQueryColumn().isNotNull

operator fun QueryColumn.plus(other: QueryColumn): QueryColumn = add(other)

operator fun QueryColumn.plus(other: Number): QueryColumn = add(other)

operator fun QueryColumn.plus(other: KProperty<Number>): QueryColumn = add(other.toQueryColumn())

operator fun QueryColumn.minus(other: QueryColumn): QueryColumn = subtract(other)

operator fun QueryColumn.minus(other: Number): QueryColumn = subtract(other)

operator fun QueryColumn.minus(other: KProperty<Number>): QueryColumn = subtract(other.toQueryColumn())

operator fun QueryColumn.times(other: QueryColumn): QueryColumn = multiply(other)

operator fun QueryColumn.times(other: Number): QueryColumn = multiply(other)

operator fun QueryColumn.times(other: KProperty<Number>): QueryColumn = multiply(other.toQueryColumn())

operator fun QueryColumn.div(other: QueryColumn): QueryColumn = divide(other)

operator fun QueryColumn.div(other: Number): QueryColumn = divide(other)

operator fun QueryColumn.div(other: KProperty<Number>): QueryColumn = divide(other.toQueryColumn())

operator fun QueryCondition.not(): QueryCondition =
    if (this is OperatorQueryCondition) {  // 如果是OperatorQueryCondition，则需要判断是否已经反转
        val field = OperatorQueryCondition::class.java.getDeclaredField("operator")
        field.isAccessible = true
        val operator = field[this] as? String?
        if (operator !== null && "NOT" in operator.uppercase()) childCondition
        else OperatorQueryCondition("NOT ", this)
    } else OperatorQueryCondition("NOT ", this)
