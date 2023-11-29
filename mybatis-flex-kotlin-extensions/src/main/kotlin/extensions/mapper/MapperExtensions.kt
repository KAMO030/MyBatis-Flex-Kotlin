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
package com.mybatisflex.kotlin.extensions.mapper

import com.mybatisflex.core.BaseMapper
import com.mybatisflex.core.activerecord.MapperModel
import com.mybatisflex.core.field.FieldQueryBuilder
import com.mybatisflex.core.query.QueryCondition
import com.mybatisflex.kotlin.extensions.db.baseMapper
import com.mybatisflex.kotlin.scope.QueryScope
import com.mybatisflex.kotlin.scope.queryScope
import java.util.function.Consumer
import kotlin.reflect.KClass

/*
 * 映射器操作扩展
 * @author KAMOsama
 */

fun <T> BaseMapper<T>.query(init: QueryScope.() -> Unit): List<T> =
    queryScope(init = init).let(this::selectListByQuery)

fun <T> BaseMapper<T>.filter(init: () -> QueryCondition): List<T> =
    init().let(this::selectListByCondition)

fun <T> BaseMapper<T>.updateByQuery(entity: T, init: QueryScope.() -> Unit): Int =
    this.updateByQuery(entity, queryScope(init = init))

fun <T> BaseMapper<T>.updateByCondition(entity: T, init: () -> QueryCondition): Int =
    this.updateByCondition(entity, init())

fun <T> BaseMapper<T>.deleteByQuery(init: QueryScope.() -> Unit): Int =
    queryScope(init = init).let(this::deleteByQuery)

fun <T> BaseMapper<T>.deleteByCondition(init: () -> QueryCondition): Int =
    init().let(this::deleteByCondition)

inline fun <T, reified R> BaseMapper<T>.selectListByQueryAs(init: QueryScope.() -> Unit): List<R> =
    queryScope(init = init).let { this.selectListByQueryAs(it, R::class.java) }

inline fun <T, reified R> BaseMapper<T>.selectListByQueryAs(
    vararg consumers: Consumer<FieldQueryBuilder<R>>,
    init: QueryScope.() -> Unit
): List<R> = queryScope(init = init).let { this.selectListByQueryAs(it, R::class.java, *consumers) }

inline fun <T, reified R> BaseMapper<T>.selectObjectListByQueryAs(
    init: QueryScope.() -> Unit
): List<R> = queryScope(init = init).let { this.selectObjectListByQueryAs(it, R::class.java) }

inline fun <T, reified R> BaseMapper<T>.selectObjectByQueryAs(
    init: QueryScope.() -> Unit
): R = queryScope(init = init).let { this.selectObjectByQueryAs(it, R::class.java) }

inline fun <T, reified R> BaseMapper<T>.selectListWithRelationsByQueryAs(
    init: QueryScope.() -> Unit
): List<R> = queryScope(init = init).let { this.selectListWithRelationsByQueryAs(it, R::class.java) }

inline fun <T, reified R> BaseMapper<T>.selectListWithRelationsByQueryAs(
    vararg consumers: Consumer<FieldQueryBuilder<R>>,
    init: QueryScope.() -> Unit
): List<R> = queryScope(init = init).let { this.selectListWithRelationsByQueryAs(it, R::class.java, *consumers) }

//    all-----------
val <E : Any> KClass<E>.all: List<E>
    get() = baseMapper.selectAll()

//    insert-----------
inline fun <reified E : MapperModel<E>> insert(build: E.() -> Unit): Int =
    E::class.java.newInstance().apply(build).run { insert() }


inline fun <reified E : MapperModel<E>> E.insert(): Int = baseMapper().insert(this)

//    update-----------
inline fun <reified E : MapperModel<E>> E.update(conditionBlock: (E) -> QueryCondition): Int =
    baseMapper().updateByCondition(this, conditionBlock(this))

//    delete-----------
inline fun <reified E : MapperModel<E>> E.remove(conditionBlock: (E) -> QueryCondition): Int =
    baseMapper().deleteByCondition(conditionBlock(this))
