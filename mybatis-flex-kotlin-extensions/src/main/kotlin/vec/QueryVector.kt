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
import com.mybatisflex.core.exception.MybatisFlexException
import com.mybatisflex.core.mybatis.Mappers
import com.mybatisflex.core.query.QueryTable
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.kotlin.extensions.db.tableInfo
import com.mybatisflex.kotlin.extensions.vec.wrap
import java.lang.reflect.Constructor

/**
 * 查询向量，一套使用函数式语言进行操作数据库的 api。
 * @param entityClass 实体类的 [Class] 。
 * @param data 查询数据，其存储了包装成 QueryWrapper 的关键信息。
 * @param entity 实体类对象实例。
 * @author CloudPlayer
 */
class QueryVector<E>(
    private val entityClass: Class<E>,
    val data: QueryData,
    val entity: E
) {
    companion object {
        /**
         * 此 invoke 方法用于替代辅助构造器的工作。
         * @param E 实体类的类型。不能为 null。实体类必须拥有一个无参构造器，否则初始化失败。
         * @param tableAlias 表别名，默认为 null，没有别名。
         * @author CloudPlayer
         */
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

    /**
     * 从查询向量中获取 [QueryWrapper] 实例对象。
     * @return [QueryWrapper] 实例对象。
     */
    val wrapper: QueryWrapper get() = data.wrap()

    /**
     * 获取查询向量将要生成的 SQL 语句。
     * @return 查询向量将要生成的 SQL 语句。
     */
    val sql: String get() = wrapper.toSQL()

    /**
     * 获取 [QueryTable] 实例对象。其中包装了表名，表别名，表架构信息。
     * @return [QueryTable] 实例对象。
     */
    val queryTable: QueryTable get() = data.table

    /**
     * 返回查询数据量。调用此属性会立即执行一次查询。
     * @return 查询数据量。
     */
    val size: Long get() = mapper.selectCountByQuery(wrapper)

    /**
     * 获取实体类的 [BaseMapper] 对象。
     * @return [BaseMapper] 实例对象。
     * @throws [MybatisFlexException] 如果实体类没有定义 Mapper 接口并继承 [BaseMapper] ，则抛出此异常。
     */
    val mapper: BaseMapper<E> get() = Mappers.ofEntityClass(entityClass)

    fun copy(
        data: QueryData = this.data,
        entityClass: Class<E> = this.entityClass,
        instance: E = this.entity
    ) = QueryVector(entityClass, data, instance)
}