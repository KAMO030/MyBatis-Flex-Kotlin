package entity

import java.util.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

class LateEmp {
    var id: Int by ReadOnlyEntity()

    var name: String by ReadOnlyEntity()

    var age: Int by ReadOnlyEntity()

    var salary: Double by ReadOnlyEntity()

    override fun toString() = StringJoiner(", ", "LateEmp(", ")").apply {
        LateEmp::class.declaredMemberProperties.forEach {
            it.isAccessible = true
            val delegate = it.getDelegate(this@LateEmp)
            if (delegate is ReadOnlyEntity<*>) {
                if (delegate.isInitialized) {
                    add("${it.name}=${it.get(this@LateEmp)}")
                } else {
                    add("${it.name} is not initialized yet")
                }
            }
        }
    }.toString()

    val initializedProperties
        get() = LateEmp::class.declaredMemberProperties.filter {
            it.isAccessible = true
            val delegate = it.getDelegate(this)
            delegate is ReadOnlyEntity<*> && delegate.isInitialized
        }

    val String.isInt
        get() = this.all {
            it.isDigit()
        }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

        }
    }
}