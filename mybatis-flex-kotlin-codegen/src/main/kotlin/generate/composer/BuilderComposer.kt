package com.mybatisflex.kotlin.codegen.generate.composer

import com.mybatisflex.kotlin.codegen.metadata.GenerationMetadata
import com.squareup.kotlinpoet.FileSpec

/**
 * 用于将 Type 和 Property 组合成文件
 */
fun interface BuilderComposer {
    companion object {
        @JvmField
        val Default = object : BuilderComposer {
            override fun transform(
                generationMetadata: GenerationMetadata,
                fileBuilder: FileSpec.Builder
            ): FileSpec.Builder = fileBuilder.apply {
                val properties = generationMetadata.properties
                val type = generationMetadata.type
                for (property in properties) {
                    type.addProperty(property.build())
                }
                addType(type.build())
            }

            override fun provideFileBuilder(
                generationMetadata: Sequence<GenerationMetadata>,
            ): Sequence<FileSpec.Builder> = generationMetadata.map {
                val generateOption = it.tableOptions
                val file = FileSpec.builder(
                    "${generateOption.basePackage}.${generateOption.optionName.replaceFirstChar(Char::lowercaseChar)}",
                    generateOption.typeName.replaceFirstChar(Char::uppercaseChar)
                )
                transform(it, file)
            }
        }
    }

    fun transform(generationMetadata: GenerationMetadata, fileBuilder: FileSpec.Builder): FileSpec.Builder

    fun provideFileBuilder(
        generationMetadata: Sequence<GenerationMetadata>,
    ): Sequence<FileSpec.Builder> = Default.provideFileBuilder(generationMetadata)
}