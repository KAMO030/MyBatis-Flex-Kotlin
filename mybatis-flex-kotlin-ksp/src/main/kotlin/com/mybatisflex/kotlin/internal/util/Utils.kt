package com.mybatisflex.kotlin.internal.util

import com.mybatisflex.kotlin.codeGenerator
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.mybatisflex.annotation.Column
import com.mybatisflex.annotation.Table
import com.mybatisflex.kotlin.internal.config.flex.FlexCharset
import com.mybatisflex.kotlin.internal.config.flex.MapperPackage
import com.mybatisflex.kotlin.internal.config.flex.TableDefClassSuffix
import com.mybatisflex.kotlin.internal.config.flex.TableDefInstanceSuffix
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.jvm.jvmField
import com.squareup.kotlinpoet.ksp.kspDependencies
import com.mybatisflex.kotlin.internal.config.flex.*
import com.mybatisflex.kotlin.internal.config.ksp.DefaultColumnsType
import com.mybatisflex.kotlin.internal.util.str.asColumnName
import com.mybatisflex.kotlin.internal.util.str.asPropertyName
import com.mybatisflex.kotlin.internal.util.str.filterInstanceSuffix
import com.mybatisflex.kotlin.options
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
internal val KSPropertyDeclaration.propertyName: String
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
@OptIn(KspExperimental::class)
internal val KSPropertyDeclaration.columnName: String
    get() {
        // 从属性声明中获取最近的类声明（即该属性所在的类），并从类声明获得 Table 注解
        val table = closestClassDeclaration()!!.getAnnotationsByType(Table::class).first()
        val column = getAnnotationsByType(Column::class).firstOrNull()
        // 如果没有 Column 注解，则使用属性名作为列名
        return (column?.value ?: simpleName.asString()).asColumnName(table.camelToUnderline)
    }

/**
 * 是否为大字段。该属性从 [Column.isLarge] 中获得。
 *
 * 如果该字段为大字段，则不会添加进生成的默认列（即 default columns）中。
 * @author CloudPlayer
 * @receiver 实体类中对应的属性声明。
 * @see Column.isLarge
 */
