package com.mybatisflex.kotlin.codegen.generate.composer

import com.mybatisflex.kotlin.codegen.config.TableOptions
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec

/**
 * 用于将 Type 和 Property 组合成文件
 */
fun interface BuilderComposer {
    companion object {
        @JvmField
        val Default = object : BuilderComposer {
            override fun transform(
                options: TableOptions,
                file: FileSpec.Builder
            ): FileSpec.Builder = file

            override fun provideFileBuilder(
                files: Sequence<Pair<TableOptions, TypeSpec>>,
            ): Sequence<FileSpec.Builder> = files.map { (options, typeSpec) ->
                val file = FileSpec.builder(
                    options.packageName,
                    options.typeName.replaceFirstChar(Char::uppercaseChar)
                )
                file.addType(typeSpec)
                transform(options, file)
            }
        }
    }

    fun transform(options: TableOptions, file: FileSpec.Builder): FileSpec.Builder

    fun provideFileBuilder(
        files: Sequence<Pair<TableOptions, TypeSpec>>
    ): Sequence<FileSpec.Builder> = Default.provideFileBuilder(files)
}