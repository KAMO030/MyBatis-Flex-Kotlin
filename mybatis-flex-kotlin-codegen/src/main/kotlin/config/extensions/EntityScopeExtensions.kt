package com.mybatisflex.kotlin.codegen.config.extensions

import com.mybatisflex.annotation.Column
import com.mybatisflex.annotation.Table
import com.mybatisflex.kotlin.codegen.annotation.GeneratorDsl
import com.mybatisflex.kotlin.codegen.config.EntityScope
import com.mybatisflex.kotlin.codegen.config.ScopedTableOptions
import com.mybatisflex.kotlin.codegen.config.TableConfiguration
import com.mybatisflex.kotlin.codegen.internal.asClassName
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.DelicateKotlinPoetApi


inline fun ScopedTableOptions<EntityScope>.default() {
    dataclass()
    tableAnnotation()
    columnAnnotation()
}

@PublishedApi
internal inline fun TableConfiguration.dispatcher(
    crossinline configure: ScopedTableOptions<EntityScope>.() -> Unit
): ScopedTableOptions<EntityScope>.() -> Unit = {
    tableNameMapper = { it.tableName.asClassName() }
    configure()
}

@OptIn(DelicateKotlinPoetApi::class)
@GeneratorDsl
fun ScopedTableOptions<EntityScope>.tableAnnotation(table: Table? = null) = transformType { tableMetadata, builder ->
    val annotationSpec = if (table != null) {
        AnnotationSpec.get(table)
    } else {
        AnnotationSpec.builder(Table::class).addMember("%S", tableMetadata.tableName).build()
    }
    builder.addAnnotation(annotationSpec)
}

@GeneratorDsl
@OptIn(DelicateKotlinPoetApi::class)
fun ScopedTableOptions<EntityScope>.columnAnnotation(column: Column? = null) =
    transformProperty { columnMetadata, builder ->
        val annotationSpec = if (column != null) {
            AnnotationSpec.get(column)
        } else {
            AnnotationSpec.builder(Column::class).addMember("%S", columnMetadata.name).build()
        }
        builder.addAnnotation(annotationSpec)
    }