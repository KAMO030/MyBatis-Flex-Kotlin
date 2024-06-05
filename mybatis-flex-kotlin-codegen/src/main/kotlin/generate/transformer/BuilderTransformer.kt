package com.mybatisflex.kotlin.codegen.generate.transformer

import com.mybatisflex.kotlin.codegen.metadata.TableMetadata
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import metadata.ColumnMetadata

/**
 * Builder 提供者，可以实现并注册 Provider 以实现自定义逻辑。
 */
interface BuilderTransformer {
    companion object : BuilderTransformer {
        override fun transformType(
            tableMetadata: TableMetadata,
            builder: TypeSpec.Builder
        ): TypeSpec.Builder = builder

        override fun transformProperty(
            columnMetadata: ColumnMetadata,
            builder: PropertySpec.Builder
        ): PropertySpec.Builder = builder
    }

    fun transformProperty(
        columnMetadata: ColumnMetadata,
        builder: PropertySpec.Builder,
    ): PropertySpec.Builder

    fun transformType(
        tableMetadata: TableMetadata,
        builder: TypeSpec.Builder,
    ): TypeSpec.Builder

    infix fun then(next: BuilderTransformer): BuilderTransformer =
        if (next === BuilderTransformer) this else TransformerCombiner(this, next)
}