package com.mybatisflex.kotlin.ksp.internal.gen.visitor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.mybatisflex.kotlin.ksp.internal.gen.mapper.MapperGenerator

class MapperVisitor(baseMapper: KSClassDeclaration) : KSVisitorVoid() {
    private val mapperGenerator = MapperGenerator(baseMapper)

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        mapperGenerator(classDeclaration)
    }
}