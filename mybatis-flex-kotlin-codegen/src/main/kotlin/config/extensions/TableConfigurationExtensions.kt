package com.mybatisflex.kotlin.codegen.config.extensions

import com.mybatisflex.kotlin.codegen.annotation.GeneratorDsl
import com.mybatisflex.kotlin.codegen.config.ScopeTableOptions
import com.mybatisflex.kotlin.codegen.config.TableConfiguration
import com.mybatisflex.kotlin.codegen.config.ThreeTierScope
import com.mybatisflex.kotlin.codegen.config.getOrRegister
import com.mybatisflex.kotlin.codegen.internal.asClassName
import com.squareup.kotlinpoet.TypeSpec

@GeneratorDsl
inline fun TableConfiguration.onController(
    configure: ScopeTableOptions<ThreeTierScope.Controller>.() -> Unit = {}
) {
    getOrRegister(ThreeTierScope.Controller.key) { optionsName ->
        ScopeTableOptions<ThreeTierScope.Controller>(optionsName, rootSourceDir).apply(configure)
    }
}

@GeneratorDsl
inline fun TableConfiguration.onServiceImpl(
    configure: ScopeTableOptions<ThreeTierScope.ServiceImpl>.() -> Unit = {}
) {
    getOrRegister(ThreeTierScope.ServiceImpl.key) { optionsName ->
        ScopeTableOptions<ThreeTierScope.ServiceImpl>(optionsName, rootSourceDir).also(configure)
    }
}

@GeneratorDsl
inline fun TableConfiguration.onService(
    configure: ScopeTableOptions<ThreeTierScope.Service>.() -> Unit = {}
) {
    getOrRegister(ThreeTierScope.Service.key) { optionsName ->
        ScopeTableOptions<ThreeTierScope.Service>(optionsName, rootSourceDir).apply {
            configure()
            typeSpecBuilder = { TypeSpec.interfaceBuilder(tableNameMapper(it)) }
        }
    }
}

@GeneratorDsl
inline fun TableConfiguration.onMapper(
    configure: ScopeTableOptions<ThreeTierScope.Mapper>.() -> Unit = {}
) {
    getOrRegister(ThreeTierScope.Mapper.key) { optionsName ->
        ScopeTableOptions<ThreeTierScope.Mapper>(optionsName, rootSourceDir).apply {
            configure()
            typeSpecBuilder = { TypeSpec.interfaceBuilder(tableNameMapper(it)) }
        }
    }
}

@GeneratorDsl
inline fun TableConfiguration.onEntity(
    configure: ScopeTableOptions<ThreeTierScope.Entity>.() -> Unit = {}
) {
    getOrRegister(ThreeTierScope.Entity.key) { optionsName ->
        ScopeTableOptions<ThreeTierScope.Entity>(optionsName, rootSourceDir).apply {
            tableNameMapper = {
                it.tableName.asClassName()
            }
            configure()
        }
    }
}

@GeneratorDsl
inline fun TableConfiguration.onTableDef(
    configure: ScopeTableOptions<ThreeTierScope.TableDef>.() -> Unit = {}
) {
    getOrRegister(ThreeTierScope.TableDef.key) { optionsName ->
        ScopeTableOptions<ThreeTierScope.TableDef>(optionsName, rootSourceDir).apply(configure)
    }
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