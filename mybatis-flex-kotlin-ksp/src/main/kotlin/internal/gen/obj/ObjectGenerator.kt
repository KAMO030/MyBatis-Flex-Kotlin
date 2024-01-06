package com.mybatisflex.kotlin.ksp.internal.gen.obj

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.mybatisflex.kotlin.ksp.internal.gen.TableDefGenerator
import com.mybatisflex.kotlin.ksp.internal.util.*
import com.mybatisflex.kotlin.ksp.kotlinVersion
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.jvm.jvmName
import com.squareup.kotlinpoet.jvm.jvmStatic

internal class ObjectGenerator : TableDefGenerator {
    private val _instancePropertySpecs = ArrayList<PropertySpec>()

    override val instancePropertySpecs: List<PropertySpec>
        get() = _instancePropertySpecs

    override val KSClassDeclaration.typeSpec: TypeSpec
        get() = TypeSpec.objectBuilder(tableClassName).apply {
            if (kotlinVersion.isAtLeast(1, 9)) {
                addModifiers(KModifier.DATA)  // 如果语言版本至少为 1.9 ，则改为生成 data object 。
            }
            val tableClassName = tableClassName
            val generateClassName = ClassName("${packageName.asString()}.table", tableClassName)  // 将要生成的类的ClassName
            val allProperties = legalProperties
            val builtPropertySpec = generateProperties(allProperties)
            val instanceProperty = instanceProperty(generateClassName).build()
            _instancePropertySpecs += instanceProperty

            addProperties(builtPropertySpec)
            addProperty(allColumnsBuilder.build())
            addProperty(getDefaultColumns(allProperties).build())
            addProperty(instanceProperty)

            // 添加 KDoc 注释，标明此类是 mybatis-flex-kotlin-ksp 生成的。
            addKdoc(
                """
                This file is automatically generated by the ksp of mybatis-flex, do not modify this file.
                """.trimIndent()
            )

            // 继承父类并传入 scheme 和 tableName 以调用父构造器
            superclass(TABLE_DEF)
            addSuperclassConstructorParameter(
                """
                "$scheme", "$tableName"
                """.trimIndent()
            )

            // 添加 invoke 方法用来模拟构造器。它应当仅在极特殊的情况下使用。
            addFunction(
                FunSpec.builder("invoke")
                    .addModifiers(KModifier.OPERATOR)
                    .returns(
                        returnType = generateClassName,
                        kdoc = CodeBlock.of(
                            """
                            每次调用将返回一个新的实例，用于在极特殊的情况下模拟构造器以构造实例。
                            
                            若你确实有构造新实例的需求，你应该在 mybatis-flex.config 文件中，做出如下配置：
                            
                            ksp.generate.type=class
                            
                            配置后 KSP 将改为生成拥有公共无参构造器的 class 而不是直接使用 object 。
                            """.trimIndent()
                        )
                    )
                    .addCode(
                        """
                            return javaClass.getDeclaredConstructor().newInstance()
                        """.trimIndent()
                    )
                    .addAnnotation(
                        AnnotationSpec.builder(Deprecated::class)
                            .addMember(
                                """
                                |"We don't recommend calling this function directly." + 
                                |"See KDoc for this function for details."
                                """.trimMargin()
                            )
                            .build()
                    )
                    .jvmName("newInstance")
                    .jvmStatic()
                    .build()
            )
        }.build()

    private fun generateProperties(sequence: Sequence<KSPropertyDeclaration>): List<PropertySpec> = sequence.map {
        it.getPropertySpecBuilder().build()
    }.toList()

}