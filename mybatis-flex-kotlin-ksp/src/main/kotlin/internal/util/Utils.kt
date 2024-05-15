package com.mybatisflex.kotlin.ksp.internal.util

import com.google.devtools.ksp.*
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.mybatisflex.annotation.Column
import com.mybatisflex.annotation.ColumnAlias
import com.mybatisflex.annotation.Table
import com.mybatisflex.kotlin.ksp.codeGenerator
import com.mybatisflex.kotlin.ksp.internal.config.flex.*
import com.mybatisflex.kotlin.ksp.internal.config.ksp.DefaultColumnsType
import com.mybatisflex.kotlin.ksp.internal.config.ksp.PropertyTypeChecker
import com.mybatisflex.kotlin.ksp.internal.util.anno.column
import com.mybatisflex.kotlin.ksp.internal.util.anno.table
import com.mybatisflex.kotlin.ksp.internal.util.str.asColumnName
import com.mybatisflex.kotlin.ksp.internal.util.str.asPropertyName
import com.mybatisflex.kotlin.ksp.internal.util.str.filterInstanceSuffix
import com.mybatisflex.kotlin.ksp.logger
import com.mybatisflex.kotlin.ksp.options
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.jvm.jvmField
import com.squareup.kotlinpoet.ksp.kspDependencies
import com.squareup.kotlinpoet.ksp.toClassName
import org.apache.ibatis.type.UnknownTypeHandler
import java.time.*
import java.util.*

/**
 * 根据 [KSPropertyDeclaration] 得到的属性名，已经过属性名风格的转换。
 *
 * 名称风格受到 mybatis-flex.config 文件影响。
 * @see TableDefPropertiesNameStyle 影响属性名风格对应的键值对。
 * @see asPropertyName 将原属性名转换为新属性名的函数。
 * @receiver 实体类中对应的属性声明。
 * @author CloudPlayer
 */
val KSPropertyDeclaration.propertyName: String
    get() = simpleName.asString().asPropertyName()

/**
 * 根据 [KSPropertyDeclaration] 得到的列名，其影响生成时该属性对应的列名。在生成 [QUERY_COLUMN] 类时，会影响传递到主构造器内部的值。
 *
 * 受到注解 [Table.camelToUnderline] 是否驼峰转下划线，以及注解 [Column.value] 中直接指定列名影响。
 *
 * 如果在 [Column.value] 中直接指定列名，那么将直接使用该列名，否则使用属性名作为列名。
 *
 * @see Table.camelToUnderline 是否驼峰转下划线。
 * @see Column.value 直接指定列名。
 * @receiver 实体类中对应的属性声明。
 * @author CloudPlayer
 */
val KSPropertyDeclaration.columnName: String
    get() {
        // 从属性声明中获取最近的类声明（即该属性所在的类），并从类声明获得 Table 注解
        val table = closestClassDeclaration()!!.table
        // 如果没有 Column 注解，或者注解的名字是没有长度的（就是没有主动设置过列名），则使用属性名作为列名
        val columnName = column?.value
        if (columnName === null || columnName.isEmpty()) {
            return simpleName.asString().asColumnName(table.camelToUnderline)
        }
        return columnName.asColumnName(table.camelToUnderline)
    }

/**
 * 是否懒加载。若为是，则所有类的所有属性均会使用函数 [lazy] 进行包裹以懒加载。
 *
 * 要开启懒加载，需要在 build.gradle 配置文件中的 ksp 配置项中，增加 arg("flex.generate.lazy", "true") 以开启懒加载。
 *
 * 你可以在 build.gradle 中按照如下示例开启懒加载：
 * ```build.gradle
 * ksp {
 *     arg("flex.generate.lazy", "true")
 * }
 * ```
 * @author CloudPlayer
 * @see lazy
 */
fun isLazy(): Boolean = options["flex.generate.lazy"]?.toBoolean() == true

/**
 * 用于默认初始化或延迟初始化，根据 [KSPropertyDeclaration] 中指示的列名来初始化。
 *
 * 其中的列名已经由 [KSPropertyDeclaration.columnName] 中解析获得。
 *
 * @param initBlock 需要被初始化的块。
 * @receiver 需要被初始化的属性。
 * @see KSPropertyDeclaration.columnName
 * @return 已初始化后的属性。
 * @author CloudPlayer
 */
fun PropertySpec.Builder.initByLazyOrDefault(initBlock: String, vararg args: Any?): PropertySpec.Builder {
    return if (isLazy()) {
        delegate(
            """
            |lazy {
            |    $initBlock
            |}
            """.trimMargin(),
            *args
        )
    } else {
        initializer(initBlock, *args).jvmField()
    }
}


