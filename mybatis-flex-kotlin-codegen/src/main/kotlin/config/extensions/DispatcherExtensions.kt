package com.mybatisflex.kotlin.codegen.config.extensions

import com.mybatisflex.kotlin.codegen.annotation.GeneratorDsl
import com.mybatisflex.kotlin.codegen.config.GenerateDispatcher
import com.mybatisflex.kotlin.codegen.config.TableConfiguration
import com.mybatisflex.kotlin.codegen.metadata.TableMetadata

@GeneratorDsl
fun GenerateDispatcher.useTestSource() {
    rootSourceDir = "${System.getProperty("user.dir")}/src/test/kotlin"
}
@GeneratorDsl
inline fun GenerateDispatcher.withTables(
    table: String,
    vararg tables: String,
    tableConfigBlock: TableConfiguration.() -> Unit
) {
    val tableConfiguration = TableConfiguration().apply(tableConfigBlock)
    specificConfiguration[table] = tableConfiguration
    tables.forEach {
        specificConfiguration[it] = tableConfiguration
    }
}

@GeneratorDsl
inline fun GenerateDispatcher.withAllTable(
    tableConfigBlock: TableConfiguration.() -> Unit
) {
    globalTableConfiguration.apply(tableConfigBlock)
    generateForAll = true
}

@GeneratorDsl
inline fun GenerateDispatcher.transformMetadata(
    crossinline sequenceTransformer: Sequence<TableMetadata>.() -> Sequence<TableMetadata>
) {
    val prev = metadataTransformer
    metadataTransformer = {
        prev().sequenceTransformer()
    }
}

@GeneratorDsl
fun GenerateDispatcher.exclude(vararg tables: String) {
    excluded += tables
}

@GeneratorDsl
fun GenerateDispatcher.useDefaultConfigForAllTable() {
    withAllTable {
        generateDefault()
    }
}