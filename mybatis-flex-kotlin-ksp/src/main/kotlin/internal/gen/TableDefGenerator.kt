package com.mybatisflex.kotlin.ksp.internal.gen

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.mybatisflex.kotlin.ksp.internal.util.instanceProperty
import com.mybatisflex.kotlin.ksp.internal.util.suppressDefault
import com.mybatisflex.kotlin.ksp.internal.util.tableClassName
import com.mybatisflex.kotlin.ksp.internal.util.write
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

interface TableDefGenerator {

    /**
     * 生成代码。
     *
     * @param classDeclaration 类声明
     * @author CloudPlayer
     */
    fun generate(classDeclaration: KSClassDeclaration) {
        val tableClassName = classDeclaration.tableClassName
        val fileSpec = FileSpec.builder(
            "${classDeclaration.packageName.asString()}.table",
            tableClassName
        )
        val typeSpec: TypeSpec = classDeclaration.typeSpec
        fileSpec.addType(typeSpec)
        fileSpec
            .suppressDefault()
            .build()
            .write()
    }

    /**
     * 通过 KSP 中的类声明构建的 kotlinpoet 的类声明。
     *
     * @receiver KSP 的类声明，即实体类。
     * @return kotlinpoet 的类声明。
     */
    val KSClassDeclaration.typeSpec: TypeSpec

    /**
     * 类本身的实例属性。每次调用 [KSClassDeclaration.typeSpec] 后，都应当更新 List 中的内容，以指示生成的类对应的实例属性以便 Tables 类进行生成。
     *
     * @return 实例属性。
     * @see KSClassDeclaration.instanceProperty
     * @author CloudPlayer
     */
    val instancePropertySpecs: List<PropertySpec>
}