/**
 * 获得已初始化的 PropertySpec ，且已经根据实体类中对应属性上面的注释为生成的属性打好了相同注释。
 *
 * 其在 [PropertySpec.Builder.initByLazyOrDefault] 中已完成初始化。
 * @param tableDef 实例化 QueryColumn 时，传入的第一个参数，表示 tableDef 的子类。
 * @receiver 实体类中对应的属性声明。
 * @see PropertySpec.Builder.initByLazyOrDefault
 * @see KSPropertyDeclaration.propertyName
 * @author CloudPlayer
 */
@OptIn(KspExperimental::class)
fun KSPropertyDeclaration.getPropertySpecBuilder(tableDef: String = "this"): PropertySpec.Builder {
    val name = propertyName
    val builder = PropertySpec.builder(
        name,
        QUERY_COLUMN
    )
    docString?.let {
        builder.addKdoc(it.trimIndent())
    }
    val columnName = columnName
    val columnAlias = getAnnotationsByType(ColumnAlias::class).firstOrNull()
    val res = if (columnAlias !== null) {
        """%T($tableDef, %S, "${columnAlias.value[0]}")"""
    } else {
        "%T($tableDef, %S)"
    }
    return builder.initByLazyOrDefault(res, QUERY_COLUMN, columnName)
}

/**
 * 获得即将生成的 TableDef 的类名。其名字已过滤实体类后缀并增添 TableDef 类后缀。
 *
 * @see TableDefClassSuffix.value
 * @see String.filterInstanceSuffix
 * @author CloudPlayer
 */
val KSClassDeclaration.tableClassName: String
    get() {
        val suffix = TableDefClassSuffix.value
        return "${simpleName.asString().filterInstanceSuffix()}$suffix"
    }

/**
 * 实例对象的名字。为考虑兼容性，在生成时会额外生成一个静态的属性引用自身，此名字
 * 就是此静态属性的名字，以和 apt 进行适配。
 *
 * @see TableDefClassSuffix.value
 * @see String.asPropertyName
 * @author CloudPlayer
 */
val KSClassDeclaration.instanceName: String
    get() {
        val value = TableDefInstanceSuffix.value
        return "${simpleName.asString()}$value".asPropertyName()
    }

/**
 * Mapper 接口的接口名。
 *
 * @author CloudPlayer
 */
val KSClassDeclaration.interfaceName: String
    get() {
        val value = simpleName.asString()
        return "${value.filterInstanceSuffix()}Mapper"
    }

/**
 * 实体类在 [Table.schema] 中指定的架构。影响生成的类在委托父类主构造器时传递的参数。
 *
 * @see Table.schema
 * @author CloudPlayer
 */
val KSClassDeclaration.schema: String
    get() {
        val table = table
        return table.schema
    }

/**
 * 实体类在 [Table.value] 中指定的表名。影响生成的类在委托父类主构造器时传递的参数。
 *
 * @see Table.value
 * @author CloudPlayer
 */
val KSClassDeclaration.tableName: String
    get() = table.value

/**
 * 生成 all columns 属性。
 *
 * 一般情况下，它都是 `QueryColumn(this, "*")` 。
 *
 * @author CloudPlayer
 */
val allColumnsBuilder: PropertySpec.Builder by lazy {
    PropertySpec.builder(
        "allColumns".asPropertyName(),
        QUERY_COLUMN
    ).initByLazyOrDefault("%T(this, %S)", QUERY_COLUMN, "*")
}

/**
 * 生成 default columns 属性。
 *
 * 默认情况下，它的类型是 [List] 而不是 [Array]。
 *
 * @param sequence default columns中包含的属性。大字段不包括其中。
 * @see DefaultColumnsType
 * @return 已构建好的 default columns 。
 * @author CloudPlayer
 */
fun getDefaultColumns(sequence: Sequence<KSPropertyDeclaration>): PropertySpec.Builder {
    val builder = PropertySpec.builder(
        "defaultColumns".asPropertyName(),
        DefaultColumnsType.value,
    )
    val fnName = DefaultColumnsType.fnName
    val columns = StringJoiner(", ")
    val columnsName = ArrayList<String>()
    sequence.forEach {
        val column = it.column
        if (column === null || (!column.isLarge && !column.ignore)) {
            columns.add("%N")
            columnsName += it.propertyName
        }
    }
    builder.initByLazyOrDefault("%N($columns)", fnName, *columnsName.toTypedArray())
    return builder
}

