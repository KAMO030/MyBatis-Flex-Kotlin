package com.mybatisflex.kotlin.codegen.config

sealed class ThreeTierScope(val key: String) {

    object TableDef : ThreeTierScope("TableDef")

    object Entity : ThreeTierScope("Entity")

    object Mapper : ThreeTierScope("Mapper")

    object Service : ThreeTierScope("Service")

    object ServiceImpl : ThreeTierScope("ServiceImpl")

    object Controller : ThreeTierScope("Controller")

}