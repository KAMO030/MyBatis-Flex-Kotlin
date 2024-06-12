package com.mybatisflex.kotlin.codegen.config

import com.squareup.kotlinpoet.TypeSpec

data class ScopedTableOptions<out T : OptionScope>(
    val scope: T,
    val option: TableOptions
) : TableOptions by option {
    constructor(
        scope: T,
        optionName: String,
        rootSourceDir: String,
        basePackage: String = "",
    ) : this(
        scope = scope,
        TableOptionsImpl(
            optionName = optionName,
            rootSourceDir = rootSourceDir,
            packageName = basePackage,
            kind = when (scope) {
                is InterfaceOptionScope -> TypeSpec.Kind.INTERFACE
                else -> TypeSpec.Kind.CLASS
            }
        )
    )
}

infix fun <T : OptionScope> TableOptions.withScope(scope: T): ScopedTableOptions<T> {
    if (this is ScopedTableOptions<*>) {
        return ScopedTableOptions(scope, option)
    }
    return ScopedTableOptions(scope, this)
}

inline fun <T : OptionScope> TableOptions.withScope(scope: T, init: ScopedTableOptions<T>.() -> Unit): ScopedTableOptions<T> {
    return withScope(scope).also(init)
}