/**
 * 生成新代码。生成的代码的字符集即为自定义的字符集。
 *
 * @see FlexCharset.value
 * @param files 依赖的所有文件，用于增量编译。
 * @param aggregating 是否使用聚合模式。生成 TableDef 和 Mapper 时为 false 即启用隔离模式，生成 Tables 为聚合模式。
 * @author CloudPlayer
 */
fun FileSpec.write(aggregating: Boolean = false, vararg files: KSFile?) {
    val dependencies = kspDependencies(aggregating, files.filterNotNull())
    val outputStream = codeGenerator.createNewFile(dependencies, packageName, name)
    outputStream.writer(FlexCharset.value).use(::writeTo)
}

/**
 * 指示实体类对应的 Mapper 类应生成在哪个包下。
 *
 * @author CloudPlayer
 * @see MapperPackage.value
 */
val KSClassDeclaration.mapperPackageName: String
    get() = MapperPackage.value ?: "${packageName.asString()}.mapper"

/**
 * 生成文件时，压制默认的警告。
 *
 * 这些警告包括：
 *
 * 1，可见性修饰符冗余（即属性，类，函数前的 public）的警告
 *
 * 2，可见性可为 private 的弱警告
 *
 * 3，从未使用xxx的警告。
 *
 * @author CloudPlayer
 * @receiver 要压制警告的文件
 * @see Suppress
 */
fun FileSpec.Builder.suppressDefault(): FileSpec.Builder = addAnnotation(
    AnnotationSpec.builder(SUPPRESS)
        .addMember("\"RedundantVisibilityModifier\", \"MemberVisibilityCanBePrivate\", \"unused\"")
        .build()
)

/**
 * 实例对象名。
 *
 * ```kotlin
 * object MyTableDef : TableDef(..., ...) {
 *      @JvmField
 *      val ... = MyTableDef  // 这里即为对应的实例对象名。
 * }
 * ```
 *
 * @param typeName 生成的 TableDef 类的类名。
 * @receiver 实体类声明。
 */
fun KSClassDeclaration.instanceProperty(
    typeName: ClassName,
    initBlock: String = "%T",
    vararg args: Any?
): PropertySpec.Builder {
    val field = PropertySpec.builder(instanceName, typeName)
    field.initializer(initBlock, typeName, *args)
    field.jvmField()
    return field
}

/**
 * 从类声明中解析出需要生成的合法属性。
 *
 * @author CloudPlayer
 */
val KSClassDeclaration.legalProperties: Sequence<KSPropertyDeclaration>
    get() = getAllProperties().filter {
        it.hasBackingField  // 只处理拥有 backing field 的属性（没有 backing field 的运行时使用会报错）。
    }.filter { prop ->
        val column = prop.column
        if (column?.ignore == true) {
            return@filter false
        }
        if (PropertyTypeChecker.value) {
            val returnType = prop.type.resolve()
            val classDeclaration = returnType.declaration as KSClassDeclaration

            classDeclaration.classKind === ClassKind.ENUM_CLASS  // 属性的返回值是枚举类
                    || classDeclaration.qualifiedName?.asString() in DEFAULT_SUPPORT_COLUMN_TYPES  // 属性的返回值类型是默认支持的类型之一
                    || (column !== null && !column.isUnknownTypeHandler())  // 不是默认类型但是配置了 TypeHandler 。
        } else {
            true
        }
    }

/**
 * 用于判断 [Column.typeHandler] 是否为 [UnknownTypeHandler] 。
 *
 * 正常情况下，应当在 try 块中就直接返回。 catch 块用于保证在极特殊情况下仍然能够正常返回值。
 *
 * @author CloudPlayer
 * @return true 表示 [Column.typeHandler] 为 [UnknownTypeHandler] ，否则为 false 。
 */
@OptIn(KspExperimental::class)
fun Column.isUnknownTypeHandler(): Boolean = try {
    typeHandler.java === Class.forName("org.apache.ibatis.type.UnknownTypeHandler")
} catch (e: KSTypeNotPresentException) {
    val type = e.ksType.declaration as KSClassDeclaration
    type.toClassName() == UNKNOWN_TYPE_HANDLER
} catch (e: Throwable) {
    logger.exception(e)
    error("unreachable code")
}

@JvmField
val BASE_MAPPER = ClassName("com.mybatisflex.core", "BaseMapper")

@JvmField
val QUERY_COLUMN = ClassName("com.mybatisflex.core.query", "QueryColumn")

@JvmField
val SUPPRESS = ClassName("kotlin", "Suppress")

@JvmField
val TABLE_DEF = ClassName("com.mybatisflex.core.table", "TableDef")

@JvmField
val UNKNOWN_TYPE_HANDLER = ClassName("org.apache.ibatis.type", "UnknownTypeHandler")
