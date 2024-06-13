package com.mybatisflex.kotlin.codegen.config.extensions

import com.mybatisflex.kotlin.codegen.config.ControllerScope
import com.mybatisflex.kotlin.codegen.config.ScopedTableOptions
import com.mybatisflex.kotlin.codegen.config.TableConfiguration

inline fun ScopedTableOptions<ControllerScope>.default() {

}

@PublishedApi
internal inline fun TableConfiguration.dispatcher(
    crossinline configure: ScopedTableOptions<ControllerScope>.() -> Unit
): ScopedTableOptions<ControllerScope>.() -> Unit = {
    configure()
}