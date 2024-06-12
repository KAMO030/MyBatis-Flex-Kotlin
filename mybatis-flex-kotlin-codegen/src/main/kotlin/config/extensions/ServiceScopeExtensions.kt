package com.mybatisflex.kotlin.codegen.config.extensions

import com.mybatisflex.core.service.IService
import com.mybatisflex.kotlin.codegen.config.EntityScope
import com.mybatisflex.kotlin.codegen.config.ScopedTableOptions
import com.mybatisflex.kotlin.codegen.config.ServiceScope
import com.mybatisflex.kotlin.codegen.config.TableConfiguration
import com.mybatisflex.kotlin.codegen.internal.replaceTypeName
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.typeNameOf

fun ScopedTableOptions<ServiceScope>.default(configuration: TableConfiguration) {
    val tableOptions = configuration.optionsMap[EntityScope.scopeName] ?: return
    transformType { tm, builder ->
        val reified = ClassName(tableOptions.packageName, tableOptions.tableNameMapper(tm))
        builder.addSuperinterface(typeNameOf<IService<Any>>().replaceTypeName(ANY, reified))
    }
}