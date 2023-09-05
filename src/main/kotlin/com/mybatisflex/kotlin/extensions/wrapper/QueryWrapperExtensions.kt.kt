package com.mybatisflex.kotlin.extensions.wrapper

import com.mybatisflex.core.query.QueryCondition
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.core.table.TableDef
import com.mybatisflex.kotlin.extensions.kproperty.column
import com.mybatisflex.kotlin.scope.QueryScope
import com.mybatisflex.kotlin.scope.queryScope
import kotlin.reflect.KClass
import kotlin.reflect.KProperty


fun QueryWrapper.from(init: (QueryScope.() -> Unit)? = null): QueryWrapper = this.from(queryScope(init = init))

fun QueryWrapper.from(vararg entities: KClass<*>): QueryWrapper = this.from(*entities.map { it.java }.toTypedArray())

fun QueryWrapper.select(vararg properties: KProperty<*>): QueryWrapper =
    this.select(*properties.map { it.column }.toTypedArray())

fun <T : TableDef> QueryWrapper.where(tableDef: T, build: T.() -> QueryCondition): QueryWrapper =
    this.where(build(tableDef))

fun QueryWrapper.where(build: QueryWrapper.() -> QueryCondition): QueryWrapper = this.where(build(this))