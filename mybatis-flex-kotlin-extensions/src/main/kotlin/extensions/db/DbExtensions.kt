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
import com.mybatisflex.core.exception.MybatisFlexException
import com.mybatisflex.core.mybatis.Mappers
import com.mybatisflex.core.paginate.Page
import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryCondition
import com.mybatisflex.core.query.QueryTable
import com.mybatisflex.core.row.Db
import com.mybatisflex.core.row.Db.selectListByQuery
import com.mybatisflex.core.row.Db.selectOneByQuery
import com.mybatisflex.core.row.Row
import com.mybatisflex.core.table.TableInfo
import com.mybatisflex.core.table.TableInfoFactory
import com.mybatisflex.kotlin.extensions.kproperty.allColumns
import com.mybatisflex.kotlin.extensions.kproperty.column
import com.mybatisflex.kotlin.extensions.kproperty.defaultColumns
import com.mybatisflex.kotlin.extensions.kproperty.toQueryColumns
import com.mybatisflex.kotlin.extensions.mapper.deleteByCondition
import com.mybatisflex.kotlin.extensions.model.toEntities
import com.mybatisflex.kotlin.extensions.model.toEntityPage
import com.mybatisflex.kotlin.extensions.model.toRow
import com.mybatisflex.kotlin.scope.QueryScope
import com.mybatisflex.kotlin.scope.UpdateScope
import com.mybatisflex.kotlin.scope.queryScope
import com.mybatisflex.kotlin.scope.updateScope
import java.io.Serializable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.isSubclassOf


/*
 * 数据库简单操作扩展
 * @author KAMOsama
 */

/**
 * 把泛型类型当作mapper类型拿到mapper实例
 * @author KAMOsama
 */
inline fun <reified M> mapper(): M = Mappers.ofMapperClass(M::class.java)

/**
 * 把泛型类型当作实体类型拿到mapper实例
 * @author KAMOsama
 */
val <E : Any> KClass<E>.baseMapper: BaseMapper<E>
    get() = Mappers.ofEntityClass(java)

val <E : Any> KClass<E>.baseMapperOrNull: BaseMapper<E>?
    get() = try {
        Mappers.ofEntityClass(java)
    } catch (_: Exception) {
        null
    }

val <E : Any> KClass<E>.tableInfo: TableInfo
    get() = requireNotNull(tableInfoOrNull) {
        "The class TableInfo cannot be found through $this" +
                " because the entity class corresponding to the generic used by this interface to inherit from BaseMapper cannot be found."
    }

val <E: Any> KClass<E>.queryTable: QueryTable
    get() {
        val info = tableInfo
        return QueryTable(info.schema, info.tableName)
    }

val <E : Any> KClass<E>.tableInfoOrNull: TableInfo?
    get() = if (isSubclassOf(BaseMapper::class)) {
        TableInfoFactory.ofMapperClass(java)
    } else {
        TableInfoFactory.ofEntityClass(java)
    }

//    query-----------
/**
 * 通过条件查询一条数据
 * @param columns 查询的列
 * @param init 查询作用域初始化函数
 */
inline fun <reified E : Any> queryOne(
    vararg columns: QueryColumn,
    init: QueryScope.() -> Unit
): E? = E::class.baseMapperOrNull?.let {
    val scope = QueryScope().apply(init)
    if (!scope.hasSelect() && columns.isNotEmpty()) scope.select(*columns)
    it.selectOneByQuery(scope)
} ?: E::class.tableInfo.let {
    queryRow(schema = it.schema, tableName = it.tableName, columns = columns) {
        init()
        // 如果未调用select方法，则默认查询所有列
        if (this.hasSelect().not()) select(E::class.allColumns)
    }?.toEntity(E::class.java)
}

/**
 * 通过条件查询多条数据
 * @param columns 查询的列
 * @param init 查询作用域初始化函数
 */
inline fun <reified E : Any> query(
    vararg columns: QueryColumn,
    init: QueryScope.() -> Unit
): List<E> = try {
    E::class.baseMapper.selectListByQuery(queryScope(columns = columns, init = init))
} catch (e: MybatisFlexException) {
    E::class.tableInfo.run {
        queryRows(schema = schema, tableName = tableName, columns = columns) {
            init()
            // 如果未调用select方法，则默认查询所有列
            if (this.hasSelect().not()) select(*E::class.defaultColumns)
        }.toEntities()
    }
}

/**
 * 通过条件查询一条数据
 * @param columns 查询的列
 * @param init 查询作用域初始化函数
 */
inline fun queryRow(
    schema: String? = null,
    tableName: String? = null,
    vararg columns: QueryColumn,
    init: QueryScope.() -> Unit
): Row? = selectOneByQuery(
    schema,
    tableName,
    queryScope(columns = columns, init = init)
)

/**
 * 通过条件查询多条数据
 * @param columns 查询的列
 * @param init 查询作用域初始化函数
 */
inline fun queryRows(
    schema: String? = null,
    tableName: String? = null,
    vararg columns: QueryColumn,
    init: QueryScope.() -> Unit
): List<Row> = selectListByQuery(
    schema, tableName, queryScope(columns = columns, init = init)
)

//    filter-----------
/**
 * 通过条件查询多条数据
 * @param columns 查询的列
 * @param queryCondition 查询条件
 */
