package com.mybatisflex.kotlin.ksp.internal.gen.visitor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.mybatisflex.kotlin.ksp.internal.config.ksp.GenerateType

internal class TableDefVisitor : KSVisitorVoid() {
    // 需要生成代码的类
    val generator = GenerateType.value.tableDefGenerator

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        generator.generate(classDeclaration)
    }
}