package com.mybatisflex.kotlin.codegen.config.extensions

import com.mybatisflex.kotlin.codegen.config.MapperScope
import com.mybatisflex.kotlin.codegen.config.ScopedTableOptions
import com.mybatisflex.kotlin.codegen.config.TableConfiguration

inline fun ScopedTableOptions<MapperScope>.default() {

}

@PublishedApi
internal inline fun TableConfiguration.dispatcher(
    crossinline configure: ScopedTableOptions<MapperScope>.() -> Unit
): ScopedTableOptions<MapperScope>.() -> Unit = {
    configure()
    columnMetadataTransformer = { emptySequence() }
}