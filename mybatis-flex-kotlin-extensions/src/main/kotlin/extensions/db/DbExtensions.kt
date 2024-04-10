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
import com.mybatisflex.kotlin.annotation.InternalMybatisFlexApi
import com.mybatisflex.kotlin.extensions.kproperty.allColumns
import com.mybatisflex.kotlin.extensions.kproperty.column
import com.mybatisflex.kotlin.extensions.kproperty.defaultColumns
import com.mybatisflex.kotlin.extensions.kproperty.toQueryColumns
import com.mybatisflex.kotlin.extensions.mapper.deleteByCondition
import com.mybatisflex.kotlin.extensions.model.toEntities
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

val <E : Any> KClass<E>.queryTable: QueryTable
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
): E? = E::class.baseMapperOrNull?.selectOneByQuery(queryScope(columns = columns, init = init))
    ?: E::class.tableInfo.let {
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
): List<E> = E::class.baseMapperOrNull?.selectListByQuery(queryScope(columns = columns, init = init))
    ?: E::class.tableInfo.run {
        queryRows(schema = schema, tableName = tableName, columns = columns) {
            init()
            // 如果未调用select方法，则默认查询所有列
            if (this.hasSelect().not()) select(*E::class.defaultColumns)
        }.toEntities()
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
    init: QueryScope.() -> Unit = {}
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
    init: QueryScope.() -> Unit = {}
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
/**
 * 分页查询
 * @param pageNumber 当前页码
 * @param pageSize 每页大小
 * @param totalRow 总数居数量
 * @param init 查询作用域初始化函数
 */
inline fun <reified E : Any> paginate(
    pageNumber: Number,
    pageSize: Number,
    totalRow: Number = -1L,
    init: QueryScope.() -> Unit
): Page<E> = paginate(Page(pageNumber, pageSize, totalRow), init)

/**
 * 分页查询
 * @param pageNumber 当前页码
 * @param pageSize 每页大小
 * @param totalRow 总数居数量
 * @param condition 查询条件函数
 */
inline fun <reified E : Any> paginateWith(
    pageNumber: Number,
    pageSize: Number,
    totalRow: Number = -1,
    condition: () -> QueryCondition
): Page<E> = paginate(Page(pageNumber, pageSize, totalRow)) { where(condition()) }

/**
 * 分页查询
 * @param page 分页对象
 * @param init 查询作用域初始化函数
 */
@OptIn(InternalMybatisFlexApi::class)
inline fun <reified E : Any> paginate(
    page: Page<E>,
    init: QueryScope.() -> Unit
): Page<E> = E::class.baseMapperOrNull?.paginate(page, queryScope(init = init))
    ?: paginateRowsAs<E>(page, init)

/**
 * 分页查询Row
 * @param schema 模式
 * @param tableName 表名
 * @param init 查询作用域初始化函数
 */
inline fun paginateRows(
    schema: String? = null,
    tableName: String? = null,
    page: Page<Row>? = null,
    init: QueryScope.() -> Unit
): Page<Row> = Db.paginate(schema, tableName, page, queryScope(init = init))

/**
 * 分页查询Row
 * 此查询只会走RowMapper，用户无需使用此方法
 * 因为Db.paginate需要Page<Row>，而传入的Page为E范型所以需要在此方法统一转换
 * @param page 分页对象
 * @param init 查询作用域初始化函数
 * @since 1.0.8
 */
@InternalMybatisFlexApi
inline fun <reified E : Any> paginateRowsAs(
    page: Page<E>,
    init: QueryScope.() -> Unit
): Page<E> = E::class.tableInfo.run {
    val rowPage = Page<Row>(page.pageNumber, page.pageSize, page.totalRow)
    rowPage.setOptimizeCountQuery(page.needOptimizeCountQuery())
    paginateRows(schema, tableName, rowPage) {
        init()
        if (this.hasSelect().not()) select(*E::class.defaultColumns)
    }
    // 保证入参数和返回参数对象一致性处理
    page.records = rowPage.records.map { it.toEntity(E::class.java) }
    page.totalPage = rowPage.totalPage
    page.totalRow = rowPage.totalRow
    page
}


/**
 * 分页查询
 * @param E 实体类型
 * @param R 接收数据类型
 * @param page 分页对象
 * @param init 查询作用域初始化函数
 * @since 1.0.8
 */
@OptIn(InternalMybatisFlexApi::class)
inline fun <reified E : Any, reified R : Any> paginateAs(
    page: Page<R>,
    init: QueryScope.() -> Unit
): Page<R> = E::class.baseMapperOrNull?.paginateAs(page, queryScope(init = init), R::class.java)
    ?: paginateRowsAs<R>(page, init)

/**
 * 分页查询
 * @param E 实体类型
 * @param R 接收数据类型
 * @param pageNumber 当前页码
 * @param pageSize 每页大小
 * @param totalRow 总数居数量
 * @param init 查询作用域初始化函数
 * @since 1.0.8
 */
inline fun <reified E : Any, reified R : Any> paginateAs(
    pageNumber: Number,
    pageSize: Number,
    totalRow: Number = -1L,
    init: QueryScope.() -> Unit
): Page<R> = paginateAs<E, R>(Page(pageNumber, pageSize, totalRow), init)



//    update----------

/**
 * 更新数据
 * @param scope 更新作用域
 * @since 1.0.8
 */
inline fun <reified E : Any> update(scope: UpdateScope<E>.() -> Unit): Int =
    updateScope<E>().apply(scope).run {
        E::class.baseMapperOrNull?.updateByQuery(updateRow.toEntity(), this)
            ?: E::class.tableInfo.let {
                Db.updateByQuery(it.schema, it.tableName, updateRow.toRow(), this)
            }
    }

//    delete----------
/**
 * 根据主键删除数据。如果是多个主键的情况下，请直接传入多个例如 ：deleteById(1,"zs",100)
 * 如果没有自定义Mapper时需要注意实体类中主键的顺序与传入的id顺序一致
 * @since 1.0.8
 */
inline fun <reified E : Any> deleteById(vararg id: Serializable) =
    E::class.baseMapperOrNull?.deleteById(id) ?: E::class.tableInfo.let {
        Db.deleteById(it.schema, it.tableName, id)
    }

/**
 * 根据map的key对应的字段比较删除
 * @param propPairs 删除条件键值对
 * @sample <p>deleteByMap(age to 18, name to "zs")</p>
 * @since 1.0.8
 */
inline fun <reified E : Any> deleteByMap(vararg propPairs: Pair<KProperty1<E, Serializable>, Serializable>): Int {
    val propMap = propPairs.associate { id -> id.first.column.name to id.second }
    return E::class.baseMapperOrNull?.deleteByMap(propMap)
        ?: E::class.tableInfo.let {
            Db.deleteByMap(it.schema, it.tableName, propMap)
        }
}

/**
 * 根据返回的条件删除
 * @param condition 条件函数
 * @since 1.0.8
 */
inline fun <reified E : Any> deleteWith(noinline condition: () -> QueryCondition) =
    E::class.baseMapperOrNull?.deleteByCondition(condition)
        ?: E::class.tableInfo.let {
            Db.deleteByCondition(it.schema, it.tableName, condition())
        }


//    insert----------
/**
 * 插入一条数据
 * @param entity 实体对象
 * @since 1.0.8
 */
inline fun <reified E : Any> insert(entity: E): Int =
    E::class.baseMapperOrNull?.insert(entity)
        ?: E::class.tableInfo.let {
            Db.insert(it.schema, it.tableName, entity.toRow())
        }