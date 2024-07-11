@file:Suppress("MemberVisibilityCanBePrivate")

package com.mybatisflex.kotlin.scope

import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.query.QueryWrapperAdapter
import com.mybatisflex.core.update.UpdateWrapper
import com.mybatisflex.kotlin.extensions.kproperty.column
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

class UpdateScope<T>(entryClass: Class<T>) : QueryWrapperAdapter<UpdateScope<T>>() {

    @PublishedApi
    internal val updateRow: UpdateWrapper<T> = UpdateWrapper.of(entryClass)

    /**
     * 设置字段对应的值
     * @param value
     * @param V 子查询的select属性类型
     */
    infix fun <V> KProperty1<T, V>.set(value: V) {
        value.let {
            if (it is KProperty<*>) it.column else it
        }.also {
            updateRow.set(column, it)
        }
    }

    /**
     * 设置字段对应原生值
     * @param value 类型可以是String, QueryWrapper(QueryScope), QueryColumn(KProperty), QueryCondition
     * @since 1.1.1
     */
    infix fun KProperty1<T, *>.setRaw(value: Any) {
        value.let {
            if (it is KProperty<*>) it.column else it
        }.also {
            updateRow.setRaw(column, it)
        }
    }

    /**
     * 设置字段子查询值
     * @param queryScope 子查询作用域
     *
     * ```kotlin
     * A::ID setRaw {
     *   select(B::ID)
     *   from(B::class)
     *   where(B::AGE eq A::AGE)
     * }
     * ```
     * ```sql
     * a.id = (select(b.id) from b where b.age = a.age limit 1)
     * ```
     */
    infix fun KProperty1<T, *>.setRaw(queryScope: QueryScope.() -> Unit) {
        updateRow.setRaw(column, queryScope().apply(queryScope).limit(1))
    }

    /**
     * 设置字段子查询值(比setRaw(queryScope)少写一个select)
     * @param column 需要设置的子查询select字段
     * @param queryScope 子查询作用域
     *
     * ```kotlin
     * A::ID.setRaw(B::ID.column){
     *   from(B::class)
     *   where(B::AGE eq A::AGE)
     * }
     * ```
     * ```sql
     * a.id = (select(b.id) from b where b.age = a.age limit 1)
     * ```
     */
    fun KProperty1<T, *>.setRaw(column: QueryColumn, queryScope: QueryScope.() -> Unit) {
        setRaw {
            queryScope()
            select(column)
        }
    }

    /**
     * 子查询赋值
     * @param property 需要设置的子查询select属性(比setRaw(queryScope)少写一个select和一个from)
     * @param queryScope 子查询作用域
     * @param T2 子查询from的实体类型,会自动from表
     *
     * ```kotlin
     * A::ID.setRaw(B::ID){ where(B::AGE eq A::AGE) }
     * ```
     * ```sql
     * a.id = (select(b.id) from b where b.age = a.age limit 1)
     * ```
     */
    inline fun <reified T2> KProperty1<T, *>.setRaw(
        property: KProperty1<T2, *>,
        crossinline queryScope: QueryScope.() -> Unit
    ) {
        setRaw(property.column) {
            queryScope()
            from(T2::class.java)
        }
    }

}

inline fun <reified T> updateScope(): UpdateScope<T> = UpdateScope(T::class.java)
