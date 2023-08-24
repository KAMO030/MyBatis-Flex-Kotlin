package com.mybatisflex.kotlin.flexStream

import com.mybatisflex.annotation.Table
import com.mybatisflex.core.query.OperatorQueryCondition
import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryCondition
import com.mybatisflex.core.table.TableDefs
import java.lang.reflect.Field
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.javaField

fun <T> KProperty<T?>.toQueryColumn(): QueryColumn = requireNotNull(javaField) {
    "Cannot convert to ${Field::class.java.canonicalName} because the property has no backing field."
}.toQueryColumn()

fun Field.toQueryColumn(): QueryColumn {
    val from = declaringClass
    val table = from.getAnnotation(Table::class.java)
        ?: throw IllegalArgumentException("Declared classes must be annotated by ${Table::class.java}")
    val scheme = table.schema
    val tableName = table.value
    return TableDefs.getQueryColumn(from, "$scheme${if (scheme.isNotBlank()) "." else ""}$tableName", name)
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

infix fun <T : Comparable<T>> KProperty<T?>.between(other: ClosedRange<out T>): QueryCondition {
    val queryColumn = toQueryColumn()
    return queryColumn.between(other.start, other.endInclusive)
}

infix fun <T : Comparable<T>> KProperty<T?>.notBetween(other: ClosedRange<out T>): QueryCondition {
    val queryColumn = toQueryColumn()
    return queryColumn.notBetween(other.start, other.endInclusive)
}

infix fun KProperty<String?>.like(other: String): QueryCondition {
    val queryColumn = toQueryColumn()
    return queryColumn.like(other)
}

infix fun KProperty<String?>.notLike(other: String): QueryCondition {
    val queryColumn = toQueryColumn()
    return queryColumn.notLike(other)
}

infix fun <T : Comparable<T>> KProperty<T?>.inList(other: Collection<T>): QueryCondition {
    val queryColumn = toQueryColumn()
    return if (other.size == 1) queryColumn.eq(other.first()) else queryColumn.`in`(other)
}

infix fun <T : Comparable<T>> KProperty0<T?>.inArray(other: Array<out T>): QueryCondition {
    val queryColumn = toQueryColumn()
    return if (other.size == 1) queryColumn.eq(other[0]) else queryColumn.`in`(other)
}

operator fun QueryColumn.plus(other: QueryColumn): QueryColumn = add(other)

operator fun QueryColumn.plus(other: Number): QueryColumn = add(other)

operator fun QueryColumn.minus(other: QueryColumn): QueryColumn = subtract(other)

operator fun QueryColumn.minus(other: Number): QueryColumn = subtract(other)

operator fun QueryColumn.times(other: QueryColumn): QueryColumn = multiply(other)

operator fun QueryColumn.times(other: Number): QueryColumn = multiply(other)

operator fun QueryColumn.div(other: QueryColumn): QueryColumn = subtract(other)

operator fun QueryColumn.div(other: Number): QueryColumn = subtract(other)

operator fun QueryCondition.not(): QueryCondition =
    if (this is OperatorQueryCondition) {  // 如果是OperatorQueryCondition，则需要判断是否已经反转
        val field = javaClass.getDeclaredField("operator")
        field.isAccessible = true
        val operator = field[this] as? String?
        if (operator !== null && "NOT" in operator.uppercase()) childCondition
        else OperatorQueryCondition("NOT ", this)
    } else OperatorQueryCondition("NOT ", this)
