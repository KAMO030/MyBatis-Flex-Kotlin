package com.mybatisflex.kotlin.ksp.internal.gen.visitor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.mybatisflex.kotlin.ksp.internal.config.flex.AllInTablesEnable
import com.mybatisflex.kotlin.ksp.internal.config.flex.AllInTablesPackage
import com.mybatisflex.kotlin.ksp.internal.gen.obj.ObjectGenerator
import com.mybatisflex.kotlin.ksp.internal.gen.tables.TablesGenerator
import com.mybatisflex.kotlin.ksp.logger
import com.mybatisflex.kotlin.ksp.options

internal class TableDefVisitor : KSVisitorVoid() {
    // 需要生成代码的类
    val objectGenerator by lazy {
        ObjectGenerator()
    }

    val tablesGenerator by lazy {
        TablesGenerator()
    }

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        val isGenerateObject = options["flex.generate.object"]
        if (isGenerateObject === null || isGenerateObject.toBoolean()) {
            val propBuilder = objectGenerator(classDeclaration)
            if (AllInTablesEnable.value) {
                AllInTablesPackage.value ?: return logger.warn("指定了生成类 Tables 但没有指定生成在哪个包下，不予生成。")
                tablesGenerator.properties += propBuilder.build()
            }
        }
    }
}