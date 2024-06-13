package com.mybatisflex.kotlin.codegen.config.extensions

import com.mybatisflex.kotlin.codegen.annotation.GeneratorDsl
import com.mybatisflex.kotlin.codegen.config.*

@GeneratorDsl
inline fun TableConfiguration.onController(
    crossinline configure: ScopedTableOptions<ControllerScope>.() -> Unit = { default() }
) = scopedOption(
    scope = ControllerScope,
    configure = dispatcher(configure)
)


@GeneratorDsl
inline fun TableConfiguration.onServiceImpl(
    crossinline configure: ScopedTableOptions<ServiceImplScope>.() -> Unit = { default() }
) = scopedOption(
    scope = ServiceImplScope,
    configure = dispatcher(configure)
)


@GeneratorDsl
inline fun TableConfiguration.onService(
    crossinline configure: ScopedTableOptions<ServiceScope>.() -> Unit = { default() }
) = scopedOption(
    scope = ServiceScope,
    configure = dispatcher(configure)
)


@GeneratorDsl
inline fun TableConfiguration.onMapper(
    crossinline configure: ScopedTableOptions<MapperScope>.() -> Unit = { default() }
) = scopedOption(
    scope = MapperScope,
    configure = dispatcher(configure)
)

@GeneratorDsl
inline fun TableConfiguration.onEntity(
    crossinline configure: ScopedTableOptions<EntityScope>.() -> Unit = { default() }
) = scopedOption(
    scope = EntityScope,
    configure = dispatcher(configure)
)


@GeneratorDsl
inline fun TableConfiguration.onTableDef(
    crossinline configure: ScopedTableOptions<TableDefScope>.() -> Unit = { default() }
) = scopedOption(
    scope = TableDefScope,
    configure = dispatcher(configure)
)


@GeneratorDsl
fun TableConfiguration.generateDefault() {
    onEntity()
    onTableDef()
    onMapper()
    onController()
    onService()
    onServiceImpl()
}