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

import com.mybatisflex.core.query.QueryTable
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.kotlin.extensions.db.tableInfo
import com.mybatisflex.kotlin.extensions.wrapper.self
import com.mybatisflex.kotlin.vec.DistinctQueryWrapper
import com.mybatisflex.kotlin.vec.QueryData
import com.mybatisflex.kotlin.vec.QueryVector
import com.mybatisflex.kotlin.vec.annotation.ExperimentalConvert
import com.mybatisflex.kotlin.vec.annotation.ExperimentalDistinct

@OptIn(ExperimentalDistinct::class)
fun QueryData.wrap(): QueryWrapper = (if (distinct) DistinctQueryWrapper() else QueryWrapper()).apply {
    self.selectColumns = columns
    self.queryTables = mutableListOf(table)
    if (tableAlias != table.name) `as`(tableAlias)
    condition?.let {
        self.where = it
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
    self.groupBy = groupBy
    having?.let {
        having(it)
    }
    if (orderBy.isNotEmpty()) self.orderBys = orderBy
    if (rows > 0) {
        limit(offset, rows)
    }
}

@ExperimentalConvert
inline fun <reified T : Any> QueryWrapper.toQueryData() = QueryData(
    columns = self.selectColumns,
    table = T::class.tableInfo.run { QueryTable(schema, tableName) },
    condition = self.where,
    groupBy = self.groupBy,
    having = self.having,
    distinct = self.isDistinct,
    orderBy = self.orderBys,
    offset = self.offset,
    rows = self.rows,
)

inline fun <reified E : Any> vecOf(tableAlias: String? = null) = QueryVector<E>(tableAlias)
