package com.mybatisflex.kotlin.codegen.internal

import com.mybatisflex.annotation.InsertListener
import com.mybatisflex.annotation.SetListener
import com.mybatisflex.annotation.Table
import com.mybatisflex.annotation.UpdateListener
import com.mybatisflex.kotlin.codegen.config.GenerateDispatcher
import com.mybatisflex.kotlin.codegen.config.TableConfiguration
import com.mybatisflex.kotlin.codegen.metadata.TableMetadata
import kotlin.reflect.KClass

fun String.asCamelCase(): String = buildString {
    var prevChar = '!'
    for (it in this@asCamelCase) {
        if (it == '_') {
            prevChar = '_'
            continue
        }
        if (prevChar == '_') {
            append(it.uppercaseChar())
        } else {
            append(it)
        }
        prevChar = it
    }
}

fun String.asClassName(): String = asCamelCase().replaceFirstChar(Char::uppercaseChar)

val TableMetadata.configuration: TableConfiguration
    get() = with(GenerateDispatcher) {
        specificConfiguration[tableName] ?: globalTableConfiguration
    }

fun Table(
    value: String,
    schema: String = "",
    camelToUnderline: Boolean = true,
    dataSource: String = "",
    comment: String = "",
    onInsert: Array<KClass<out InsertListener>> = emptyArray(),
    onUpdate: Array<KClass<out UpdateListener>> = emptyArray(),
    onSet: Array<KClass<out SetListener>> = emptyArray(),
    mapperGenerateEnable: Boolean = true,
): Table = Table(
    value = value,
    schema = schema,
    camelToUnderline = camelToUnderline,
    dataSource = dataSource,
    comment = comment,
    onInsert = onInsert,
    onUpdate = onUpdate,
    onSet = onSet,
    mapperGenerateEnable = mapperGenerateEnable
)
