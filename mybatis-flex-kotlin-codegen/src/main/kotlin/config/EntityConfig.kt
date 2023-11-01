package com.mybatisflex.kotlin.ksp.config

import java.io.Serializable
import kotlin.reflect.KClass
import kotlin.reflect.KType

class EntityConfig : Serializable {
    var superClass: KType? = null

    val implInterfaces: MutableList<KType> = ArrayList()

    val annotations: MutableList<KClass<out Annotation>> = ArrayList()

    var classPrefix: String = ""

    var classSuffix: String = ""

    var overrideAble = false
}
