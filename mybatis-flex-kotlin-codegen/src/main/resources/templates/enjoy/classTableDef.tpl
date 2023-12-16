#set(tableDefClassName = table.buildTableDefClassName())
#set(schema = table.schema == null ? "" : table.schema)
package #(packageConfig.tableDefPackage)

import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.table.TableDef

/**
 * #(table.getComment()) 表定义层。
 *
 * @author #(javadocConfig.getAuthor())
 * @since #(javadocConfig.getSince())
 */
class #(tableDefClassName) : TableDef("#(schema)", "#(table.name)") {

    companion object {
        /**
         * #(table.getComment())
         */
        @JvmField
        val #(tableDefConfig.buildFieldName(table.buildEntityClassName() + tableDefConfig.instanceSuffix)): #(tableDefClassName) = #(tableDefClassName)()
    }



#for(column: table.getSortedColumns())
    #(column.buildComment())
    val #(tableDefConfig.buildFieldName(column.property)): QueryColumn = QueryColumn(this, "#(column.name)")

#end
    /**
     * 所有字段。
     */
    val #(tableDefConfig.buildFieldName("allColumns")): QueryColumn = QueryColumn(this, "*")

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    val #(tableDefConfig.buildFieldName("defaultColumns")): Array<out QueryColumn> = arrayOf(#for(column: table.columns)#if(column.isDefaultColumn())#(tableDefConfig.buildFieldName(column.property))#if(for.index + 1 != for.size), #end#end#end)

}