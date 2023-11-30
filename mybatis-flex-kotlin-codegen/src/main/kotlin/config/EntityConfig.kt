package com.mybatisflex.kotlin.codegen.config
import java.io.Serializable
import kotlin.reflect.KType

class EntityConfig : Serializable {
    var superClass: KType? = null

    val implInterfaces: MutableList<KType> = ArrayList()

    val annotations: MutableList<Annotation> = ArrayList()

    var classPrefix: String = ""

    var classSuffix: String = ""

    var overrideAble = false

    val importText: String = buildString {
        annotations.forEach {
            append("import ")
            append(it::class.qualifiedName)
            append("\n")
        }
    }

    companion object {
        @JvmStatic
        val DEFAULT = EntityConfig().apply {
            annotations += Deprecated("homo114514")
        }
    }
}
