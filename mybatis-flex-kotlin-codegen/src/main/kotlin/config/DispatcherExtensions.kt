package com.mybatisflex.kotlin.codegen.config

import com.mybatisflex.kotlin.codegen.annotation.GeneratorDsl
import com.mybatisflex.kotlin.codegen.metadata.TableMetadata

@GeneratorDsl
fun GenerateDispatcher.useTestSource() {
    rootSourceDir = "${System.getProperty("user.dir")}/src/test/kotlin"
}
@GeneratorDsl
inline fun GenerateDispatcher.withTables(table: String, vararg tables: String, tableOptionBlock: TableOption.() -> Unit) {
    val option = TableOption().apply(tableOptionBlock)
    specificOption[table] = option
    tables.forEach {
        specificOption[it] = option
    }
}

@GeneratorDsl
inline fun GenerateDispatcher.withAllTable(tableOptionBlock: TableOption.() -> Unit) {
    globalOption.apply(tableOptionBlock)
    generateForAll = true
}

@GeneratorDsl
inline fun GenerateDispatcher.transformMetadata(crossinline sequenceTransformer: Sequence<TableMetadata>.() -> Sequence<TableMetadata>) {
    metadataTransformer = {
        metadataTransformer().sequenceTransformer()
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