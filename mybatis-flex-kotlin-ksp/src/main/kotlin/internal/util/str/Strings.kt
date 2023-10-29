package com.mybatisflex.kotlin.ksp.internal.util.str

import com.mybatisflex.kotlin.ksp.internal.config.flex.PropertiesNameStyle.*
import com.mybatisflex.kotlin.ksp.internal.config.flex.TableDefIgnoreEntitySuffixes
import com.mybatisflex.kotlin.ksp.internal.config.flex.TableDefPropertiesNameStyle

fun String.asPropertyName(): String = when (TableDefPropertiesNameStyle.value) {
    upperCase -> toUpperCase()
    lowerCase -> toLowerCase()
    upperCamelCase -> toUpperCamelCase()
    lowerCamelCase -> toLowerCamelCase()
    original -> this
}

fun String.toUpperCase(): String {
    if (isBlank()) return ""
    val sb = StringBuilder()
    forEachIndexed { index, c ->
        if (c.isUpperCase()) {
            if (index != 0) {
                sb.append('_')
            }
            sb.append(c)
        } else {
            sb.append(c.uppercaseChar())
        }
    }
    return sb.toString()
}

fun String.toLowerCase(): String = toUpperCase().lowercase()

fun String.toUpperCamelCase(): String = replaceFirstChar(Char::uppercaseChar)

fun String.toLowerCamelCase(): String = replaceFirstChar(Char::lowercaseChar)

val String.filterInstanceSuffix: String
    get() = TableDefIgnoreEntitySuffixes.value.find(::endsWith)?.let(::removeSuffix) ?: this


fun String.asColumnName(toUnderLine: Boolean): String = if (toUnderLine) {
    toLowerCase()
} else {
    this
}