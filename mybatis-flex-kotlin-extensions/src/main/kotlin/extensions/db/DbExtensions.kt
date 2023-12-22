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
package com.mybatisflex.kotlin.extensions.db

import com.mybatisflex.core.BaseMapper
import com.mybatisflex.core.exception.MybatisFlexException
import com.mybatisflex.core.mybatis.Mappers
import com.mybatisflex.core.paginate.Page
import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryCondition
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.core.row.Db
import com.mybatisflex.core.row.Db.selectListByQuery
import com.mybatisflex.core.row.Db.selectOneByQuery
import com.mybatisflex.core.row.Row
import com.mybatisflex.core.table.TableInfo
import com.mybatisflex.core.table.TableInfoFactory
import com.mybatisflex.kotlin.extensions.kproperty.defaultColumns
import com.mybatisflex.kotlin.extensions.kproperty.toQueryColumns
import com.mybatisflex.kotlin.extensions.model.toEntities
import com.mybatisflex.kotlin.scope.QueryScope
import com.mybatisflex.kotlin.scope.queryScope
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubclassOf


/*
 * 数据库简单操作扩展
 * @author 卡莫sama
 */

/**
 * 把泛型类型当作mapper类型拿到mapper实例
 * @author 卡莫sama
 */
inline fun <reified M> mapper(): M = Mappers.ofMapperClass(M::class.java)

/**
 * 把泛型类型当作实体类型拿到mapper实例
 * @author 卡莫sama
 */
val <E : Any> KClass<E>.baseMapper: BaseMapper<E>
    get() = Mappers.ofEntityClass(java)

val <E : Any> KClass<E>.tableInfo: TableInfo
    get() = tableInfoOrNull ?: error(
        "The class TableInfo cannot be found through $this" +
                " because the entity class corresponding to the generic used by this interface to inherit from BaseMapper cannot be found."
    )


val <E : Any> KClass<E>.tableInfoOrNull: TableInfo?
    get() = if (isSubclassOf(BaseMapper::class)) {
        TableInfoFactory.ofMapperClass(java)
    } else {
        TableInfoFactory.ofEntityClass(java)
    }

//    query-----------
inline fun <reified T : Any> queryOne(
    vararg columns: QueryColumn,
    init: QueryScope.() -> Unit
): T? =
    try {
        T::class.baseMapper.selectOneByQuery(queryScope(columns = columns, init = init))
    } catch (e: MybatisFlexException) {
        T::class.tableInfo.run {
            queryRow(schema = schema, tableName = tableName, columns = columns, init = {
                init()
                // 如果未调用select方法，则默认查询所有列
                if (this.hasSelect().not()) select(*T::class.defaultColumns)
            })?.toEntity(T::class.java)
        }
    }

inline fun queryRow(
    vararg columns: QueryColumn,
    schema: String? = null,
    tableName: String? = null,
    init: QueryScope.() -> Unit
): Row? =
    selectOneByQuery(
        schema,
        tableName,
        queryScope(columns = columns, init = init)
    )

inline fun <reified T : Any> query(
    init: QueryScope.() -> Unit
): List<T> =
    try {
        T::class.baseMapper.selectListByQuery(queryScope(init = init))
    } catch (e: MybatisFlexException) {
        T::class.tableInfo.run {
            queryRows(schema = schema, tableName = tableName, init = {
                init()
                // 如果未调用select方法，则默认查询所有列
                if (this.hasSelect().not()) select(*T::class.defaultColumns)
            }).toEntities()
        }
    }


inline fun queryRows(
    schema: String? = null,
    tableName: String? = null,
    init: QueryScope.() -> Unit
): List<Row> = selectListByQuery(
    schema, tableName, queryScope(init = init)
)

//    filter-----------
inline fun <reified E : Any> filter(
    vararg columns: QueryColumn,
    queryCondition: QueryCondition = QueryCondition.createEmpty()
): List<E> =
    try {
        E::class.baseMapper.selectListByCondition(queryCondition)
    } catch (e: MybatisFlexException) {
        E::class.tableInfo.run {
            selectListByQuery(
                schema,
                tableName,
                QueryWrapper().select(*columns).where(queryCondition)
            ).toEntities()
        }
    }


inline fun <reified E : Any> filterColumn(
    vararg columns: QueryColumn = E::class.tableInfo.defaultQueryColumn.toTypedArray(),
    init: () -> QueryCondition
): List<E> =
    filter(
        columns = columns,
        queryCondition = init()
    )

inline fun <reified E : Any> filter(
    columns: Array<out QueryColumn> = E::class.defaultColumns,
    init: () -> QueryCondition
): List<E> =
    filterColumn(
        columns = columns,
        init = init
    )

inline fun <reified E : Any> filter(
    vararg columns: KProperty<*>,
    init: () -> QueryCondition
): List<E> =
    filter(
        columns = columns.toQueryColumns(),
        init = init
    )

//    all----------
inline fun <reified E : Any> all(): List<E> = filter<E>(E::class.defaultColumns, QueryCondition::createEmpty)

inline fun <reified E : Any> paginate(
    page: Page<E>,
    init: QueryScope.() -> Unit
): Page<E> {
    return try {
        E::class.baseMapper.paginate(page, queryScope(init = init))
    } catch (e: MybatisFlexException) {
        E::class.tableInfo.run {
            queryPage(schema, tableName, Page(page.pageNumber, page.pageSize), init = {
                init()
                if (this.hasSelect().not()) select(*E::class.defaultColumns)
            }).let {
                Page(it.records.toEntities(), it.pageNumber, it.pageSize, it.totalRow)
            }
        }
    }
}

inline fun queryPage(
    schema: String? = null,
    tableName: String? = null,
    page: Page<Row>? = null,
    init: QueryScope.() -> Unit
): Page<Row> = Db.paginate(schema, tableName, page, queryScope(init = init))

