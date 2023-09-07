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
package com.mybatisflex.kotlin.extensions.model

import com.mybatisflex.core.activerecord.Model
import com.mybatisflex.core.row.Row
import com.mybatisflex.core.row.RowUtil
import com.mybatisflex.core.util.SqlUtil
import com.mybatisflex.kotlin.extensions.db.*
import java.io.Serializable
import kotlin.reflect.KClass

/*
 * 实体操作扩展
 * @author 卡莫sama
 */

inline fun <reified T> Row.toEntity(): T = RowUtil.toEntity(this, T::class.java)

inline fun <reified E> Collection<Row>.toEntities(): MutableList<E> = RowUtil.toEntityList(this.toMutableList(), E::class.java)

inline fun <reified E : Any> all(): List<E> =
    E::class.baseMapper.selectAll()

val <E : Any> KClass<E>.all: List<E>
    get() = baseMapper.selectAll()

inline fun <reified E : Model<E>> List<E>.batchInsert() = E::class.baseMapper.insertBatch(this)

fun <E : Model<E>> List<E>.batchUpdateById(): Boolean = all(Model<E>::updateById)

inline fun <reified E : Model<E>> List<E>.batchDeleteById(): Boolean {
    val tableInfo = E::class.tableInfo
    //拿到集合中所有实体的主键
    val primaryValues = this.map { tableInfo.getPkValue(it) as Serializable }
    return SqlUtil.toBool(E::class.baseMapper.deleteBatchByIds(primaryValues))
}

