package com.mybatisflex.kotlin.codegen.metadata

/**
 * 数据库表里面的列信息。
 */
data class ColumnMetadata(
    /**
     * 名称。
     */
    val name: String,

    /**
     * 属性类型。
     */
    val propertyType: PropertyType,

    /**
     * 数据库的字段类型，比如 varchar/tinyint 等
     */
    val rawType: String,

    /**
     * 属于哪张表
     */
    val table: TableMetadata,

    /**
     * 注释。
     */
    val comment: String? = null,

    /**
     * 是否为主键。
     */
    var isPrimaryKey: Boolean = false,

    /**
     * 是否可为空。
     */
    val nullable: Boolean = true,

    /**
     * 是否自增。
     */
    val autoIncrement: Boolean = false,

    /**
     * 数据库中的字段长度，比如 varchar(32) 中的 32
     */
    val rawLength: Int = Int.MIN_VALUE,
) {
    override fun toString(): String {
        return "ColumnMetadata(name='$name', propertyType=$propertyType, rawType='$rawType', table=${table.tableName}, isPrimaryKey=$isPrimaryKey, comment=$comment, nullable=$nullable, autoIncrement=$autoIncrement, rawLength=$rawLength)"
    }
}
