package com.mybatisflex.kotlin.codegen.metadata

import com.mybatisflex.kotlin.codegen.config.TableOptions
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

data class GenerationMetadata(
    val tableOptions: TableOptions,
    val type: TypeSpec.Builder,
    val properties: Sequence<PropertySpec.Builder>,
)
