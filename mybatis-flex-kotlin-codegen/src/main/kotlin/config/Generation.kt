package com.mybatisflex.kotlin.codegen.config

import com.mybatisflex.kotlin.codegen.annotation.GeneratorDsl
import com.mybatisflex.kotlin.codegen.config.extensions.useDefaultConfigForAllTable
import com.mybatisflex.kotlin.codegen.generate.composer.BuilderComposer
import com.mybatisflex.kotlin.codegen.internal.configuration
import com.mybatisflex.kotlin.codegen.metadata.DataSourceMetadata
import com.mybatisflex.kotlin.codegen.metadata.GenerationMetadata
import com.mybatisflex.kotlin.codegen.metadata.TableMetadata
import com.mybatisflex.kotlin.codegen.metadata.provider.DefaultMetadataProvider
import com.mybatisflex.kotlin.codegen.metadata.provider.MetadataProvider
import javax.sql.DataSource

/**
 * 生成调度器。
 */
@GeneratorDsl
object GenerateDispatcher {
    var metadataProvider: MetadataProvider = DefaultMetadataProvider

    var rootSourceDir: String = "${System.getProperty("user.dir")}\\src\\main\\kotlin"

    var basePackage: String = ""

    var builderComposer: BuilderComposer = BuilderComposer.Default

    @PublishedApi
    internal val specificConfiguration = mutableMapOf<String, TableConfiguration>()

    @PublishedApi
    internal val excluded: MutableSet<String> = mutableSetOf()

    @PublishedApi
    internal var metadataTransformer: Sequence<TableMetadata>.() -> Sequence<TableMetadata> = {
        filterNot { it.tableName in excluded }.run {
            if (generateForAll) this
            else filter {
                it.configuration !== globalTableConfiguration
            }
        }
    }

    @PublishedApi
    internal val globalTableConfiguration: TableConfiguration = TableConfiguration()

    @PublishedApi
    internal var generateForAll: Boolean = false

    @PublishedApi
    // TODO: 清除缓存后，正在运行的会失去 specificOption 中的内容
    internal fun clearCache() {
        specificConfiguration.clear()
        excluded.clear()
        globalTableConfiguration.clearCache()
    }
}

@GeneratorDsl
inline fun generate(dataSource: DataSource, schema: String? = null, block: GenerateDispatcher.() -> Unit) =
    with(GenerateDispatcher) {
        block()
        val tables = metadataProvider.provideMetadata(DataSourceMetadata(dataSource, schema))
            .asSequence().metadataTransformer()
        val tableOptions = tables.flatMap { it.configuration.optionsMap.values }
        val types = tables.flatMap {
            val configuration = it.configuration
            configuration.optionsMap.values.map { tableOptions ->
                val builderTransformer = tableOptions.builderTransformer
                builderTransformer.transformType(it, tableOptions.typeSpecBuilder(it))
            }
        }
        val res = tables.flatMap {
            val configuration = it.configuration
            configuration.optionsMap.values.map { tableOptions ->
                val builderTransformer = tableOptions.builderTransformer
                it.columns.asSequence().map { column ->
                    builderTransformer.transformProperty(column, tableOptions.propertySpecBuilder(column))
                }
            }
        }
        val metadataSequence = sequence {
            val optionIterator = tableOptions.iterator()
            val typeIterator = types.iterator()
            val propertyIterator = res.iterator()
            while (optionIterator.hasNext() && typeIterator.hasNext() && propertyIterator.hasNext()) {
                val op = optionIterator.next()
                val type = typeIterator.next()
                val prop = propertyIterator.next()
                yield(GenerationMetadata(op, type, prop))
            }
        }
        builderComposer.provideFileBuilder(metadataSequence)
    }

@GeneratorDsl
fun generate(dataSource: DataSource, schema: String? = null) =
    generate(dataSource, schema) {
        useDefaultConfigForAllTable()
    }


