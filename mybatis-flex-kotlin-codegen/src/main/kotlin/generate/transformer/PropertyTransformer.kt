package com.mybatisflex.kotlin.codegen.generate.transformer

import com.mybatisflex.kotlin.codegen.metadata.TableMetadata
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import metadata.ColumnMetadata

class PropertyTransformer(
    private val block: (
        columnMetadata: ColumnMetadata,
        builder: PropertySpec.Builder
    ) -> Unit
) : BuilderTransformer {
    override fun transformProperty(
        columnMetadata: ColumnMetadata,
        builder: PropertySpec.Builder
    ): PropertySpec.Builder = builder.also { block(columnMetadata, builder) }

    override fun transformType(
        tableMetadata: TableMetadata,
        builder: TypeSpec.Builder
    ): TypeSpec.Builder = builder
}