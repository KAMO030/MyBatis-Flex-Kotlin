package com.mybatisflex.kotlin.codegen.generate.transformer

import com.mybatisflex.kotlin.codegen.metadata.ColumnMetadata
import com.mybatisflex.kotlin.codegen.metadata.TableMetadata
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

class TypeTransformer(
    private val block: (
        tableMetadata: TableMetadata,
        builder: TypeSpec.Builder
    ) -> Unit
) : BuilderTransformer {
    override fun transformType(
        tableMetadata: TableMetadata,
        builder: TypeSpec.Builder
    ): TypeSpec.Builder = builder.also { block(tableMetadata, builder) }

    override fun transformProperty(
        columnMetadata: ColumnMetadata,
        builder: PropertySpec.Builder
    ): PropertySpec.Builder = builder
}