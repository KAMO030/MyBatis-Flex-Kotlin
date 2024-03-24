@file:Suppress("MemberVisibilityCanBePrivate")
package com.mybatisflex.kotlin.scope

import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryWrapperAdapter
import com.mybatisflex.core.table.TableInfoFactory
import com.mybatisflex.core.update.UpdateWrapper
import com.mybatisflex.core.util.UpdateEntity
import com.mybatisflex.kotlin.extensions.kproperty.column
import kotlin.reflect.KProperty1

class UpdateScope<T>(
    entityClass: Class<T>,
) : QueryWrapperAdapter<UpdateScope<T>>() {

    @Suppress("UNCHECKED_CAST")
    @PublishedApi
    internal val updateWrapper: UpdateWrapper<T> = UpdateEntity.of(entityClass) as UpdateWrapper<T>

    private val tableInfo = TableInfoFactory.ofEntityClass(entityClass)


    infix fun <V> KProperty1<T, V>.set(value: V) {
        updateWrapper.set(tableInfo.getColumnByProperty(this.name), value)
    }

    infix fun <V> KProperty1<T, V>.setRaw(queryWrapper: QueryScope.() -> Unit) {
        updateWrapper.setRaw(tableInfo.getColumnByProperty(this.name), queryScope().apply(queryWrapper).limit(1))
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


inline fun <reified T> updateScope(): UpdateScope<T> {
    return UpdateScope(T::class.java)
}