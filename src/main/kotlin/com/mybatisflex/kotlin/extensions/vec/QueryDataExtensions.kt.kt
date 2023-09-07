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
package com.mybatisflex.kotlin.extensions.vec

import com.mybatisflex.core.query.CPI
import com.mybatisflex.core.query.QueryTable
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.core.row.Row
import com.mybatisflex.core.util.MapperUtil
import com.mybatisflex.kotlin.extensions.db.tableInfo
import com.mybatisflex.kotlin.vec.DistinctQueryWrapper
import com.mybatisflex.kotlin.vec.QueryData
import com.mybatisflex.kotlin.vec.QueryVector
import com.mybatisflex.kotlin.vec.annotation.ExperimentalConvert
import com.mybatisflex.kotlin.vec.annotation.ExperimentalDistinct
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalDistinct::class)
fun QueryData.wrap(): QueryWrapper = (if (distinct) DistinctQueryWrapper() else QueryWrapper()).apply {
    select(*columns.toTypedArray())
    from(this@wrap.table)
    if (tableAlias != table.name) `as`(tableAlias)
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
    table = T::class.tableInfo.run { QueryTable(schema, tableName) },
    condition = CPI.getWhereQueryCondition(this),
    groupBy = CPI.getGroupByColumns(this),
    having = CPI.getHavingQueryCondition(this),
    distinct = MapperUtil.hasDistinct(CPI.getSelectColumns(this)),
    orderBy = CPI.getOrderBys(this),
    offset = CPI.getLimitOffset(this),
    rows = CPI.getLimitRows(this),
)

inline fun <reified E : Any> vecOf(tableAlias: String? = null) = QueryVector<E>(tableAlias)

inline fun <reified T : Any> isRow(): Boolean = Row::class.java.isAssignableFrom(T::class.java)

@OptIn(ExperimentalContracts::class)
fun isRow(entity: Any?): Boolean {
    contract {
        returns(true) implies (entity is Row)
    }
    return entity is Row
}

