package com.mybatisflex.kotlin.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.noarg.gradle.NoArgExtension

class MybatisFlexKotlinPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        project.plugins.apply("org.jetbrains.kotlin.plugin.noarg")
        project.plugins.apply("org.jetbrains.kotlin.plugin.allopen")

        project.extensions.configure<NoArgExtension> {
            annotation("com.mybatisflex.annotation.Table")
        }

        project.extensions.configure<AllOpenExtension> {
            annotation("com.mybatisflex.annotation.Table")
        }

        project.tasks.withType(KotlinCompile::class.java) {
            kotlinOptions {
                // 允许 Java 不重写 Kotlin 接口中非抽象方法
                freeCompilerArgs += "-Xjvm-default=all"
            }
        }
    }

}