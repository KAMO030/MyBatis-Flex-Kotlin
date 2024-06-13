package com.mybatisflex.kotlin.codegen.config.extensions

import com.mybatisflex.kotlin.codegen.config.ScopedTableOptions
import com.mybatisflex.kotlin.codegen.config.ServiceImplScope
import com.mybatisflex.kotlin.codegen.config.ServiceScope
import com.mybatisflex.kotlin.codegen.config.TableConfiguration
import com.squareup.kotlinpoet.ClassName

fun ScopedTableOptions<ServiceImplScope>.default() {

}

inline fun TableConfiguration.dispatcher(
    crossinline configure: ScopedTableOptions<ServiceImplScope>.() -> Unit
): ScopedTableOptions<ServiceImplScope>.() -> Unit = {
    optionsMap[ServiceScope.scopeName]?.let { serviceOptions ->
        transformType { tm, builder ->
            val reified = ClassName(serviceOptions.packageName, serviceOptions.tableNameMapper(tm))
            builder.addSuperinterface(reified)
        }
    }
    configure()
}