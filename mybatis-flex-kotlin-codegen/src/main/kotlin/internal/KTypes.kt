package com.mybatisflex.kotlin.codegen.internal

import kotlin.reflect.KType


/**
 * 去除全名中的包名，只携带类名。
 * 例如 `kotlin.String` 变为 `String`，
 * `kotlin.collections.List<kotlin.String>` 变为 `List<String>`，
 *
 * `java.util.HashMap<kotlin.collections.List<kotlin.String>, kotlin.collections.Iterable<kotlin.Int>>`
 * 变为 `HashMap<List<String>, Iterable<Int>>`。
 *
 *
 */
internal fun KType.asTypedString(): String {
    val name = toString()
    val regex = Regex("""\w+\.""")
    return name.replace(regex, "")
}

/**
 * 将全名中的全名提取出来。
 *
 * `typeOf<HashMap<List<String>, ArrayList<in String>>>()`
 * 变为 `
 * [java.util.HashMap,
 * kotlin.collections.List,
 * kotlin.String,
 * java.util.ArrayList,
 * kotlin.String]`。
 */
internal fun KType.asQualifiedNames(): List<String> {
    val regex = Regex("""(\w+\.?)+""")
    // 用于排除 in 和 out 的逆变协变修饰符。
    val regex2 = Regex("""(in|out)\s+""")
    val res = toString().replace(regex2, "")
    return regex.findAll(res).mapTo(ArrayList()) { it.value }
}