package internal.util.str

import internal.config.flex.PropertiesNameStyle.*
import internal.config.flex.TableDefIgnoreEntitySuffixes
import internal.config.flex.TableDefPropertiesNameStyle
import java.lang.StringBuilder

fun String.asPropertyName(): String = when (TableDefPropertiesNameStyle.value) {
    upperCase -> toUpperCase()
    lowerCase -> toLowerCase()
    upperCamelCase -> toUpperCamelCase()
    lowerCamelCase -> this
}

fun String.toUpperCase(): String {
    val sb = StringBuilder()
    forEachIndexed { index, c ->
        if (c.isUpperCase()) {
            if (index != 0) {
                sb.append('_')
            }
            sb.append(c)
        } else {
            sb.append(c.uppercaseChar())
        }
    }
    return sb.toString()
}

fun String.toLowerCase(): String = toUpperCase().lowercase()

fun String.toUpperCamelCase(): String = replaceFirstChar {
    it.uppercaseChar()
}

val String.filterInstanceSuffix: String
    get() {
        TableDefIgnoreEntitySuffixes.value.forEach {
            if (endsWith(it)) {
                return this.substring(0, this.length - it.length)
            }
        }
        return this
    }


fun String.asColumnName(toUnderLine: Boolean): String = if (toUnderLine) {
    toLowerCase()
} else {
    this
}