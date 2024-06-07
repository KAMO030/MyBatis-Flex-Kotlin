package com.mybatisflex.kotlin.codegen.config.extensions

import com.mybatisflex.kotlin.codegen.config.ScopeTableOptions
import com.mybatisflex.kotlin.codegen.config.ThreeTierScope
import com.squareup.kotlinpoet.KModifier

fun ScopeTableOptions<ThreeTierScope.Entity>.dataclass() {
    transformType { _, builder ->
        builder.addModifiers(KModifier.DATA)
    }
}
