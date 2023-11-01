package com.mybatisflex.kotlin.ksp.internal.util.str

import com.mybatisflex.kotlin.ksp.internal.config.flex.PropertiesNameStyle.*
import com.mybatisflex.kotlin.ksp.internal.config.flex.TableDefIgnoreEntitySuffixes
import com.mybatisflex.kotlin.ksp.internal.config.flex.TableDefPropertiesNameStyle

fun String.asPropertyName(): String = when (TableDefPropertiesNameStyle.value) {
    upperCase -> toUpperSnakeCase()
    lowerCase -> toLowerSnakeCase()
    upperCamelCase -> toUpperCamelCase()
    lowerCamelCase -> toLowerCamelCase()
    original -> this
}

fun String.toUpperSnakeCase(): String = mapIndexed { index: Int, c: Char ->
    when {
        c.isUpperCase() -> if (index != 0) "_$c" else c
        else -> c.uppercaseChar()
    }
}.joinToString("")


fun String.toLowerSnakeCase(): String = toUpperSnakeCase().lowercase()

fun String.toUpperCamelCase(): String = replaceFirstChar(Char::uppercaseChar)

fun String.toLowerCamelCase(): String = replaceFirstChar(Char::lowercaseChar)

fun String.filterInstanceSuffix(): String =
    TableDefIgnoreEntitySuffixes.value.find(::endsWith)?.let(::removeSuffix) ?: this

fun String.asColumnName(toUnderLine: Boolean): String = if (toUnderLine) {
    toLowerSnakeCase()
} else {
    this
}