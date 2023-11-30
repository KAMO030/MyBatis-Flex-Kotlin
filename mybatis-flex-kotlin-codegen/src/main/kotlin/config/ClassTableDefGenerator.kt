package com.mybatisflex.kotlin.codegen.config

import com.mybatisflex.codegen.config.GlobalConfig
import com.mybatisflex.codegen.entity.Table
import com.mybatisflex.codegen.generator.IGenerator
import java.io.File

class ClassTableDefGenerator : IGenerator {
    @get:JvmName("getTemplatePathInKotlin")
    @set:JvmName("setTemplatePathInKotlin")
    var templatePath: String = "templates/enjoy/classTableDef.tpl"

    override fun generate(table: Table, globalConfig: GlobalConfig) {

        val packageConfig = globalConfig.packageConfig
        val tableDefConfig = globalConfig.tableDefConfig

        val entityPackage = packageConfig.tableDefPackage.replace(".", "/")

        val file = File(packageConfig.sourceDir, "$entityPackage/${table.buildTableDefClassName()}.kt")

        if (file.exists() && !tableDefConfig.isOverwriteEnable) {
            return
        }

        globalConfig.templateConfig.template.generate(
            mapOf(
                "table" to table,
                "tableDefConfig" to tableDefConfig,
                "packageConfig" to packageConfig,
                "javadocConfig" to globalConfig.javadocConfig
            ),
            templatePath,
            file
        )
    }

    @Deprecated("", ReplaceWith("templatePath"), DeprecationLevel.HIDDEN)
    override fun getTemplatePath(): String = templatePath

    @Deprecated("", ReplaceWith("templatePath"), DeprecationLevel.HIDDEN)
    override fun setTemplatePath(templatePath: String) {
        this.templatePath = templatePath
    }
}