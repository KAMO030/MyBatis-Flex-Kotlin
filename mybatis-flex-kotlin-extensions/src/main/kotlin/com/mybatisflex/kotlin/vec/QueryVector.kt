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
import com.mybatisflex.core.exception.FlexExceptions
import com.mybatisflex.core.mybatis.Mappers
import com.mybatisflex.core.query.QueryTable
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.kotlin.extensions.db.tableInfo
import com.mybatisflex.kotlin.extensions.vec.wrap
import java.lang.reflect.Constructor

/**
 * @author CloudPlayer
 */
class QueryVector<E>(
    private val entityClass: Class<E>,
    val data: QueryData,
    val entity: E
) {
    companion object {
        inline operator fun <reified E : Any> invoke(tableAlias: String? = null): QueryVector<E> {
            val clazz = E::class.java
            val tableInfo = E::class.tableInfo
            val instance: E = try {
                val constructor: Constructor<E> = E::class.java.getDeclaredConstructor()
                constructor.isAccessible = true
                constructor.newInstance()
            } catch (e: Throwable) {
                throw FlexExceptions.wrap(e)
            }
            return QueryVector(
                clazz,
                QueryData(
                    table = QueryTable(tableInfo.schema, tableInfo.tableName),
                    tableAlias = tableAlias ?: tableInfo.tableName
                ),
                instance
            )
        }
    }

    val wrapper: QueryWrapper get() = data.wrap()

    val sql: String get() = wrapper.toSQL()

    val queryTable: QueryTable get() = data.table

    val size: Long get() = mapper.selectCountByQuery(wrapper)

    val mapper: BaseMapper<E> get() = Mappers.ofEntityClass(entityClass)

    fun copy(
        data: QueryData = this.data,
        entityClass: Class<E> = this.entityClass,
        instance: E = this.entity
    ) = QueryVector(entityClass, data, instance)
}