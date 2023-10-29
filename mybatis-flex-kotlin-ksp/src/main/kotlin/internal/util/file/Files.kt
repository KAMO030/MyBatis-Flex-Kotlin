package com.mybatisflex.kotlin.ksp.internal.util.file

import com.mybatisflex.kotlin.ksp.internal.config.flex.StopBubbling
import com.mybatisflex.kotlin.ksp.options
import java.io.File
import java.util.*

/**
 * 从配置文件中获取 [Properties] 实例，以解析其中的配置项。
 *
 * @author CloudPlayer
 */
val File.properties: Properties
    inline get() = inputStream().use {
        Properties().apply {
            load(it)
        }
    }

/**
 * 判断此文件是否为 mybatis-flex.config 配置文件。
 *
 * @author CloudPlayer
 */
internal val File.isFlexConfigFile: Boolean
    get() = isFile && name == "mybatis-flex.config"

/**
 * 所有从子项目到根项目的 mybatis-flex.config 配置文件。
 *
 * @author CloudPlayer
 */
internal val flexConfigs: List<File> by lazy {
    val projectPath = options["flex.project.path"]
    val rootPath = options["flex.root.project.path"]
    when {
        projectPath !== null && rootPath !== null -> {
            val projectDirFile =  File(projectPath)
            val rootDirFile = File(rootPath)
            var thisFile = projectDirFile
            val res = ArrayList<File>()
            if (!StopBubbling.value) {  // 如果禁止向上合并，则无需递归
                while (thisFile != rootDirFile) {
                    val file = File(thisFile, "mybatis-flex.config")
                    if (file.isFlexConfigFile) {
                        res += file
                    }
                    thisFile = thisFile.parentFile ?: break
                }
            }

            val file = File(thisFile, "mybatis-flex.config")
            if (file.isFlexConfigFile) {
                res += file
            }
            res
        }
        projectPath !== null && rootPath === null -> listOf(File(projectPath, "mybatis-flex.config"))
        rootPath !== null -> listOf(File(rootPath, "mybatis-flex.config"))
        else -> emptyList()
    }
}
