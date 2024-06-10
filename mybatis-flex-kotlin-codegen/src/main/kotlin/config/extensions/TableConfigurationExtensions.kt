package com.mybatisflex.kotlin.codegen.config.extensions

import com.mybatisflex.kotlin.codegen.annotation.GeneratorDsl
import com.mybatisflex.kotlin.codegen.config.*
import com.mybatisflex.kotlin.codegen.internal.asClassName
import com.squareup.kotlinpoet.TypeSpec

@GeneratorDsl
inline fun TableConfiguration.onController(
    configure: ScopedTableOptions<ControllerScope>.() -> Unit = {}
) {
    getOrRegisterScopedOption(ControllerScope, configure)
}

@GeneratorDsl
inline fun TableConfiguration.onServiceImpl(
    configure: ScopedTableOptions<ServiceImplScope>.() -> Unit = {}
) {
    getOrRegisterScopedOption(ServiceImplScope, configure)
}

@GeneratorDsl
inline fun TableConfiguration.onService(
    configure: ScopedTableOptions<ServiceScope>.() -> Unit = {}
) {
    getOrRegister(ServiceScope.scopeName) { optionsName ->
        ScopedTableOptions(ServiceScope, TableOptionsImpl(optionsName, rootSourceDir)).apply {
            configure()
            typeSpecBuilder = {
                TypeSpec.interfaceBuilder(tableNameMapper(it))
            }
        }
    }
}

@GeneratorDsl
inline fun TableConfiguration.onMapper(
    configure: ScopedTableOptions<MapperScope>.() -> Unit = {}
) {
    getOrRegister(MapperScope.scopeName) { optionsName ->
        ScopedTableOptions(MapperScope, TableOptionsImpl(optionsName, rootSourceDir)).apply {
            configure()
            typeSpecBuilder = { TypeSpec.interfaceBuilder(tableNameMapper(it)) }
        }
    }
}

@GeneratorDsl
inline fun TableConfiguration.onEntity(
    configure: ScopedTableOptions<EntityScope>.() -> Unit = {}
) {
    getOrRegister(EntityScope.scopeName) { optionsName ->
        ScopedTableOptions(EntityScope, TableOptionsImpl(optionsName, rootSourceDir)).apply {
            configure()
            tableNameMapper = {
                it.tableName.asClassName()
            }
        }
    }
}

@GeneratorDsl
inline fun TableConfiguration.onTableDef(
    configure: ScopedTableOptions<TableDefScope>.() -> Unit = {}
) {
    getOrRegisterScopedOption(TableDefScope, configure)
}

@GeneratorDsl
fun TableConfiguration.generateDefault() {
    onEntity {
        dataclass()
    }
    onTableDef()
    onMapper()
    onController()
    onService()
    onServiceImpl()
}