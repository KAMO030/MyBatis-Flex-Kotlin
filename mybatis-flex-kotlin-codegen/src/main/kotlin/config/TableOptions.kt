package com.mybatisflex.kotlin.codegen.config

import com.mybatisflex.kotlin.codegen.generate.transformer.BuilderTransformer
import com.mybatisflex.kotlin.codegen.metadata.ColumnMetadata
import com.mybatisflex.kotlin.codegen.metadata.GenerationMetadata
import com.mybatisflex.kotlin.codegen.metadata.TableMetadata
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

interface TableOptions {
    val optionName: String
    var rootSourceDir: String
    var packageName: String
    var builderTransformer: BuilderTransformer
    var typeName: String
    // 表名映射
    var tableNameMapper: (TableMetadata) -> String
    var columnNameMapper: (ColumnMetadata) -> String

    var typeSpecBuilder: (TableMetadata) -> TypeSpec.Builder
    var propertySpecBuilder: (ColumnMetadata) -> PropertySpec.Builder

    var columnMetadataTransformer: Sequence<ColumnMetadata>.() -> Sequence<ColumnMetadata>
    var typeSpecComposer: (GenerationMetadata) -> TypeSpec
}