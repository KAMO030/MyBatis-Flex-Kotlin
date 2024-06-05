package com.mybatisflex.kotlin.codegen.config

import com.mybatisflex.kotlin.codegen.annotation.GeneratorDsl
import com.mybatisflex.kotlin.codegen.internal.asClassName
import com.squareup.kotlinpoet.TypeSpec
@GeneratorDsl
inline fun TableOption.onController(option: GenerateOption.() -> Unit = {}) {
    getOrRegister("Controller").option()
}

@GeneratorDsl
inline fun TableOption.onService(option: GenerateOption.() -> Unit = {}) {
    getOrRegister("Service").apply {
        option()
        typeSpecBuilder = { TypeSpec.interfaceBuilder(tableNameMapper(it)) }
    }
}

@GeneratorDsl
inline fun TableOption.onMapper(option: GenerateOption.() -> Unit = {}) {
    getOrRegister("Mapper").apply {
        option()
        typeSpecBuilder = { TypeSpec.interfaceBuilder(tableNameMapper(it)) }
    }
}

@GeneratorDsl
inline fun TableOption.onEntity(option: GenerateOption.() -> Unit = {}) {
    getOrRegister("Entity").apply {
        tableNameMapper = {
            it.tableName.asClassName()
        }
        option()
    }
}

@GeneratorDsl
inline fun TableOption.onTableDef(option: GenerateOption.() -> Unit = {}) {
    getOrRegister("TableDef").option()
}

@GeneratorDsl
inline fun TableOption.onServiceImpl(option: GenerateOption.() -> Unit = {}) {
    getOrRegister("ServiceImpl").option()
}

@GeneratorDsl
fun TableOption.generateDefault() {
    onEntity {
        dataclass()
    }
    onTableDef()
    onMapper()
    onController()
    onService()
    onServiceImpl()
}