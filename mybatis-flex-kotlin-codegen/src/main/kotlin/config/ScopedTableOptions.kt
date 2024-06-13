package com.mybatisflex.kotlin.codegen.config

import com.squareup.kotlinpoet.TypeSpec

/**
 * 作用域配置项
 * 单一只作为需要限定扩展配置项方法时使用的作用域包装
 */
class ScopedTableOptions<out T : OptionScope> internal constructor(
    val options: TableOptions
) : TableOptions by options

fun <T : OptionScope> TableOptions.withScope(
    scoped: (ScopedTableOptions<T>.() -> Unit)
) {
    if (this is ScopedTableOptions<*>) {
        ScopedTableOptions<T>(options)
    } else {
        ScopedTableOptions(this)
    }.scoped()
}

/**
 * 在Configuration中注册或获取到一个TableOptions对象,
 * 并生成一个ScopedTableOptions作用域进行配置
 * @param scope 作用域
 * @param creator 创建TableOptions,默认创建TableOptionsImpl,可以是其他TableOptions的实现类用来扩展逻辑
 * @param configure 配置作用域
 */
@PublishedApi
internal inline fun <T : OptionScope> TableConfiguration.scopedOption(
    scope: T,
    creator: (String, TypeSpec.Kind) -> TableOptions = { scopeName, kind ->
        TableOptionsImpl(scopeName, this, kind)
    },
    noinline configure: ScopedTableOptions<T>.() -> Unit = {}
) {
    val kind = when (scope) {
        is InterfaceOptionScope -> TypeSpec.Kind.INTERFACE
        else -> TypeSpec.Kind.CLASS
    }
    creator(scope.scopeName, kind)
        .also { this.optionsMap[it.optionName] = it }
        .withScope(configure)
}