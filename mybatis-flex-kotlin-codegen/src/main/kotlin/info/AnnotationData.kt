package com.mybatisflex.kotlin.codegen.info

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters

data class AnnotationData(
    val annotation: KClass<out Annotation>,
    val parameters: Map<String, String>
) {
    override fun toString() = buildString {
        append("@")
        append(annotation.simpleName)
        if (parameters.isEmpty()) {
            return@buildString
        }
        append("(")
        val iter = parameters.iterator()
        val legalParameterName = annotation.primaryConstructor!!.valueParameters.mapTo(HashSet()) { it.name }
        while (iter.hasNext()) {
            val (key, value) = iter.next()
            if (key !in legalParameterName) {
                throw IllegalArgumentException("$annotation does not have a parameter named `$key`, legal name of $annotation: $legalParameterName")
            }
            append(key)
            append(" = ")
            append(value.takeIf { it.isNotEmpty() }  ?: "\"\"")
            if (iter.hasNext()) append(", ")
        }
        append(")")
    }

}