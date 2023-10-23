package com.mybatisflex.kotlin.internal.config.ksp

import com.mybatisflex.kotlin.internal.config.Configuration

/**
 * mybatis-flex.config 中，专门配置 ksp 的相关配置。
 *
 * 这种配置的键多半是 `ksp` 以开头的。
 *
 * @author CloudPlayer
 */
internal sealed interface KspConfiguration<out T> : Configuration<T> {
    override val key: String

    override val value: T

    /**
     * 使用字符串进行初始化 [value] 的函数。在此函数中，你应当保证其值合理时在调用此方法后 value 会发生变化。
     * @param value 从配置文件中读取到的值。
     * @author CloudPlayer
     */
    fun initValue(value: String)

    companion object {
        /**
         * ksp 对应的所有配置类。这些类均是单例。
         *
         * @author CloudPlayer
         */
        val configs by lazy {
            KspConfiguration::class.sealedSubclasses.map {
                requireNotNull(it.objectInstance) {
                    "${it.qualifiedName} is not an object."
                }
            }
        }

        operator fun get(key: String) = configs.find {
            it.key == key
        }
    }
}