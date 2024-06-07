package com.mybatisflex.kotlin.codegen.metadata

data class TableMetadata(
    val schema: String?,
    val tableName: String,
    val comment: String? = null,
) {
    internal val _primaryKey: MutableSet<String> = mutableSetOf()

    val primaryKey: Set<String>
        get() = _primaryKey

    private val _columns = mutableSetOf<ColumnMetadata>()

    val columns: Set<ColumnMetadata>
        get() = _columns

    operator fun plusAssign(column: ColumnMetadata) {
        if (column.name in _primaryKey) {
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
