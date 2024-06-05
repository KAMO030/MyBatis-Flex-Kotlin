package com.mybatisflex.kotlin.codegen.generate.transformer

import com.mybatisflex.kotlin.codegen.metadata.TableMetadata
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import metadata.ColumnMetadata

class TransformerCombiner(
    private val inner: BuilderTransformer,
    private val outer: BuilderTransformer,
) : BuilderTransformer {
    override fun transformProperty(
        columnMetadata: ColumnMetadata,
        builder: PropertySpec.Builder
    ): PropertySpec.Builder = outer.transformProperty(
        columnMetadata, inner.transformProperty(columnMetadata, builder)
    )


    override fun transformType(
        tableMetadata: TableMetadata,
        builder: TypeSpec.Builder
    ): TypeSpec.Builder =
        outer.transformType(tableMetadata, inner.transformType(tableMetadata, builder))
}
