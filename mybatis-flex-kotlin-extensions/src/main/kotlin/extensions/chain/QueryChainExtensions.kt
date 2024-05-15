package com.mybatisflex.kotlin.extensions.chain

import com.mybatisflex.core.query.QueryChain

inline fun <reified T: Any> QueryChain(): QueryChain<T> = QueryChain.of(T::class.java)

fun QueryChain<*>.toList(): List<*> = list()

@JvmName("toListAs")
inline fun <reified T> QueryChain<*>.toList(): List<T> = listAs(T::class.java)

inline fun <reified T> QueryChain<*>.toTypedList(): List<T> = objListAs(T::class.java)

fun <T> QueryChain<T>.single(): T = one()

fun <T> QueryChain<T>.singleOrNull(): T? = one()

inline fun <reified T> QueryChain<*>.singleAs(): T = oneAs(T::class.java)

inline fun <reified T> QueryChain<*>.singleAsOrNull(): T? = oneAs(T::class.java)