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
package com.mybatisflex.kotlin.extensions.db

import com.mybatisflex.core.BaseMapper
import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryTable
import com.mybatisflex.core.table.TableInfo
import com.mybatisflex.core.table.TableInfoFactory
import com.mybatisflex.kotlin.extensions.kproperty.column
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubclassOf

val <E : Any> KClass<E>.tableInfo: TableInfo
    get() = requireNotNull(tableInfoOrNull) {
        "The class TableInfo cannot be found through $this" +
                " because the entity class corresponding to the generic used by this interface to inherit from BaseMapper cannot be found."
    }

val <E : Any> KClass<E>.queryTable: QueryTable
    get() = tableInfo.queryTable

/**
 * @since 1.1.0
 */
val TableInfo.queryTable: QueryTable
    get() = QueryTable(schema, tableName)


val <E : Any> KClass<E>.tableInfoOrNull: TableInfo?
    get() = if (isSubclassOf(BaseMapper::class)) {
        TableInfoFactory.ofMapperClass(java)
    } else {
        TableInfoFactory.ofEntityClass(java)
    }

/**
 * 通过QueryTable对象获得一个QueryColumn对象
 * @param columnName 列名
 * @since 1.1.0
 */
operator fun QueryTable.get(columnName: String): QueryColumn = QueryColumn(this,columnName)

/**
 * 通过QueryTable对象获得一个QueryColumn对象
 * @param property 属性对象
 * @since 1.1.0
 */
operator fun QueryTable.get(property: KProperty<*>): QueryColumn = QueryColumn(this,property.column.name)

/**
 * 给QueryTable对象添加别名
 * @param alias 别名
 * @since 1.1.0
 */
infix fun QueryTable.`as`(alias: String): QueryTable = `as`(alias)