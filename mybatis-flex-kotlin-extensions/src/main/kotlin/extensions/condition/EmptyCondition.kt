package com.mybatisflex.kotlin.extensions.condition

import com.mybatisflex.core.constant.SqlConnector
import com.mybatisflex.core.dialect.DialectFactory
import com.mybatisflex.core.dialect.IDialect
import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryCondition
import com.mybatisflex.core.query.QueryTable
import com.mybatisflex.core.query.RawQueryCondition
import com.mybatisflex.kotlin.extensions.condition.annotation.ExperimentalEmptyCondition
import java.util.function.BooleanSupplier

@ExperimentalEmptyCondition
//@Suppress("unused")
object EmptyCondition : QueryCondition() {
    @get:JvmName("value")
    val value: Nothing?
        get() = null

    @get:JvmName("column")
    val column: Nothing?
        get() = null

    @get:JvmName("logic")
    val logic: Nothing?
        get() = null

    @get:JvmName("nextEffectiveCondition")
    val nextEffectiveCondition: QueryCondition?
        get() = super.getNextEffectiveCondition()

    @get:JvmName("prevEffectiveCondition")
    val prevEffectiveCondition: QueryCondition?
        get() = super.getPrevEffectiveCondition()

    override fun toString(): String {
        return "com.mybatisflex.kotlin.extensions.condition.EmptyCondition"
    }

    @Deprecated(
        message = "Attempting to clone a singleton is illegal.",
        replaceWith = ReplaceWith("this"),
        level = DeprecationLevel.ERROR,
    )
    override fun clone(): EmptyCondition {
        return this
    }

    override fun notEmpty(): Boolean {
        return false
    }

    @Deprecated("The property of EmptyCondition is immutable.", level = DeprecationLevel.HIDDEN)
    override fun setEmpty(empty: Boolean): EmptyCondition {
        return this
    }

    @Deprecated("use property, instead of getter.", replaceWith = ReplaceWith("EmptyCondition.column"))
    override fun getColumn(): Nothing? {
        return null
    }

    @Deprecated("The property of EmptyCondition is immutable.", level = DeprecationLevel.HIDDEN)
    override fun setColumn(column: QueryColumn) {
        // do nothing here
    }

    @Deprecated("use property, instead of getter.", replaceWith = ReplaceWith("EmptyCondition.value"))
    override fun getValue(): Nothing? {
        return null
    }

    @Deprecated("The property of EmptyCondition is immutable.", level = DeprecationLevel.HIDDEN)
    override fun setValue(value: Any?) {
        // do nothing here
    }

    @Deprecated("use property, instead of getter.", replaceWith = ReplaceWith("EmptyCondition.logic"))
    override fun getLogic(): Nothing? {
        return null
    }

    @Deprecated("The property of EmptyCondition is immutable.", level = DeprecationLevel.HIDDEN)
    override fun setLogic(logic: String?) {
        // do nothing here
    }

    @Deprecated("The property of EmptyCondition is immutable.", level = DeprecationLevel.HIDDEN)
    override fun `when`(effective: Boolean): EmptyCondition {
        return this
    }

    @Deprecated("The property of EmptyCondition is immutable.", level = DeprecationLevel.HIDDEN)
    override fun `when`(fn: BooleanSupplier): EmptyCondition {
        return this
    }

    override fun checkEffective(): Boolean {
        return false
    }

    override infix fun and(sql: String): QueryCondition {
        return RawQueryCondition(sql)
    }

    override fun and(sql: String, vararg params: Any?): QueryCondition {
        return RawQueryCondition(sql, *params)
    }

    override infix fun and(nextCondition: QueryCondition): QueryCondition {
        return prev?.and(nextCondition) ?: nextCondition
    }

    override infix fun or(sql: String): QueryCondition {
        return RawQueryCondition(sql)
    }

    override fun or(sql: String, vararg params: Any?): QueryCondition {
        return RawQueryCondition(sql, *params)
    }

    override infix fun or(nextCondition: QueryCondition): QueryCondition {
        return prev?.or(nextCondition) ?: nextCondition
    }

    override fun connect(nextCondition: QueryCondition, connector: SqlConnector) {
        if (nextCondition is EmptyCondition) {
            return
        }
        super.connect(nextCondition, connector)
    }

    override fun toSql(queryTables: List<QueryTable>, dialect: IDialect): String {
        return super.toSql(queryTables, dialect)
    }

    fun toSql(): String = toSql(emptyList(), DialectFactory.getDialect())

    @Deprecated(
        "use property, instead of getter.",
        replaceWith = ReplaceWith("EmptyCondition.prevEffectiveCondition"),
        level = DeprecationLevel.HIDDEN
    )
    override fun getPrevEffectiveCondition(): QueryCondition? {
        return super.getPrevEffectiveCondition()
    }

    @Deprecated(
        "use property, instead of getter.",
        replaceWith = ReplaceWith("EmptyCondition.nextEffectiveCondition"),
        level = DeprecationLevel.HIDDEN
    )
    override fun getNextEffectiveCondition(): QueryCondition? {
        return super.getNextEffectiveCondition()
    }

    override fun appendQuestionMark(sqlBuilder: StringBuilder) {
        super.appendQuestionMark(sqlBuilder)
    }
}