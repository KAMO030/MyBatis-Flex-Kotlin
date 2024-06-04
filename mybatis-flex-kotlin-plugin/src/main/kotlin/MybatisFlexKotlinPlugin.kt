/*
 *  Copyright (c) 2023-Present, Mybatis-Flex-Kotlin (kamosama@qq.com).
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.mybatisflex.kotlin.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
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

    }

}