package entity

import kotlin.math.abs

class InitClass {
    var id: Int = -114514
        get() = abs(field)
        set(value) {
            require(value > 0) {
                "id must be greater than 0"
            }
            println("setter")
            field = value
        }

    fun fn() = id

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println(InitClass().fn())
        }
    }
}