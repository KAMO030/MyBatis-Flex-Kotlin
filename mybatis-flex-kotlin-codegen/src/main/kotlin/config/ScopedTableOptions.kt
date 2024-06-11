package com.mybatisflex.kotlin.codegen.config

data class ScopedTableOptions<out T : OptionScope>(
    val scope: T,
    val option: TableOptions
) : TableOptions by option {
    constructor(
        scope: T,
        optionName: String,
        rootSourceDir: String,
        basePackage: String = "",
    ) : this(scope, TableOptionsImpl(optionName, rootSourceDir, basePackage))
}

infix fun <T : OptionScope> TableOptions.withScope(scope: T): ScopedTableOptions<T> {
    return ScopedTableOptions(scope, this)
}

inline fun <T : OptionScope> TableOptions.withScope(scope: T, init: ScopedTableOptions<T>.() -> Unit): ScopedTableOptions<T> {
    return ScopedTableOptions(scope, this).also(init)
}