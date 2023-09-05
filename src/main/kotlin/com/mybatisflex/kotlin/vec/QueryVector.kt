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

import com.mybatisflex.core.BaseMapper
import com.mybatisflex.core.mybatis.Mappers
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.core.table.TableDef
import com.mybatisflex.core.table.TableDefs
import com.mybatisflex.core.table.TableInfo
import com.mybatisflex.core.table.TableInfoFactory
import com.mybatisflex.kotlin.extensions.db.tableInfo
import com.mybatisflex.kotlin.extensions.vec.wrap

open class QueryVector<E>(
    open val entityClass: Class<E>,
    open val data: QueryData,
    open val entityInstance: E? = null
) {
    companion object {
        inline operator fun <reified E : Any> invoke(tableAlias: String? = null): QueryVector<E> {
            val clazz = E::class.java
            val tableInfo = E::class.tableInfo
            return QueryVector(clazz, QueryData(tableInfo = tableInfo, tableAlias = tableAlias ?: tableInfo.tableName))
        }
    }

    val entity: E get() = entityInstance ?: entityClass.getDeclaredConstructor().newInstance()

    val wrapper: QueryWrapper get() = data.wrap()

    val sql: String get() = wrapper.toSQL()

    val tableDef: TableDef
        get() = TableDefs.getTableDef(entityClass, tableInfo.tableNameWithSchema)
            ?: throw NoSuchElementException("The TableDef corresponding to class $entityClass could not be found")


    val tableInfo: TableInfo
        get() = TableInfoFactory.ofEntityClass(entityClass)
            ?: throw NoSuchElementException("The TableInfo corresponding to class $entityClass could not be found")

    val size: Long get() = mapper.selectCountByQuery(wrapper)

    val mapper: BaseMapper<E> get() = Mappers.ofEntityClass(entityClass)

    fun copy(
        data: QueryData = this.data,
        entityClass: Class<E> = this.entityClass
    ) = QueryVector(entityClass, data)
}