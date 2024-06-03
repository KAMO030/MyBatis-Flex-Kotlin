package com.mybatisflex.kotlin.codegen.metadata.provider

import com.mybatisflex.kotlin.codegen.metadata.DataSourceMetadata
import com.mybatisflex.kotlin.codegen.metadata.TableMetadata

fun interface MetadataProvider {
    fun provideMetadata(dataSource: DataSourceMetadata): Set<TableMetadata>
}

