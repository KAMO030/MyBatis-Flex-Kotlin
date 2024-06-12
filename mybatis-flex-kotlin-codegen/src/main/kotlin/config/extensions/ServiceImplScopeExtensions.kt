package com.mybatisflex.kotlin.codegen.config.extensions

import com.mybatisflex.kotlin.codegen.config.ScopedTableOptions
import com.mybatisflex.kotlin.codegen.config.ServiceImplScope
import com.mybatisflex.kotlin.codegen.config.ServiceScope
import com.mybatisflex.kotlin.codegen.config.TableConfiguration
import com.squareup.kotlinpoet.ClassName

fun ScopedTableOptions<ServiceImplScope>.default(configuration: TableConfiguration) {
    val service = configuration.optionsMap[ServiceScope.scopeName] ?: return
    transformType { tm, builder ->
        val reified = ClassName(service.packageName, service.tableNameMapper(tm))
        builder.addSuperinterface(reified)
    }
}