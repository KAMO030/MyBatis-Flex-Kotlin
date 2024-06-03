package com.mybatisflex.kotlin.codegen.metadata

import metadata.ColumnMetadata

data class TableMetadata(
    val schema: String?,
    val tableName: String,
    val comment: String? = null,
    val primaryKey: MutableSet<String> = mutableSetOf(),
) {
    private val _columns = mutableSetOf<ColumnMetadata>()

    val columns: Set<ColumnMetadata>
        get() = _columns

    operator fun plusAssign(column: ColumnMetadata) {
        if (column.name in primaryKey) {
            column.isPrimaryKey = true
        }
        _columns += column
    }
}

val TableMetadata.commonQuerySql: String
    get() {
        val from = if (schema.isNullOrBlank()) {
            tableName
        } else {
            "$schema.$tableName"
        }
        return "SELECT * FROM $from WHERE 1 = 2"
    }

val TableMetadata.mysqlQuerySql: String
    get() {
        val from = if (schema.isNullOrBlank()) {
            tableName
        } else {
            "`$schema`.`$tableName`"
        }
        return "SELECT * FROM $from WHERE 1 = 2"
    }
