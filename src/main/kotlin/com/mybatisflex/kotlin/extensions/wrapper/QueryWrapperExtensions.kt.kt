package com.mybatisflex.kotlin.extensions.wrapper

import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.kotlin.extensions.kproperty.column
import com.mybatisflex.kotlin.scope.QueryScope
import com.mybatisflex.kotlin.scope.queryScope
import kotlin.reflect.KClass
import kotlin.reflect.KProperty


fun QueryWrapper.from(init: (QueryScope.() -> Unit)? = null): QueryWrapper = this.from(queryScope(init = init))

fun QueryWrapper.from(vararg entities: KClass<*>): QueryWrapper = this.from(*entities.map { it.java }.toTypedArray())

fun QueryWrapper.select( properties:()-> Iterable<KProperty<*>>): QueryWrapper =
    this.select(*(properties().map { it.column }.toTypedArray()))

