package com.mybatisflex.kotlin.codegen.config.extensions

import com.mybatisflex.kotlin.codegen.config.ScopedTableOptions
import com.mybatisflex.kotlin.codegen.config.TableConfiguration
import com.mybatisflex.kotlin.codegen.config.TableDefScope

inline fun ScopedTableOptions<TableDefScope>.default() {

}

@PublishedApi
internal inline fun TableConfiguration.dispatcher(
    crossinline configure: ScopedTableOptions<TableDefScope>.() -> Unit
): ScopedTableOptions<TableDefScope>.() -> Unit = {
    builderTransformer {
        configure()
    }
}