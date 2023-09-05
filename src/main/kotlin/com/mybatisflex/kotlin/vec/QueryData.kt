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
package com.mybatisflex.kotlin.vec

import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryCondition
import com.mybatisflex.core.query.QueryOrderBy
import com.mybatisflex.core.table.TableDef

/**
 * 查询时的对应数据。
 * @param columns 要查询的列，相当于select中的列。
 * @param table 从哪张表查询，相当于from。
 * @param condition 查询的条件，相当于where中的条件。
 * @param groupBy 相当于group by中的列。
 * @param having 相当于having中的条件。
 * @param distinct 是否去重。false表示不去重，true表示去重。
 * @param offset 分页中的行偏移量。
 * @param rows 分页中的行数。
 * @param tableAlias 表别名。
 */
data class QueryData(
    val columns: List<QueryColumn> = emptyList(),
    val table: TableDef,
    val condition: QueryCondition? = null,
    val groupBy: List<QueryColumn> = emptyList(),
    val having: QueryCondition? = null,
    val distinct: Boolean = false,
    val orderBy: List<QueryOrderBy> = emptyList(),
    val offset: Long = 0L,
    val rows: Long = -1L,
    val tableAlias: String = table.tableName
)
