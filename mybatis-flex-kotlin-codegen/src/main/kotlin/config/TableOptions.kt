package com.mybatisflex.kotlin.codegen.config

import com.mybatisflex.kotlin.codegen.annotation.GeneratorDsl
import com.mybatisflex.kotlin.codegen.generate.transformer.BuilderTransformer
import com.mybatisflex.kotlin.codegen.internal.asCamelCase
import com.mybatisflex.kotlin.codegen.internal.asClassName
import com.mybatisflex.kotlin.codegen.metadata.ColumnMetadata
import com.mybatisflex.kotlin.codegen.metadata.TableMetadata
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

/**
 * 产物使用的配置。可以单独配置产物。
 */
@GeneratorDsl
open class TableOptions(
    val optionName: String,
    var rootSourceDir: String,
    var basePackage: String = "",
) {
    // TODO: 支持转换器
    var builderTransformer: BuilderTransformer = BuilderTransformer

    // 表名映射
    var tableNameMapper: (TableMetadata) -> String = {
        "${it.tableName.asClassName()}$optionName".also { res ->
            typeName = res
        }
    }
        set(value) {
            field = {
                value(it).also { res -> typeName = res }
            }
        }

    internal lateinit var typeName: String

    // 列名映射
    var columnNameMapper: (ColumnMetadata) -> String = {
        it.name.asCamelCase()
    }

    var typeSpecBuilder: (TableMetadata) -> TypeSpec.Builder = { TypeSpec.classBuilder(tableNameMapper(it)) }

    var propertySpecBuilder: (ColumnMetadata) -> PropertySpec.Builder =
        { PropertySpec.builder(it.name, it.propertyType.asTypeName()) }

    @PublishedApi
    internal var tableMetadataTransformer: Sequence<TableMetadata>.() -> Sequence<TableMetadata> = { this }

    @PublishedApi
    internal var columnMetadataTransformer: Sequence<ColumnMetadata>.() -> Sequence<ColumnMetadata> = { this }
}