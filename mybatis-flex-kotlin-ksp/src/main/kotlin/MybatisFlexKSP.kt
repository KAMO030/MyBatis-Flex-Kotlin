package com.mybatisflex.kotlin.ksp

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.mybatisflex.annotation.Table
import com.mybatisflex.kotlin.ksp.internal.config.flex.AllInTablesEnable
import com.mybatisflex.kotlin.ksp.internal.config.flex.Enable
import com.mybatisflex.kotlin.ksp.internal.config.flex.MapperBaseClass
import com.mybatisflex.kotlin.ksp.internal.config.flex.MapperGenerateEnable
import com.mybatisflex.kotlin.ksp.internal.gen.tables.TablesGenerator
import com.mybatisflex.kotlin.ksp.internal.gen.visitor.MapperVisitor
import com.mybatisflex.kotlin.ksp.internal.gen.visitor.TableDefVisitor
import com.mybatisflex.kotlin.ksp.internal.util.anno.table
import com.mybatisflex.kotlin.ksp.internal.util.file.flexConfigs

internal class MybatisFlexKSP : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (options["flex.ksp.enable"]?.toBoolean() == false) return emptyList()
        initConfigs(flexConfigs)
        if (!Enable.value) return emptyList()
        logger.warn("mybatis flex kotlin symbol processor run start...")
        generate(resolver)
        return emptyList()
    }

    private fun generate(resolver: Resolver) {
        val tableDefVisitor = TableDefVisitor()
        val seq = resolver  // 获取需要生成 TableDef 的类
            .getSymbolsWithAnnotation(Table::class.java.canonicalName)
            .filterIsInstance<KSClassDeclaration>()
            .filter {
                it.classKind === ClassKind.CLASS
            }.filter {
                it.validate()
            }

        seq.forEach {
            it.accept(tableDefVisitor, Unit)  // 生成 TableDef
        }

        if (AllInTablesEnable.value) {  // 生成 Tables 类
            val generator = tableDefVisitor.generator
            TablesGenerator(generator.instancePropertySpecs, *seq.mapNotNull { it.containingFile }.toList().toTypedArray()).generate()
        }

        if (MapperGenerateEnable.value) {  // 生成 Mapper 接口
            val mapperVisitor = MapperVisitor(
                resolver.getClassDeclarationByName(MapperBaseClass.value) ?:
                resolver.getClassDeclarationByName(MapperBaseClass.BASE_MAPPER)!!
            )
            seq.filter {
                it.table.mapperGenerateEnable
            }.forEach {
                it.accept(mapperVisitor, Unit)
            }
        }
    }

    // 用于打印 options 中的内容，仅调试时使用。
    @Suppress("unused")
    private fun logOptions() = options.forEach { (key, value) ->
        logger.warn("options:  $key = $value")
    }
}