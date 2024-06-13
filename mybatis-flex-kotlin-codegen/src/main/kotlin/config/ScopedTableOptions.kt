package com.mybatisflex.kotlin.codegen.config

import com.mybatisflex.kotlin.codegen.metadata.TableMetadata
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeSpec.Kind

data class ScopedTableOptions<out T : OptionScope>(
    val scope: T,
    val option: TableOptions
) : TableOptions by option {

    override var typeSpecBuilder: (TableMetadata) -> TypeSpec.Builder = {
        builderByKind(kind, tableNameMapper(it))
    }

    override val kind: Kind = when (scope) {
        is InterfaceOptionScope -> Kind.INTERFACE
        else -> Kind.CLASS
    }

}

infix fun <T : OptionScope> TableOptions.withScope(scope: T): ScopedTableOptions<T> {
    return if (this is ScopedTableOptions<*>) {
        ScopedTableOptions(scope, option)
    } else {
        ScopedTableOptions(scope, this)
    }
}

inline fun <T : OptionScope> TableOptions.withScope(scope: T, init: ScopedTableOptions<T>.() -> Unit): ScopedTableOptions<T> {
    return withScope(scope).also(init)
}