package com.mybatisflex.kotlin.codegen.metadata.provider

import com.mybatisflex.kotlin.codegen.metadata.DataSourceMetadata
import com.mybatisflex.kotlin.codegen.metadata.PropertyType
import com.mybatisflex.kotlin.codegen.metadata.TableMetadata
import com.mybatisflex.kotlin.codegen.metadata.commonQuerySql
import metadata.ColumnMetadata
import java.sql.ResultSet

open class DefaultProvider : MetadataProvider {
    open lateinit var dataSource: DataSourceMetadata

    override fun provideMetadata(dataSource: DataSourceMetadata): Set<TableMetadata> = buildSet {
        this@DefaultProvider.dataSource = dataSource
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
                    val res = ColumnMetadata(
                        name = column.getColumnName(i),
                        propertyType = PropertyType.of(column.getColumnClassName(i)),
                        rawType = column.getColumnClassName(i),
                        table = tableMetadata,
                        comment = commentMap[column.getColumnName(i)],
                        nullable = column.isNullable(i) == 1,
                        autoIncrement = column.isAutoIncrement(i),
                        rawLength = column.getColumnDisplaySize(i)
                    )
                    yield(res)
                }
            }
        }
    }

    protected open fun getColumnComment(tableMetadata: TableMetadata): Map<String, String> = buildMap {
        with(dataSource) {
            databaseMetaData.getColumns(connection.catalog, schema, tableMetadata.tableName, null)
        }.use {
            while (it.next()) {
                this[it.getString("COLUMN_NAME")] = it.getString("REMARKS")
            }
        }
    }

    protected open val TableMetadata.querySql: String
        get() = commonQuerySql

    protected open val ResultSet.tableName: String
        get() = getString("TABLE_NAME")

    protected open val ResultSet.tableComment: String
        get() = getString("REMARKS")

    protected open fun TableMetadata.initPrimaryKey() {
        with(dataSource) {
            databaseMetaData.getPrimaryKeys(connection.catalog, schema, tableName)
        }.use {
            while (it.next()) {
                primaryKey += it.getString("COLUMN_NAME")
            }
        }
    }
}