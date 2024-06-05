package com.mybatisflex.kotlin.codegen.config

import com.mybatisflex.annotation.Column
import com.mybatisflex.annotation.Table
import com.mybatisflex.kotlin.codegen.annotation.GeneratorDsl
import com.mybatisflex.kotlin.codegen.generate.transformer.BuilderTransformer
import com.mybatisflex.kotlin.codegen.generate.transformer.PropertyTransformer
import com.mybatisflex.kotlin.codegen.generate.transformer.TypeTransformer
import com.mybatisflex.kotlin.codegen.metadata.TableMetadata
import com.squareup.kotlinpoet.*
import metadata.ColumnMetadata

@GeneratorDsl
fun GenerateOption.forTables(transform: (seq: Sequence<TableMetadata>) -> Sequence<TableMetadata>) {
    tableMetadataTransformer = {
        transform(tableMetadataTransformer())
    }
}

@GeneratorDsl
fun GenerateOption.forColumns(transform: (seq: Sequence<ColumnMetadata>) -> Sequence<ColumnMetadata>) {
    columnMetadataTransformer = {
        transform(columnMetadataTransformer())
    }
}

@GeneratorDsl
fun GenerateOption.applyTransformer(builderTransformer: BuilderTransformer) = builderTransformer.also {
    this.builderTransformer = this.builderTransformer then builderTransformer
}

@GeneratorDsl
fun GenerateOption.transformType(
    transformer: (
        tableMetadata: TableMetadata,
        builder: TypeSpec.Builder,
    ) -> Unit
) = applyTransformer(TypeTransformer(transformer))

@GeneratorDsl
fun GenerateOption.transformProperty(
    transformer: (
        columnMetadata: ColumnMetadata,
        builder: PropertySpec.Builder
    ) -> Unit
) = applyTransformer(PropertyTransformer(transformer))

@GeneratorDsl
fun GenerateOption.dataclass() = transformType { _, builder ->
    builder.addModifiers(KModifier.DATA)
}

@OptIn(DelicateKotlinPoetApi::class)
@GeneratorDsl
fun GenerateOption.tableAnnotation(table: Table? = null) = transformType { tableMetadata, builder ->
    val annotationSpec = if (table != null) {
        AnnotationSpec.get(table)
    } else {
        AnnotationSpec.builder(Table::class).addMember("%S", tableMetadata.tableName).build()
    }
    builder.addAnnotation(annotationSpec)
}

@GeneratorDsl
@OptIn(DelicateKotlinPoetApi::class)
fun GenerateOption.columnAnnotation(column: Column? = null) = transformProperty { columnMetadata, builder ->
    val annotationSpec = if (column != null) {
        AnnotationSpec.get(column)
    } else {
        AnnotationSpec.builder(Column::class).addMember("%S", columnMetadata.name).build()
    }
    builder.addAnnotation(annotationSpec)
}


