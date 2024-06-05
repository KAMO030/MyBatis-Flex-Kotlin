package com.mybatisflex.kotlin.codegen.metadata

import com.mybatisflex.kotlin.codegen.config.GenerateOption
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

data class GenerationMetadata(
    val generateOption: GenerateOption,
    val type: TypeSpec.Builder,
    val properties: Sequence<PropertySpec.Builder>,
)
