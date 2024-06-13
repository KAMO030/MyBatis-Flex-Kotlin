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

inline fun ScopedTableOptions<ServiceScope>.default() {

}

@PublishedApi
internal inline fun TableConfiguration.dispatcher(
    crossinline configure: ScopedTableOptions<ServiceScope>.() -> Unit
): ScopedTableOptions<ServiceScope>.() -> Unit = {
    builderTransformer {
        this@dispatcher.optionsMap[EntityScope.scopeName]?.let { entityOptions ->
            transformType { tm, builder ->
                val reified = ClassName(entityOptions.packageName, entityOptions.tableNameMapper(tm))
                builder.addSuperinterface(typeNameOf<IService<Any>>().replaceTypeName(ANY, reified))
            }
        }
        configure()
        columnMetadataTransformer = { emptySequence() }
    }
}
