package com.mybatisflex.kotlin.codegen.config.extensions

import com.mybatisflex.kotlin.codegen.config.ClassOptionScope
import com.mybatisflex.kotlin.codegen.config.ScopedTableOptions
import com.squareup.kotlinpoet.KModifier

fun ScopedTableOptions<ClassOptionScope>.dataclass() {
    transformType { _, builder ->
        builder.addModifiers(KModifier.DATA)
    }
}
