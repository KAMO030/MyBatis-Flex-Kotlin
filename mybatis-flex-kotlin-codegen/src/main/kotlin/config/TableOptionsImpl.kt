package com.mybatisflex.kotlin.codegen.config

import com.mybatisflex.kotlin.codegen.annotation.GeneratorDsl
import com.mybatisflex.kotlin.codegen.generate.transformer.BuilderTransformer
import com.mybatisflex.kotlin.codegen.internal.asCamelCase
import com.mybatisflex.kotlin.codegen.internal.asClassName
import com.mybatisflex.kotlin.codegen.metadata.ColumnMetadata
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
    override var basePackage: String = "",
    val kind: Kind = Kind.CLASS,
    // TODO: 支持转换器
    override var builderTransformer: BuilderTransformer = BuilderTransformer,
    // 列名映射
    override var columnNameMapper: (ColumnMetadata) -> String = {
        it.name.asCamelCase()
    },
    override var propertySpecBuilder: (ColumnMetadata) -> PropertySpec.Builder = {
        PropertySpec.builder(it.name, it.propertyType.asTypeName())
    },
    override var tableMetadataTransformer: Sequence<TableMetadata>.() -> Sequence<TableMetadata> = { this },
    override var columnMetadataTransformer: Sequence<ColumnMetadata>.() -> Sequence<ColumnMetadata> = { this },
) : TableOptions {
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
        when (kind) {
            Kind.CLASS -> TypeSpec.classBuilder(tableNameMapper(it))
            Kind.INTERFACE -> TypeSpec.interfaceBuilder(tableNameMapper(it))
            Kind.OBJECT -> TypeSpec.objectBuilder(tableNameMapper(it))
        }
        }

}