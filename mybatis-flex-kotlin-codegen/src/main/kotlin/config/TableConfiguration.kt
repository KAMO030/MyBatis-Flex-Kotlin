package com.mybatisflex.kotlin.codegen.config

import com.mybatisflex.kotlin.codegen.annotation.GeneratorDsl

/**
 * 指示一个表生成什么东西。其下每一个 [TableOptions] 都表示一个产物，并且可以独立配置。
 */
@GeneratorDsl
class TableConfiguration {
    @PublishedApi
    internal val optionsMap: MutableMap<String, TableOptions> = mutableMapOf()

    var rootSourceDir: String = GenerateDispatcher.rootSourceDir

    var basePackage: String = GenerateDispatcher.basePackage

    inline fun registerOption(
        optionName: String,
        option: TableOptions = TableOptionsImpl(optionName, this),
        initOption: TableOptions.() -> Unit = {}
    ) {
        this.optionsMap[optionName] = option.apply(initOption)
    }

    @JvmName("registerOptionWithType")
    inline fun <T : TableOptions> registerOption(
        option: T,
        optionName: String = option.optionName,
        initOption: T.() -> Unit = {}
    ) {
        this.optionsMap[optionName] = option.apply(initOption)
    }

    inline fun TableConfiguration.getOrRegister(
        optionName: String,
        register: (String) -> TableOptions = { TableOptionsImpl(it, this) }
    ): TableOptions = optionsMap.getOrPut(optionName) { register(optionName) }

    fun clearCache() {
        optionsMap.clear()
    }
}
