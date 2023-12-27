package com.mybatisflex.kotlin.ksp.internal.gen.visitor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.mybatisflex.kotlin.ksp.internal.gen.obj.ObjectGenerator
import com.mybatisflex.kotlin.ksp.options

internal class TableDefVisitor : KSVisitorVoid() {
    // 需要生成代码的类
    val generator by lazy {
        ObjectGenerator()
    }

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        val isGenerateObject = options["flex.generate.object"]
        if (isGenerateObject === null || isGenerateObject.toBoolean()) {
            generator.generate(classDeclaration)
        }
    }
}