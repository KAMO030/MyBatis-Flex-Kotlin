package com.mybatisflex.kotlin.flexStream


import com.example.springbootdemo.flexStream.*
import com.mybatisflex.annotation.Table
import com.mybatisflex.core.BaseMapper
import com.mybatisflex.core.mybatis.Mappers
import com.mybatisflex.core.query.BaseQueryWrapper
import com.mybatisflex.core.query.QueryChain
import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.row.Row
import com.mybatisflex.core.table.TableDef
import com.mybatisflex.core.table.TableDefs
import kotlin.collections.toList

// O为Mapper绑定的实体类
@ExperimentalFlexStream
open class FlexStream<T, O : Any>(
    open val entity: O,  // 实体类
    private val _tableDef: T? = null,  // apt生成的TableDef
    open val mapper: BaseMapper<O>? = try {
        Mappers.ofEntityClass(entity.javaClass)
    } catch (_: Throwable) {
        null
    },
    queryChain: QueryChain<O> = QueryChain(mapper),
    val copy: Boolean = true
) : Iterable<O> {
    private val _queryChain = queryChain

    private val iterList: List<O> by lazy { toList() }

    open val queryChain: QueryChain<O>
        get() = if (copy) _queryChain.clone() else _queryChain

    open val tableDef: TableDef
        get() {
            if (_tableDef !== null && _tableDef is TableDef) return _tableDef
            val clazz = entity::class.java
            val table = clazz.getAnnotation(Table::class.java)
                ?: throw IllegalArgumentException("Declared classes must be annotated by ${Table::class.java}")
            val scheme = table.schema
            val tableName = table.value
            return TableDefs.getTableDef(clazz, "$scheme${if (scheme.isNotBlank()) "." else ""}$tableName")
        }

    var offset = 0L
        private set

    var rows = -1L
        private set

    @get:JvmName("toSQL")
    val sql: String
        get() = _queryChain.toSQL()

    companion object {
        inline operator fun <reified T : TableDef, reified I : Any> invoke(copy: Boolean = true) =
            this(T::class.java, I::class.java, copy)

        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        @JvmName("newInstance")
        @JvmOverloads
        operator fun <T : TableDef, O : Any> invoke(
            tableDefClass: Class<T>,
            entityClass: Class<O>,
            copy: Boolean = true
        ): FlexStream<T, O> {
            val mapper = try {
                Mappers.ofEntityClass(entityClass)
            } catch (e: Throwable) {
                null
            }
            val field = tableDefClass.getField(
                tableDefClass.simpleName
                    .replace("TableDef", "")
                    .snakeFormat
                    .uppercase()
            )
            val tableDef = field[null] as T
            val flexData = try {
                entityClass.getDeclaredConstructor()
            } catch (e: NoSuchMethodException) {
                throw NoSuchMethodException("The entity class must provide a parameterless constructor.")
            }.newInstance()
            return FlexStream(flexData, tableDef, mapper as? BaseMapper<O>?, copy = copy)
        }

        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        @JvmName("newInstance")
        @JvmOverloads
        operator fun invoke(copy: Boolean = true) = FlexStream(Row(), NonexistentTableDef, copy = copy)
    }

    open infix fun limit(range: LongRange): FlexStream<T, O> = limit(range.first, range.last)

    open infix fun limit(range: IntRange): FlexStream<T, O> = limit(range.first.toLong(), range.last.toLong())

    open fun limit(offset: Long, rows: Long): FlexStream<T, O> {
        require(offset >= 0) {
            "offset must greater than zero."
        }
        require(rows >= 1) {
            "rows must greater than one."
        }
        this.offset = offset
        this.rows = rows
        return copyIf(queryChain = queryChain.limit(offset, rows))
    }


    fun copy(
        tableDef: T? = this._tableDef,
        mapper: BaseMapper<O>? = this.mapper,
        flexData: O = this.entity,
        queryChain: QueryChain<O> = this.queryChain,
        copy: Boolean = this.copy
    ) = FlexStream(flexData, tableDef, mapper, queryChain, copy).also {
        it.rows = rows
        it.offset = offset
    }

    fun copyIf(
        tableDef: T? = this._tableDef,
        mapper: BaseMapper<O>? = this.mapper,
        flexData: O = this.entity,
        queryChain: QueryChain<O> = this.queryChain,
        copy: Boolean = this.copy
    ) = if (this.copy) copy(tableDef, mapper, flexData, queryChain, copy) else this

    open infix fun drop(idx: Long): FlexStream<T, O> {
        offset = idx
        return copyIf(queryChain = queryChain)
    }

    open infix fun take(idx: Long): FlexStream<T, O> {
        rows = idx
        return copyIf(queryChain = queryChain)
    }

    override fun iterator(): Iterator<O> = iterList.listIterator()

    @ExperimentalFlexStream
    @Suppress("UNCHECKED_CAST")
    fun distinct(): FS<T, O> {
        val clazz = BaseQueryWrapper::class.java
        val field = clazz.getDeclaredField("selectColumns")
        field.isAccessible = true
        val queryChain = _queryChain
        val list = field[queryChain] as? List<QueryColumn>?
        println(list)
        field[queryChain] = object : ArrayList<QueryColumn>() {
            override fun add(element: QueryColumn): Boolean {
                return if (isEmpty()) super.add(QueryFunctions.distinct(element)) else super.add(element)
            }
        }.apply { list?.forEach { add(it) } }
        println(field[queryChain])
        return copy(queryChain = queryChain)
    }
}
