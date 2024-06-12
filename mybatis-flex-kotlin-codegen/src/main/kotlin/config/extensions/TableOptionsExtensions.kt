package com.mybatisflex.kotlin.codegen.config.extensions

import com.mybatisflex.kotlin.codegen.annotation.GeneratorDsl
import com.mybatisflex.kotlin.codegen.config.TableOptions
import com.mybatisflex.kotlin.codegen.generate.transformer.BuilderTransformer
import com.mybatisflex.kotlin.codegen.generate.transformer.PropertyTransformer
import com.mybatisflex.kotlin.codegen.generate.transformer.TypeTransformer
import com.mybatisflex.kotlin.codegen.metadata.ColumnMetadata
import com.mybatisflex.kotlin.codegen.metadata.TableMetadata
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

@GeneratorDsl
fun TableOptions.forColumns(transform: (seq: Sequence<ColumnMetadata>) -> Sequence<ColumnMetadata>) {
    columnMetadataTransformer = {
        transform(columnMetadataTransformer())
    }
}

@GeneratorDsl
fun TableOptions.applyTransformer(builderTransformer: BuilderTransformer) = builderTransformer.also {
    this.builderTransformer = this.builderTransformer then builderTransformer
}

@GeneratorDsl
fun TableOptions.transformType(
    transformer: (
        tableMetadata: TableMetadata,
        builder: TypeSpec.Builder,
    ) -> Unit
) = applyTransformer(TypeTransformer(transformer))

@GeneratorDsl
fun TableOptions.transformProperty(
    transformer: (
        columnMetadata: ColumnMetadata,
        builder: PropertySpec.Builder
    ) -> Unit
) = applyTransformer(PropertyTransformer(transformer))





