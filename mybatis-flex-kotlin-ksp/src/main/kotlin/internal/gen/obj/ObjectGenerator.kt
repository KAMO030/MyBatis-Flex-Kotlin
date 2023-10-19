package internal.gen.obj

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import internal.util.*

class ObjectGenerator {
    operator fun invoke(it: KSClassDeclaration): PropertySpec.Builder {
        val tableClassName = it.tableClassName
        val generateClassName = ClassName("${it.packageName.asString()}.table", it.tableClassName)  // 将要生成的类的ClassName
        val fileSpec = FileSpec.builder(
            "${it.packageName.asString()}.table",
            tableClassName
        )
        val list = generateProperties(it.getAllProperties())
        val instanceProperty = it.instanceProperty(generateClassName)
        fileSpec.addType(
            TypeSpec.objectBuilder(tableClassName)
                .addProperties(list)
                .addProperty(allColumns.build())
                .addProperty(getDefaultColumns(it.getAllProperties()).build())
                .addKdoc(
                    """
                This file is automatically generated by the ksp of mybatis-flex, do not modify this file.
                """.trimIndent()
                )
                .superclass(TABLE_DEF)
                .addSuperclassConstructorParameter(
                    """
                "${it.scheme}", "${it.tableName}"
            """.trimIndent()
                )
                .addFunction(invokeFunction().build())
                .addProperty(instanceProperty.build())
                .build()
        )
        fileSpec
            .suppressDefault()
            .build()
            .write()
        return it.instanceProperty(generateClassName)
    }

    private fun generateProperties(sequence: Sequence<KSPropertyDeclaration>): List<PropertySpec> = sequence.map {
        it.propertySpecBuilder.build()
    }.toList()

}