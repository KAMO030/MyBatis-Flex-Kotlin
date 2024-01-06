package com.mybatisflex.kotlin.ksp.internal.config.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.mybatisflex.annotation.Column
import com.mybatisflex.kotlin.ksp.illegalValueWarning
import com.mybatisflex.kotlin.ksp.internal.gen.TableDefGenerator
import com.mybatisflex.kotlin.ksp.internal.gen.cls.ClassGenerator
import com.mybatisflex.kotlin.ksp.internal.gen.obj.ObjectGenerator
import com.mybatisflex.kotlin.ksp.internal.util.DEFAULT_SUPPORT_COLUMN_TYPES
import com.mybatisflex.kotlin.ksp.internal.util.QUERY_COLUMN
import com.mybatisflex.kotlin.ksp.internal.util.legalProperties
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter

/**
 * 指定 default columns 的类型。默认情况下为 [List] 。
 *
 * 支持四种类型：[List], [Array], [Sequence], [Set] 。
 *
 * @author CloudPlayer
 */
internal object DefaultColumnsType : KspConfiguration<ParameterizedTypeName> {
    override val key: String = "ksp.prop.type.defaultColumns"

    private var _value = LIST

    // 如果是数组类型，则其中的泛型为 out QueryColumn，以防止数组被修改。
    override val value: ParameterizedTypeName
        get() = if (_value != ARRAY) _value.parameterizedBy(QUERY_COLUMN)
        else _value.plusParameter(WildcardTypeName.producerOf(QUERY_COLUMN))

    /**
     * 用于构建 default columns 时所使用的 kotlin 标准库中的函数名。
     * 分别对应着：
     *
     * 1，[listOf]
     *
     * 2，[setOf]
     *
     * 3，[arrayOf]
     *
     * 4，[sequenceOf]
     */
    var fnName: String = "list"
        get() = "${field}Of"
        private set

    /**
     * 合法的类型。其值为对应的 ClassName 。
     */
    private val legalTypes = mapOf(
        "list" to LIST,
        "set" to SET,
        "array" to ARRAY,
        "sequence" to ClassName("kotlin.sequences", "Sequence"),
    )

    override fun initValue(value: String) {
        val type = legalTypes[value] ?: return illegalValueWarning(key, value)
        fnName = value
        _value = type
    }
}

/**
 * 用于配置是否开启属性的合法性检查。默认开启。
 *
 * 如果开启了合法性检查，那么当属性不为枚举类，
 * 类型不在 [DEFAULT_SUPPORT_COLUMN_TYPES] 之中，且其 [Column.typeHandler] 没有配置为其他时，将忽略该属性。
 *
 * @author CloudPlayer
 * @see KSClassDeclaration.legalProperties
 */
internal object PropertyTypeChecker : KspConfiguration<Boolean> {
    override val key: String = "ksp.prop.type.check"

    private var _value = true

    override val value: Boolean
        get() = _value

    override fun initValue(value: String) {
        _value = value.toBoolean()
    }
}

/**
 * 用于指定 KSP 生成的 TableDef 为 class 或 object 。
 *
 * 一般情况下，我们推荐您使用 object 而不是 class 。在以下情况中，你需要使用 class 而不是 object ：
 *
 * 1，你需要使用构造器来获取一个新的实例。尽管我们给 object 提供了 invoke 方法来模拟构造器行为，但这终究并非构造器。
 *
 * 2，你需要完全兼容 Java 代码。生成的 class 能够完全像 apt 生成的 Java 类一样在 Java 调用。
 *
 * @author CloudPlayer
 */
internal object GenerateType : KspConfiguration<GenerateTypeEnum> {
    override val key: String = "ksp.generate.type"

    private var _value: GenerateTypeEnum = GenerateTypeEnum.OBJECT

    override val value: GenerateTypeEnum
        get() = _value

    override fun initValue(value: String) {
        _value = try {
            GenerateTypeEnum.valueOf(value.uppercase())
        } catch (_: IllegalArgumentException) {
            return illegalValueWarning(key, value)
        }
    }
}

enum class GenerateTypeEnum(val description: String, val tableDefGenerator: TableDefGenerator) {
    OBJECT("object", ObjectGenerator()), CLASS("class", ClassGenerator());
}