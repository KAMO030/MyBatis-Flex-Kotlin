package com.mybatisflex.kotlin.codegen.config

interface OptionScope {
    val scopeName: String
}

open class ClassOptionScope(override val scopeName: String) : OptionScope

open class InterfaceOptionScope(override val scopeName: String) : OptionScope

object TableDefScope : ClassOptionScope("TableDef")

object EntityScope : ClassOptionScope("Entity")

object ServiceImplScope : ClassOptionScope("ServiceImpl")

object ControllerScope : ClassOptionScope("Controller")

object MapperScope : InterfaceOptionScope("Mapper")

object ServiceScope : InterfaceOptionScope("Service")

