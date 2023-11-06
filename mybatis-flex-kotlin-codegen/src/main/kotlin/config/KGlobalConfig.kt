package com.mybatisflex.kotlin.ksp.config

import com.mybatisflex.codegen.config.GlobalConfig
import com.mybatisflex.codegen.config.JavadocConfig

class KGlobalConfig : GlobalConfig() {
    @Deprecated("", level = DeprecationLevel.HIDDEN)
    override fun getJavadocConfig(): JavadocConfig {
        return super.getJavadocConfig()
    }
}