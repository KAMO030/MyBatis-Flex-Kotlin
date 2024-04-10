@file:Suppress("MemberVisibilityCanBePrivate")
package com.mybatisflex.kotlin.scope

import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryWrapperAdapter
import com.mybatisflex.core.update.UpdateWrapper
import com.mybatisflex.kotlin.extensions.kproperty.column
import kotlin.reflect.KProperty1

class UpdateScope<T>(entryClass: Class<T>) : QueryWrapperAdapter<UpdateScope<T>>() {

    @PublishedApi
    internal val updateRow: UpdateWrapper<T> = UpdateWrapper.of(entryClass)


    infix fun <V> KProperty1<T, V>.set(value: V) {
        updateRow.set(column, value)
    }

    infix fun <V> KProperty1<T, V>.setRaw(queryWrapper: QueryScope.() -> Unit) {
        updateRow.setRaw(column, queryScope().apply(queryWrapper).limit(1))
    }

    fun <V> KProperty1<T, V>.setRaw(column: QueryColumn, queryWrapper: QueryScope.() -> Unit) {
        setRaw {
            queryWrapper()
            select(column)
        }
    }

    fun <V> KProperty1<T, V>.setRaw(property: KProperty1<*, *>, queryWrapper: QueryScope.() -> Unit) {
        setRaw(property.column, queryWrapper)
    }

}

inline fun <reified T> updateScope(): UpdateScope<T> = UpdateScope(T::class.java)
