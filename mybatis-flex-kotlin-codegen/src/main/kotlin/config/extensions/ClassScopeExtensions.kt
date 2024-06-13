package com.mybatisflex.kotlin.codegen.config.extensions

import com.mybatisflex.kotlin.codegen.annotation.GeneratorDsl
import com.mybatisflex.kotlin.codegen.config.ClassOptionScope
import com.mybatisflex.kotlin.codegen.config.ScopedTableOptions
import com.mybatisflex.kotlin.codegen.internal.VOID
import com.mybatisflex.kotlin.codegen.internal.replaceNothingToTypeName
import com.mybatisflex.kotlin.codegen.internal.replaceTypeName
import com.squareup.kotlinpoet.*
import kotlin.reflect.typeOf

@GeneratorDsl
var ScopedTableOptions<ClassOptionScope>.dataclass: Boolean
    get() = transformerCallBacks.contains("dataclass")
    set(value) {
        if (value.not()) {
            transformerCallBacks.remove("dataclass")
        } else {
            transformerCallBacks["dataclass"] = {
                transformProperty { columnMetadata, builder ->
                    builder.initializer(columnNameMapper(columnMetadata))
                }
                transformType { _, builder ->
                    builder.modifiers += KModifier.DATA
                }
                val prev = typeSpecComposer
                typeSpecComposer = { generation ->
                    val typeSpec = generation.type
                    val properties = generation.properties
                    val constructorBuilder = FunSpec.constructorBuilder()
                    properties.map {
                        it.build()
                    }.map {
                        ParameterSpec.builder(it.name, it.type).build()
                    }.forEach {
                        constructorBuilder.parameters += it
                    }
                    typeSpec.primaryConstructor(constructorBuilder.build())
                    prev(generation)
                }
            }
        }
    }

@GeneratorDsl
inline fun <reified T : Any> ScopedTableOptions<ClassOptionScope>.superclass(replaceNothing: Boolean = false) =
    registerTransformerCallBacks("superclass") {
        transformType { _, builder ->
            if (replaceNothing) {
                builder.superclass(typeOf<T>().replaceNothingToTypeName(typeName))
            } else {
                builder.superclass(typeNameOf<T>().replaceTypeName(VOID, NOTHING))
            }
        }
    }