@OptIn(KspExperimental::class)
internal val KSPropertyDeclaration.isLarge: Boolean
    get() {
        val column = getAnnotationsByType(Column::class).firstOrNull()
        return column?.isLarge == true
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
internal fun isLazy(): Boolean = options["flex.generate.lazy"]?.toBoolean() == true

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
internal fun PropertySpec.Builder.initByLazyOrDefault(initBlock: String): PropertySpec.Builder {
    return if (isLazy()) {
        delegate(
            """
            |lazy {
            |    $initBlock
            |}
            """.trimMargin()
        )
    } else {
        initializer(initBlock).jvmField()
    }
}


/**
 * 获得已初始化的 PropertySpec ，且已经根据实体类中对应属性上面的注释为生成的属性打好了相同注释。
 *
 * 其在 [PropertySpec.Builder.initByLazyOrDefault] 中已完成初始化。
 * @receiver 实体类中对应的属性声明。
 * @see PropertySpec.Builder.initByLazyOrDefault
 * @see KSPropertyDeclaration.propertyName
 * @author CloudPlayer
 */
internal val KSPropertyDeclaration.propertySpecBuilder: PropertySpec.Builder
    get() {
        val name = propertyName
        val builder = PropertySpec.builder(
            name,
            QUERY_COLUMN
        )
        docString?.let {
            builder.addKdoc(it.trimIndent())
        }
        return builder.initByLazyOrDefault("QueryColumn(this,  \"$name\")")
    }

/**
 * 获得即将生成的 TableDef 的类名。其名字已过滤实体类后缀并增添 TableDef 类后缀。
 *
 * @see TableDefClassSuffix.value
 * @see String.filterInstanceSuffix
 * @author CloudPlayer
 */
internal val KSClassDeclaration.tableClassName: String
    get() {
        val suffix = TableDefClassSuffix.value
        return "${simpleName.asString().filterInstanceSuffix}$suffix"
    }

/**
 * 实例对象的名字。为考虑兼容性，在生成时会额外生成一个静态的属性引用自身，此名字
 * 就是此静态属性的名字，以和 apt 进行适配。
 *
 * @see TableDefClassSuffix.value
 * @see String.asPropertyName
 * @author CloudPlayer
 */
internal val KSClassDeclaration.instanceName: String
    get() {
        val value = TableDefInstanceSuffix.value
        return "${simpleName.asString()}$value".asPropertyName()
    }

/**
 * Mapper 接口的接口名。
 *
 * @author CloudPlayer
 */
internal val KSClassDeclaration.interfaceName: String
    get() {
        val value = simpleName.asString()
        return "${value.filterInstanceSuffix}Mapper"
    }

/**
 * 实体类在 [Table.schema] 中指定的架构。影响生成的类在委托父类主构造器时传递的参数。
 *
 * @see Table.schema
 * @author CloudPlayer
 */
@OptIn(KspExperimental::class)
internal val KSClassDeclaration.scheme: String
    get() {
        val table = getAnnotationsByType(Table::class).first()
        return table.schema
    }

/**
 * 实体类在 [Table.value] 中指定的表名。影响生成的类在委托父类主构造器时传递的参数。
 *
 * @see Table.value
 * @author CloudPlayer
 */
@OptIn(KspExperimental::class)
internal val KSClassDeclaration.tableName: String
    get() {
        val table = getAnnotationsByType(Table::class).first()
        return table.value
    }

/**
 * 生成 all columns 属性。
 *
 * 一般情况下，它都是 `QueryColumn(this, "*")` 。
 *
 * @author CloudPlayer
 */
internal val allColumns: PropertySpec.Builder
    get() {
        val builder = PropertySpec.builder(
            "allColumns".asPropertyName(),
            QUERY_COLUMN
        )
        builder.initByLazyOrDefault("QueryColumn(this, \"*\")")
        return builder
    }

/**
 * 生成 default columns 属性。
 *
 * 默认情况下，它的类型是 [List] 而不是 [Array]。
 *
 * @param iterable default columns中包含的属性。大字段不包括其中。
 * @return 已构建好的 default columns 。
 * @author CloudPlayer
 */
internal fun getDefaultColumns(iterable: Sequence<KSPropertyDeclaration>): PropertySpec.Builder {
    val builder = PropertySpec.builder(
        "defaultColumns".asPropertyName(),
        DefaultColumnsType.value,
    )
    val fnName = DefaultColumnsType.fnName
    val columns = StringJoiner(",")
    iterable.forEach {
        if (!it.isLarge) columns.add("`${it.propertyName}`")
    }
    builder.initByLazyOrDefault("$fnName($columns)")
    return builder
}

/**
 * 生成新代码。生成的代码的字符集即为自定义的字符集。
 *
 * @see FlexCharset.value
 * @author CloudPlayer
 */

internal fun FileSpec.write() {
    val dependencies = kspDependencies(false)
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
    get() {
        val pack = MapperPackage.value
        val res by lazy {
            "${packageName.asString()}.mapper"
        }
        return pack ?: res
    }

/**
 * 给对象增添一个无参操作符函数 invoke ，用于模拟对象的主构造器以实现兼容。在调用时均会返回自身。
 *
 * 正如下面的例子所示：
 * ```kotlin
 * object MyTableDef : TableDef(..., ...) {
 *      ...  // ksp 生成的其他内容。
 *      inline operator fun invoke() = MyTableDef  // 在调用时模拟主构造器 new 对象的行为。
 * }
 * ```
 * @author CloudPlayer
 */
fun invokeFunction(): FunSpec.Builder {
    val func = FunSpec.builder("invoke")
    func.modifiers += KModifier.OPERATOR
    func.modifiers += KModifier.INLINE
    func.addKdoc("A constructor used to mock objects for code compatibility. It returns itself on each call.")
    func.addCode("return this")
    func.addAnnotation(
        AnnotationSpec.builder(SUPPRESS)
            .addMember("\"NOTHING_TO_INLINE\"")
            .build()
    )
    return func
}

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
 * 4，冗余反引号。
 *
 * @author CloudPlayer
 * @receiver 要压制警告的文件
 * @see Suppress
 */
fun FileSpec.Builder.suppressDefault(): FileSpec.Builder = addAnnotation(
    AnnotationSpec.builder(SUPPRESS)
        .addMember("\"RedundantVisibilityModifier\", \"MemberVisibilityCanBePrivate\", \"unused\", \"RemoveRedundantBackticks\"")
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
fun KSClassDeclaration.instanceProperty(typeName: ClassName): PropertySpec.Builder {
    val field = PropertySpec.builder(instanceName, typeName)
    field.initializer(typeName.simpleName)
    field.jvmField()
    return field
}

@JvmField
val BASE_MAPPER = ClassName("com.mybatisflex.core", "BaseMapper")

@JvmField
val QUERY_COLUMN = ClassName("com.mybatisflex.core.query", "QueryColumn")

@JvmField
val SUPPRESS = ClassName("kotlin", "Suppress")

@JvmField
val TABLE_DEF = ClassName("com.mybatisflex.core.table", "TableDef")
