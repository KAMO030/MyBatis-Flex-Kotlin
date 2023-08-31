package com.mybatisflex.kotlin.vec

import com.mybatisflex.core.query.CPI
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.core.row.Row
import com.mybatisflex.core.table.TableDefs
import com.mybatisflex.core.util.MapperUtil
import com.mybatisflex.kotlin.vec.JoinTypes.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalDistinct::class)
fun QueryData.wrap(): QueryWrapper = (if (distinct) DistinctQueryWrapper() else QueryWrapper()).apply {
    select(*columns.toTypedArray())
    from(table)
    if (tableAlias != table.tableName) `as`(tableAlias)
    condition?.let {
        where(it)
    }
    /*    join?.let { (join, condition, table, alias) ->
        when (join) {
            CROSS -> crossJoin<QueryWrapper>(table)
            INNER -> innerJoin(table)
            FULL -> fullJoin(table)
            LEFT -> leftJoin(table)
            RIGHT -> rightJoin(table)
        }.`as`(alias).on(condition)
    }*/
    groupBy(*groupBy.toTypedArray())
    having?.let {
        having(it)
    }
    if (orderBy.isNotEmpty()) orderBy(*orderBy.toTypedArray())
    if (rows > 0) {
        limit(offset, rows)
    }
}


@ExperimentalConvert
inline fun <reified T : Any> QueryWrapper.toQueryData() = QueryData(
    columns = CPI.getSelectColumns(this),
    table = TableDefs.getTableDef(T::class.java, CPI.getQueryTables(this).first().nameWithSchema),
    condition = CPI.getWhereQueryCondition(this),
    groupBy = CPI.getGroupByColumns(this),
    having = CPI.getHavingQueryCondition(this),
    distinct = MapperUtil.hasDistinct(CPI.getSelectColumns(this)),
    orderBy = CPI.getOrderBys(this),
    offset = CPI.getLimitOffset(this),
    rows = CPI.getLimitRows(this),
)


fun main() {
    println(JoinTypes.valueOf("LEFT"))
}

inline fun <reified E : Any> vecOf(tableAlias: String? = null) = QueryVector<E>(tableAlias)

inline fun <reified T : Any> isRow(): Boolean = Row::class.java.isAssignableFrom(T::class.java)

@OptIn(ExperimentalContracts::class)
fun isRow(entity: Any?): Boolean {
    contract {
        returns(true) implies (entity is Row)
    }
    return entity is Row
}