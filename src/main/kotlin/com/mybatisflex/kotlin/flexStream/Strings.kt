package com.mybatisflex.kotlin.flexStream

val String.snakeFormat: String
    get() {
        val sb = StringBuilder()
        forEachIndexed { idx, char ->
            if (char.isLowerCase()) sb.append(char)
            else {
                if (idx != 0) sb.append('_')
                sb.append(char.lowercaseChar())
            }
        }
        return sb.toString()
    }