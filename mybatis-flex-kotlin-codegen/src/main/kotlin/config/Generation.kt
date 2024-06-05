package com.mybatisflex.kotlin.codegen.config

import com.mybatisflex.kotlin.codegen.annotation.GeneratorDsl
import com.mybatisflex.kotlin.codegen.generate.composer.BuilderComposer
import com.mybatisflex.kotlin.codegen.generate.transformer.BuilderTransformer
import com.mybatisflex.kotlin.codegen.internal.asCamelCase
import com.mybatisflex.kotlin.codegen.internal.asClassName
import com.mybatisflex.kotlin.codegen.internal.option
import com.mybatisflex.kotlin.codegen.metadata.DataSourceMetadata
import com.mybatisflex.kotlin.codegen.metadata.GenerationMetadata
import com.mybatisflex.kotlin.codegen.metadata.TableMetadata
import com.mybatisflex.kotlin.codegen.metadata.provider.DefaultMetadataProvider
import com.mybatisflex.kotlin.codegen.metadata.provider.MetadataProvider
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import metadata.ColumnMetadata
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
    internal val specificOption = mutableMapOf<String, TableOption>()

    @PublishedApi
    internal val excluded: MutableSet<String> = mutableSetOf()

    @PublishedApi
    internal var metadataTransformer: Sequence<TableMetadata>.() -> Sequence<TableMetadata> = {
        filterNot { it.tableName in excluded }.run {
            if (generateForAll) this
            else filter {
                specificOption[it.tableName] != null
            }
        }
    }

    @PublishedApi
    internal val globalOption: TableOption = TableOption()

    @PublishedApi
    internal var generateForAll: Boolean = false

    @PublishedApi
    // TODO: 清除缓存后，正在运行的会失去 specificOption 中的内容
    internal fun clearCache() {
        specificOption.clear()
        excluded.clear()
        globalOption.clearCache()
    }
}

/**
 * 指示一个表生成什么东西。其下每一个 [GenerateOption] 都表示一个产物，并且可以独立配置。
 */
@GeneratorDsl
class TableOption {
    @PublishedApi
    internal val options: MutableMap<String, GenerateOption> = mutableMapOf()

    var rootSourceDir: String = GenerateDispatcher.rootSourceDir

    var basePackage: String = GenerateDispatcher.basePackage

    inline fun registerOption(
        optionName: String,
        option: GenerateOption = GenerateOption(optionName, rootSourceDir),
        initOption: GenerateOption.() -> Unit = {}
    ) {
        this.options[optionName] = option.apply(initOption)
    }

    @JvmName("registerOptionWithType")
    inline fun <T : GenerateOption> registerOption(
        option: T,
        optionName: String = option.optionName,
        initOption: T.() -> Unit = {}
    ) {
        this.options[optionName] = option.apply(initOption)
    }

    fun clearCache() {
        options.clear()
    }
}

inline fun TableOption.getOrRegister(
    optionName: String,
    register: (String) -> GenerateOption = { GenerateOption(it, rootSourceDir) }
): GenerateOption = options.getOrPut(optionName) { register(optionName) }

/**
 * 产物使用的配置。可以单独配置产物。
 */
@GeneratorDsl
open class GenerateOption(
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

@GeneratorDsl
inline fun generate(dataSource: DataSource, schema: String? = null, block: GenerateDispatcher.() -> Unit) =
    with(GenerateDispatcher) {
        block()
        val tables = metadataProvider.provideMetadata(DataSourceMetadata(dataSource, schema))
            .asSequence().metadataTransformer()
        val generateOptions = tables.flatMap { it.option.options.values }
        val types = tables.flatMap {
            val option = it.option
            option.options.values.map { generateOption ->
                val builderTransformer = generateOption.builderTransformer
                builderTransformer.transformType(it, generateOption.typeSpecBuilder(it))
            }
        }
        val res = tables.flatMap {
            val option = it.option
            option.options.values.map { generateOption ->
                val builderTransformer = generateOption.builderTransformer
                it.columns.asSequence().map { column ->
                    builderTransformer.transformProperty(column, generateOption.propertySpecBuilder(column))
                }
            }
        }
        val metadataSequence = sequence {
            val optionIterator = generateOptions.iterator()
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


