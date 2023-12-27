package com.mybatisflex.kotlin.ksp.internal.gen

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.mybatisflex.kotlin.ksp.internal.util.instanceProperty
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

interface TableDefGenerator {

    /**
     * 生成代码。
     *
     * @param classDeclaration 类声明
     * @return 生成的代码
     * @author CloudPlayer
     */
    fun generate(classDeclaration: KSClassDeclaration)

    /**
     * 通过 KSP 中的类声明构建的 kotlinpoet 的类声明。
     *
     * @receiver KSP 的类声明，即实体类。
     * @return kotlinpoet 的类声明。
     */
    val KSClassDeclaration.typeSpec: TypeSpec

    /**
     * 类本身的实例属性。
     *
     * @return 实例属性。
     * @see KSClassDeclaration.instanceProperty
     * @author CloudPlayer
     */
    val instancePropertySpecs: List<PropertySpec>
}