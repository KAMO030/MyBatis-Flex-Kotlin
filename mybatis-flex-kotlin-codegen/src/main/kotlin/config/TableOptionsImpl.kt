package com.mybatisflex.kotlin.codegen.config

import com.mybatisflex.kotlin.codegen.annotation.GeneratorDsl
import com.mybatisflex.kotlin.codegen.generate.transformer.BuilderTransformer
import com.mybatisflex.kotlin.codegen.internal.asCamelCase
import com.mybatisflex.kotlin.codegen.internal.asClassName
import com.mybatisflex.kotlin.codegen.metadata.ColumnMetadata
import com.mybatisflex.kotlin.codegen.metadata.GenerationMetadata
import com.mybatisflex.kotlin.codegen.metadata.TableMetadata
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeSpec.Builder
import com.squareup.kotlinpoet.TypeSpec.Kind

/**
 * 产物使用的配置。可以单独配置产物。
 */
@GeneratorDsl
open class TableOptionsImpl(
    override val optionName: String,
    override var rootSourceDir: String,
    override var packageName: String = optionName,
    override val kind: Kind = Kind.CLASS,
) : TableOptions {

    constructor(
        optionName: String,
        configuration: TableConfiguration,
        kind: Kind = Kind.CLASS,
    ) : this(
        optionName,
        configuration.rootSourceDir,
        "${configuration.basePackage}.${optionName.replaceFirstChar(Char::lowercaseChar)}",
        kind
    )

    // TODO: 支持转换器
    override var builderTransformer: BuilderTransformer = BuilderTransformer

    // 列名映射
    override var columnNameMapper: (ColumnMetadata) -> String = {
        it.name.asCamelCase()
    }

    override var propertySpecBuilder: (ColumnMetadata) -> PropertySpec.Builder = {
        PropertySpec.builder(columnNameMapper(it), it.propertyType.asTypeName())
    }

    override var columnMetadataTransformer: Sequence<ColumnMetadata>.() -> Sequence<ColumnMetadata> = { this }

    override var typeSpecComposer: (GenerationMetadata) -> TypeSpec = {
        val typeSpec = it.type
        val properties = it.properties
        properties.forEach { propBuilder ->
            typeSpec.propertySpecs += propBuilder.build()
        }
        typeSpec.build()
    }

    // 表名映射
    private var _tableNameMapper: ((TableMetadata) -> String)? = null

    override var tableNameMapper: (TableMetadata) -> String
        get() = (_tableNameMapper ?: {
            "${it.tableName.asClassName()}$optionName".also { res ->
                typeName = res
            }
        }).also { _tableNameMapper = it }
        set(value) {
            _tableNameMapper = {
                value(it).also { res -> typeName = res }
            }
        }

    override lateinit var typeName: String

    override var typeSpecBuilder: (TableMetadata) -> Builder = {
        builderByKind(kind, tableNameMapper(it))
    }

}