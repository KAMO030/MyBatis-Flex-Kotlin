package com.mybatisflex.kotlin.codegen.config.extensions

import com.mybatisflex.kotlin.codegen.annotation.GeneratorDsl
import com.mybatisflex.kotlin.codegen.config.*

@GeneratorDsl
inline fun TableConfiguration.onController(
    configure: ScopedTableOptions<ControllerScope>.() -> Unit = {}
) = scopedOption(ControllerScope, configure)


@GeneratorDsl
inline fun TableConfiguration.onServiceImpl(
    crossinline configure: ScopedTableOptions<ServiceImplScope>.() -> Unit = { default() }
) = scopedOption(ServiceImplScope, dispatcher(configure))


@GeneratorDsl
inline fun TableConfiguration.onService(
    crossinline configure: ScopedTableOptions<ServiceScope>.() -> Unit = { default() }
) = scopedOption(ServiceScope, dispatcher(configure))


@GeneratorDsl
inline fun TableConfiguration.onMapper(
    configure: ScopedTableOptions<MapperScope>.() -> Unit = {}
) = scopedOption(MapperScope) {
    configure()
    columnMetadataTransformer = { emptySequence() }
}


@GeneratorDsl
inline fun TableConfiguration.onEntity(
    crossinline configure: ScopedTableOptions<EntityScope>.() -> Unit = { default() }
) = scopedOption(EntityScope, dispatcher(configure))


@GeneratorDsl
inline fun TableConfiguration.onTableDef(
    configure: ScopedTableOptions<TableDefScope>.() -> Unit = {}
) = scopedOption(TableDefScope, configure)


inline fun <reified T : OptionScope> TableConfiguration.scopedOption(
    scope: T,
    configure: ScopedTableOptions<T>.() -> Unit = {}
) {
    val op = getOrRegister(scope.scopeName)
    registerOption(op withScope scope) {
        configure()
    }
}

@GeneratorDsl
fun TableConfiguration.generateDefault() {
    onEntity()
    onTableDef()
    onMapper()
    onController()
    onService()
    onServiceImpl()
}