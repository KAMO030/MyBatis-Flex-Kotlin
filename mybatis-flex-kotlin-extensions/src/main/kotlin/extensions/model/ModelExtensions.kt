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
package com.mybatisflex.kotlin.extensions.model

import com.mybatisflex.core.activerecord.MapperModel
import com.mybatisflex.core.paginate.Page
import com.mybatisflex.core.row.Row
import com.mybatisflex.core.row.RowUtil
import com.mybatisflex.core.table.EntityMetaObject
import com.mybatisflex.core.table.TableInfo
import com.mybatisflex.core.table.TableInfoFactory
import com.mybatisflex.core.update.UpdateWrapper
import com.mybatisflex.core.util.SqlUtil
import com.mybatisflex.kotlin.extensions.db.*
import java.io.Serializable

/*
 * 实体操作扩展
 * @author KAMOsama
 */

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <reified T> Row.toEntity(): T = RowUtil.toEntity(this, T::class.java)

fun <T : Any> T.toRow(): Row {
    val row = Row()
    if (this is UpdateWrapper<*>) {
        this.updates.forEach { (column, value) ->
            row.put(column, value)
        }
    } else {
        val tableInfo: TableInfo = TableInfoFactory.ofEntityClass(javaClass)
        val metaObject = EntityMetaObject.forObject(this, tableInfo.reflectorFactory)
        tableInfo.primaryKeyList.forEach { idInfo ->
            metaObject.getValue(idInfo.property)?.let { row[idInfo.column] = it }
        }
        tableInfo.columnInfoList.forEach { idInfo ->
            metaObject.getValue(idInfo.property)?.let { row[idInfo.column] = it }
        }
    }

    return row
}

inline fun <reified E> Collection<Row>.toEntities(): MutableList<E> =
    RowUtil.toEntityList(this.toMutableList(), E::class.java)

inline fun <reified T> Row.toObject(): T = RowUtil.toObject(this, T::class.java)

inline fun <reified E> Collection<Row>.toObjects(): MutableList<E> =
    RowUtil.toObjectList(this.toMutableList(), E::class.java)

inline fun <reified T> Page<Row>.toEntityPage(): Page<T> = Page(records.toEntities(), pageNumber, pageSize, totalRow)

inline fun <reified E : MapperModel<E>> List<E>.batchInsert() = E::class.baseMapper.insertBatch(this)

fun <E : MapperModel<E>> List<E>.batchUpdateById(): Boolean = all(MapperModel<E>::updateById)

inline fun <reified E : MapperModel<E>> List<E>.batchDeleteById(): Boolean {
    val tableInfo = E::class.tableInfo
    //拿到集合中所有实体的主键
    val primaryValues = this.map { tableInfo.getPkValue(it) as Serializable }
    return SqlUtil.toBool(E::class.baseMapper.deleteBatchByIds(primaryValues))
}
