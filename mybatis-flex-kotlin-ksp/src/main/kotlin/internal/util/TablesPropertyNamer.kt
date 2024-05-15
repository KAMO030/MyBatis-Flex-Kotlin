package com.mybatisflex.kotlin.ksp.internal.util

import com.squareup.kotlinpoet.PropertySpec

class TablesPropertyNamer {
    private val timesMap = HashMap<String, Int>()

    private fun getPropertyName(propertySpec: PropertySpec): String {
        val name = propertySpec.name
        var time = timesMap[name]
        return if (time != null) {
            timesMap[name] = ++time
            "${name}$$time"
        } else {
            timesMap[name] = 0
            name
        }
    }

    fun renameProperties(propertySpecs: List<PropertySpec>): List<PropertySpec> = buildList {
        propertySpecs.forEach {
            this += it.toBuilder(name = getPropertyName(it)).build()
        }
    }
}