package com.mybatisflex.kotlin.codegen.metadata

import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

sealed interface PropertyType {
    @JvmInline
    value class KTypeType(val kType: KType) : PropertyType {
        override fun asTypeName() = kType.asTypeName()
    }

    @JvmInline
    value class ClassType(val clazz: Class<*>) : PropertyType {
        override fun asTypeName() = clazz.kotlin.asTypeName()
    }

    @JvmInline
    value class KClassType(val kClass: KClass<*>) : PropertyType {
        override fun asTypeName(): TypeName = kClass.asTypeName()
    }

    fun asTypeName(): TypeName

    companion object {
        @JvmStatic
        fun of(kType: KType): PropertyType = KTypeType(kType)

        @JvmStatic
        fun of(clazz: Class<*>): PropertyType = ClassType(clazz)

        @JvmStatic
        fun of(kClass: KClass<*>): PropertyType = KClassType(kClass)

        @JvmStatic
        fun of(type: String): PropertyType = ClassType(Class.forName(type))

        inline fun <reified T : Any> of(): PropertyType = KTypeType(typeOf<T>())
    }
}
