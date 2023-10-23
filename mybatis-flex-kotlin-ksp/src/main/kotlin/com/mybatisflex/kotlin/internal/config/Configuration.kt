package com.mybatisflex.kotlin.internal.config

/**
 * 从 mybatis-flex.config 配置文件中读取到的配置。
 * @param T 配置的类型
 * @property key 键。
 * @property value 值。其密封子类均会提供一个默认值。
 * @author CloudPlayer
 */
internal interface Configuration<out T> {
    val key: String

    val value: T

    operator fun component1() = key

    operator fun component2() = value
}
