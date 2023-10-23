package com.mybatisflex.kotlin.internal.config.ksp

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.mybatisflex.kotlin.illegalValueWarning
import com.mybatisflex.kotlin.internal.util.QUERY_COLUMN

/**
 * 指定 default columns 的类型。默认情况下为 [List] 。
 *
 * 支持四种类型：[List], [Array], [Sequence], [Set] 。
 *
 * @author CloudPlayer
 */
internal object DefaultColumnsType : KspConfiguration<ParameterizedTypeName> {
    override val key: String = "ksp.type.defaultColumns"

    private var _value = LIST

    // 如果是数组类型，则其中的泛型为 out QueryColumn，以防止数组被修改。
    override val value: ParameterizedTypeName
        get() = if (_value != ARRAY) _value.parameterizedBy(QUERY_COLUMN)
        else _value.plusParameter(TypeVariableName("out QueryColumn"))

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
        val type = legalTypes[value]
        if (type === null) {
            illegalValueWarning(key, value)
            return
        }
        fnName = value
        _value = type
    }
}