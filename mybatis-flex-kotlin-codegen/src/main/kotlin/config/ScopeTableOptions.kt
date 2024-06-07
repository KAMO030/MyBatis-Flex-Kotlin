package com.mybatisflex.kotlin.codegen.config

open class ScopeTableOptions<Scope>(
    optionName: String,
    rootSourceDir: String,
    basePackage: String = "",
) : TableOptions(
    optionName,
    rootSourceDir,
    basePackage
)