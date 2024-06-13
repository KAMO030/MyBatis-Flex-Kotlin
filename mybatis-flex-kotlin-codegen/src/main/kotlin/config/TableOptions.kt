package com.mybatisflex.kotlin.codegen.config

import com.mybatisflex.kotlin.codegen.generate.transformer.BuilderTransformer
import com.mybatisflex.kotlin.codegen.metadata.ColumnMetadata
import com.mybatisflex.kotlin.codegen.metadata.GenerationMetadata
import com.mybatisflex.kotlin.codegen.metadata.TableMetadata
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeSpec.Builder
import com.squareup.kotlinpoet.TypeSpec.Kind

interface TableOptions {
    val optionName: String
    var rootSourceDir: String
    var packageName: String
    var builderTransformer: BuilderTransformer
    var typeName: String
    val kind: Kind
    // 表名映射
    var tableNameMapper: (TableMetadata) -> String
    var columnNameMapper: (ColumnMetadata) -> String

    var typeSpecBuilder: (TableMetadata) -> Builder
    var propertySpecBuilder: (ColumnMetadata) -> PropertySpec.Builder

    var columnMetadataTransformer: Sequence<ColumnMetadata>.() -> Sequence<ColumnMetadata>
    var typeSpecComposer: (GenerationMetadata) -> TypeSpec
}

internal fun builderByKind(
    kind: Kind,
    name: String,
): Builder = when (kind) {
    Kind.CLASS -> TypeSpec.classBuilder(name)
    Kind.OBJECT -> TypeSpec.objectBuilder(name)
    Kind.INTERFACE -> TypeSpec.interfaceBuilder(name)
}