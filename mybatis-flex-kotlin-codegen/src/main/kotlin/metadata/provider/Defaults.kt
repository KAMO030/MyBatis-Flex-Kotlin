package com.mybatisflex.kotlin.codegen.metadata.provider

import com.mybatisflex.kotlin.codegen.metadata.*
import java.sql.ResultSet
import java.sql.ResultSetMetaData

open class DefaultMetadataProvider : MetadataProvider {
    companion object : DefaultMetadataProvider()

    open lateinit var dataSource: DataSourceMetadata

    override fun provideMetadata(dataSource: DataSourceMetadata): Set<TableMetadata> = buildSet {
        this@DefaultMetadataProvider.dataSource = dataSource
        dataSource.tablesResultSet.use {
            while (it.next()) {
                val metadata = TableMetadata(dataSource.schema, it.tableName, it.tableComment)
                metadata.initPrimaryKey()
                provideColumnMetadata(metadata).forEach { columnMetadata ->
                    metadata += columnMetadata
                }
                this += metadata
            }
        }
    }

    protected open fun provideColumnMetadata(tableMetadata: TableMetadata): Sequence<ColumnMetadata> = sequence {
        val commentMap = getColumnComment(tableMetadata)
        dataSource.connection.createStatement().use {
            it.executeQuery(tableMetadata.querySql).use { rs ->
                val column = rs.metaData
                for (i in 1..column.columnCount) {
                    val res = buildColumnMetadata(tableMetadata, column, i, commentMap)
                    yield(res)
                }
            }
        }
    }

    protected open fun buildColumnMetadata(
        table: TableMetadata,
        column: ResultSetMetaData,
        columnIndex: Int,
        commentMap: Map<String, String> = emptyMap()
    ): ColumnMetadata = ColumnMetadata(
        name = column.getColumnName(columnIndex),
        propertyType = PropertyType.of(column.getColumnClassName(columnIndex)),
        rawType = column.getColumnClassName(columnIndex),
        table = table,
        comment = commentMap[column.getColumnName(columnIndex)],
        nullable = column.isNullable(columnIndex) == 1,
        autoIncrement = column.isAutoIncrement(columnIndex),
        rawLength = column.getColumnDisplaySize(columnIndex)
    )

    protected open fun getColumnComment(tableMetadata: TableMetadata): Map<String, String> = buildMap {
        with(dataSource) {
            databaseMetaData.getColumns(connection.catalog, schema, tableMetadata.tableName, null)
        }.use {
            while (it.next()) {
                this[it.getString("COLUMN_NAME")] = it.getString("REMARKS").orEmpty() // 可能没有注释
            }
        }
    }

    protected open val TableMetadata.querySql: String
        get() = commonQuerySql

    protected open val ResultSet.tableName: String
        get() = getString("TABLE_NAME")

    protected open val ResultSet.tableComment: String
        // 可能没有注释
        get() = getString("REMARKS").orEmpty()

    protected open fun TableMetadata.initPrimaryKey() {
        with(dataSource) {
            databaseMetaData.getPrimaryKeys(connection.catalog, schema, tableName)
        }.use {
            while (it.next()) {
                _primaryKey += it.getString("COLUMN_NAME")
            }
        }
    }
}