inline fun <reified E : Any> filter(
    vararg columns: QueryColumn = emptyArray(),
    queryCondition: QueryCondition = QueryCondition.createEmpty()
): List<E> = query(columns = columns) { and(queryCondition) }

/**
 * 通过条件查询多条数据
 * @param columns 查询的列
 * @param condition 查询条件
 */
inline fun <reified E : Any> filterColumn(
    vararg columns: QueryColumn = E::class.defaultColumns,
    condition: () -> QueryCondition
): List<E> = filter(columns = columns, queryCondition = condition())

/**
 * 通过条件查询一条数据
 * @param columns 查询的列
 * @param condition 查询条件
 * @since 1.0.5
 */
inline fun <reified E : Any> filterOne(
    vararg columns: KProperty<*> = emptyArray(),
    condition: () -> QueryCondition
): E? = queryOne {
    takeIf { columns.isNotEmpty() }?.select(*columns)
    and(condition())
}

/**
 * 通过条件查询一条数据
 * @param columns 查询的列
 * @param condition 查询条件
 * @since 1.0.5
 */
inline fun <reified E : Any> filterOneColumn(
    vararg columns: QueryColumn = E::class.defaultColumns,
    condition: () -> QueryCondition
): E? = queryOne { select(*columns).and(condition()) }

/**
 * 通过条件查询多条数据
 * @param columns 查询的列
 * @param condition 查询条件
 */
inline fun <reified E : Any> filter(
    vararg columns: KProperty<*> = emptyArray(),
    condition: () -> QueryCondition
): List<E> =
    filterColumn(
        columns = columns.toQueryColumns(),
        condition = condition
    )

//    all----------
/**
 * 查询泛型对应的表的所有数据
 */
inline fun <reified E : Any> all() = filter<E>(condition = QueryCondition::createEmpty)

//    paginate----------
inline fun <reified E : Any> paginate(
    pageNumber: Number,
    pageSize: Number,
    totalRow: Number? = null,
    init: QueryScope.() -> Unit
): Page<E> = paginate(
    totalRow?.let { Page(pageNumber, pageSize, it) } ?: Page<E>(pageNumber, pageSize),
    init)

inline fun <reified E : Any> paginateWith(
    pageNumber: Number,
    pageSize: Number,
    totalRow: Number? = null,
    queryConditionGet: () -> QueryCondition
): Page<E> = paginate(
    totalRow?.let { Page(pageNumber, pageSize, it) } ?: Page<E>(pageNumber, pageSize)
) { where(queryConditionGet()) }


inline fun <reified E : Any> paginate(
    page: Page<E>,
    init: QueryScope.() -> Unit
): Page<E> = try {
    E::class.baseMapper.paginate(page, queryScope(init = init))
} catch (e: MybatisFlexException) {
    E::class.tableInfo.run {
        paginateRows(schema, tableName, Page(page.pageNumber, page.pageSize)) {
            init()
            if (this.hasSelect().not()) select(*E::class.defaultColumns)
        }.toEntityPage()
    }
}

inline fun paginateRows(
    schema: String? = null,
    tableName: String? = null,
    page: Page<Row>? = null,
    init: QueryScope.() -> Unit
): Page<Row> = Db.paginate(schema, tableName, page, queryScope(init = init))


//    update----------
inline fun <reified E : Any> update(scope: UpdateScope<E>.() -> Unit): Int =
    updateScope<E>().run {
        scope()
        return try {
            E::class.baseMapper.updateByQuery(updateWrapper.toEntity(), this)
        } catch (e: MybatisFlexException) {
            E::class.tableInfo.let {
                Db.updateByQuery(it.schema, it.tableName, updateWrapper.toRow(), this)
            }
        }
    }

//    delete----------
/**
 * 根据主键删除数据。如果是多个主键的情况下，请直接传入多个例如 ：deleteById(1,"zs",100)
 * 如果没有自定义Mapper时需要注意实体类中主键的顺序与传入的id顺序一致
 *
 */
inline fun <reified E : Any> deleteById(vararg id: Serializable) =
    try {
        E::class.baseMapper.deleteById(id)
    } catch (e: MybatisFlexException) {
        E::class.tableInfo.let {
            val row = Row.ofKey(it.primaryColumns.joinToString(separator = ","), id)
            Db.deleteById(it.schema, it.tableName, row)
        }
    }

/**
 * 根据map的key对应的字段比较删除
 */
inline fun <reified E : Any> deleteByMap(vararg propPairs: Pair<KProperty1<E, Serializable>, Serializable>): Int {
    val propMap = propPairs.associate { id -> id.first.column.name to id.second }
    return try {
        E::class.baseMapper.deleteByMap(propMap)
    } catch (e: MybatisFlexException) {
        E::class.tableInfo.let {
            Db.deleteByMap(it.schema, it.tableName, propMap)
        }
    }
}

/**
 * 根据返回的条件删除
 */
inline fun <reified E : Any> deleteWith(noinline condition: () -> QueryCondition) =
    try {
        E::class.baseMapper.deleteByCondition(condition)
    } catch (e: MybatisFlexException) {
        E::class.tableInfo.let {
            Db.deleteByCondition(it.schema, it.tableName, condition())
        }
    }