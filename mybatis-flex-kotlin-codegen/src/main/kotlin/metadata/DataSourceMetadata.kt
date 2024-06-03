package com.mybatisflex.kotlin.codegen.metadata

import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.ResultSet
import javax.sql.DataSource

open class DataSourceMetadata(
    val dataSource: DataSource,
    val schema: String? = null,
    val connection: Connection = dataSource.connection,
    val databaseMetaData: DatabaseMetaData = connection.metaData,
) {
    open val tablesResultSet: ResultSet
        get() = databaseMetaData.getTables(connection.catalog, schema, null, arrayOf("TABLE", "VIEW"))

    override fun toString(): String {
        return "DataSourceMetadata(dataSource=$dataSource, connection=$connection, databaseMetaData=$databaseMetaData)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DataSourceMetadata) return false

        if (dataSource != other.dataSource) return false
        if (connection != other.connection) return false
        if (databaseMetaData != other.databaseMetaData) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dataSource.hashCode()
        result = 31 * result + connection.hashCode()
        result = 31 * result + databaseMetaData.hashCode()
        return result
    }
}