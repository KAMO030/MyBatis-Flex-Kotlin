package com.mybatisflex.kotlin.vec

import com.mybatisflex.core.BaseMapper
import com.mybatisflex.core.mybatis.Mappers
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.core.table.TableDef
import com.mybatisflex.core.table.TableDefs
import com.mybatisflex.core.table.TableInfo
import com.mybatisflex.core.table.TableInfoFactory
import com.mybatisflex.kotlin.extensions.vec.wrap

open class QueryVector<E>(
    open val entityClass: Class<E>,
    open val data: QueryData,
    open val entityInstance: E? = null
) {
    companion object {
        inline operator fun <reified E : Any> invoke(tableAlias: String? = null): QueryVector<E> {
            val clazz = E::class.java
            val tableDef = TableDefs.getTableDef(clazz, TableInfoFactory.ofEntityClass(clazz).tableNameWithSchema)
                ?: throw IllegalArgumentException("QueryVector cannot be initialized by class $clazz, which does not have a corresponding TableDef.")
            return QueryVector(clazz, QueryData(table = tableDef, tableAlias = tableAlias ?: tableDef.tableName))
        }
    }

    val entity: E get() = entityInstance ?: entityClass.getDeclaredConstructor().newInstance()

    val wrapper: QueryWrapper get() = data.wrap()

    val sql: String get() = wrapper.toSQL()

    val tableDef: TableDef
        get() = TableDefs.getTableDef(entityClass, tableInfo.tableNameWithSchema)
            ?: throw NoSuchElementException("The TableDef corresponding to class $entityClass could not be found")


    val tableInfo: TableInfo
        get() = TableInfoFactory.ofEntityClass(entityClass)
            ?: throw NoSuchElementException("The TableInfo corresponding to class $entityClass could not be found")

    val size: Long get() = mapper.selectCountByQuery(wrapper)

    val mapper: BaseMapper<E> get() = Mappers.ofEntityClass(entityClass)

    fun copy(
        data: QueryData = this.data,
        entityClass: Class<E> = this.entityClass
    ) = QueryVector(entityClass, data)
}