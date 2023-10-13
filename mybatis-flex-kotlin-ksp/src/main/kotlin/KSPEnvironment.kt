import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import internal.config.flex.MybatisFlexConfiguration
import internal.config.ksp.KspConfiguration
import internal.util.file.isFlexConfigFile
import internal.util.file.properties
import java.io.File

/**
 * 用于初始化KSP环境，将KSP环境中的对象赋值给全局变量，方便后续使用
 *
 * @author CloudPlayer
 */
internal fun initKspEnvironment(environment: SymbolProcessorEnvironment) {
    codeGenerator = environment.codeGenerator
    logger = environment.logger
    options = environment.options
}

internal lateinit var codeGenerator: CodeGenerator

internal lateinit var logger: KSPLogger

internal lateinit var options: Map<String, String>

/**
 * 初始化Flex配置文件对应的单例，使得配置文件中的配置项可以被方便地读取.
 * @param configFile Flex 配置文件，即 mybatis-flex.config.
 */
internal fun initFlexConfigs(configFile: File) {
    if (!configFile.isFlexConfigFile) {
        logger.warn("${configFile.absolutePath} not exist.")
        return
    }
    val properties = configFile.properties
    MybatisFlexConfiguration.configs.forEach {
        val value: String? = properties.getProperty(it.key)
        value?.let(it::initValue)
    }
}

/**
 * 初始化所有 mybatis-flex.config 配置文件。
 *
 * 此方法会遍历所有 mybatis-flex.config 文件，从根项目开始初始化直至子项目。
 *
 * @param configFiles 需要初始化的配置文件列表。
 * @author CloudPlayer
 */
internal fun initConfigs(configFiles: List<File>) {
    for (i in configFiles.lastIndex downTo 0) {
        val value = configFiles[i]
        initFlexConfigs(value)
        initKspConfigs(value)
    }
}

internal fun initKspConfigs(configFile: File) {
    if (!configFile.isFlexConfigFile) {
        logger.warn("${configFile.absolutePath} not exist.")
        return
    }
    val properties = configFile.properties
    KspConfiguration.configs.forEach {
        val value: String? = properties.getProperty(it.key)
        value?.let(it::initValue)
    }
}

/**
 * 当配置文件中存在非法的配置项时，会打印警告信息
 *
 * @author CloudPlayer
 */
internal fun illegalValueWarning(key: String, value: String) {
    logger.warn(
        "Illegal value: `$value` for mybatis-flex configuration key: `$key`," +
                " please check the file `mybatis-flex.config`."
    )
}