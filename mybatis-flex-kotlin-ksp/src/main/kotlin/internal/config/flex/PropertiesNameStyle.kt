@file:Suppress("EnumEntryName")

package com.mybatisflex.kotlin.ksp.internal.config.flex

/**
 * 生成的代码的属性风格。
 *
 * [upperCase]：大写 + 下划线，例如：USER_NAME
 *
 * [lowerCase]：小写 + 下划线，例如：user_name
 *
 * [upperCamelCase]：首字母大写的驼峰命名，例如：UserName
 *
 * [lowerCamelCase]：首字母小写的驼峰命名，例如：userName
 *
 * [original]：原始属性名，不做任何修改。
 */
enum class PropertiesNameStyle {
    /**
     * 大写 + 下划线，例如：USER_NAME
     */
    upperCase,

    /**
     * 小写 + 下划线，例如：user_name
     */
    lowerCase,

    /**
     * 首字母大写的驼峰命名，例如：UserName
     */
    upperCamelCase,

    /**
     * 首字母小写的驼峰命名，例如：userName
     */
    lowerCamelCase,

    /**
     * 原始的属性名，不做任何更改。
     */
    original
}