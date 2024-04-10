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
@file:Suppress("unused")
package com.mybatisflex.kotlin.scope

import com.mybatisflex.core.BaseMapper
import com.mybatisflex.core.FlexConsts
import com.mybatisflex.core.MybatisFlexBootstrap
import com.mybatisflex.kotlin.annotation.MybatisFlexDsl
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.datasource.pooled.PooledDataSource
import org.apache.ibatis.io.ResolverUtil
import org.apache.ibatis.logging.Log
import java.sql.Driver
import java.sql.DriverManager
import javax.sql.DataSource
import kotlin.reflect.KClass


/**
 * Bootstrap构建配置作用域
 * @author KAMOsama
 * @date 2023/8/7
 */
class BootstrapScope(private val instant: MybatisFlexBootstrap = MybatisFlexBootstrap.getInstance()) {


    /**
     * 配置简易内置数据源
     */
    fun defaultPooledDataSources(
        dataSourceKey: String = FlexConsts.NAME,
        dataSourcePropScope: DataSourcePropScope.() -> Unit
    ): MybatisFlexBootstrap = DataSourcePropScope().apply(dataSourcePropScope).run {
        instant.addDataSource(
            dataSourceKey,
            PooledDataSource(
                ((driver?.java) ?: DriverManager.getDrivers().nextElement().javaClass).name,
                url, username, password
            )
        )
    }

    var logImpl: KClass<out Log>
        get() = instant.logImpl.kotlin
        set(v) {
            instant.logImpl = v.java
        }

    /**
     * 扫描包路径自动注册Mapper
     * @param packages 包路径
     * @param needScanAnnotated 是否开启扫描Mapper注解，默认关闭
     * @since 1.0.8
     */
    fun scanPackages(vararg packages: String, needScanAnnotated: Boolean = false) {
        if (packages.isEmpty()) return
        val resolverUtil = ResolverUtil<BaseMapper<*>>()
        resolverUtil.findImplementations(BaseMapper::class.java, *packages)
        if (needScanAnnotated) {
            resolverUtil.findAnnotated(Mapper::class.java, *packages)
        }
        resolverUtil.classes.forEach { instant.addMapper(it) }
    }

    operator fun Class<*>.unaryPlus(): MybatisFlexBootstrap =
        instant.addMapper(this)

    operator fun KClass<*>.unaryPlus(): MybatisFlexBootstrap =
        instant.addMapper(java)

    operator fun DataSource.unaryPlus(): MybatisFlexBootstrap =
        instant.setDataSource(this)

    infix fun String.of(dataSource: DataSource): MybatisFlexBootstrap =
        instant.setDataSource(this, dataSource)

}

class DataSourcePropScope {

    var url: String? = null

    var username: String? = null

    var password: String? = null

    var driver: KClass<Driver>? = null

}

@Deprecated("Use runFlex instead", ReplaceWith("runFlex(instant, scope)"))
inline fun buildBootstrap(
    instant: MybatisFlexBootstrap = MybatisFlexBootstrap.getInstance(),
    scope: BootstrapScope.(MybatisFlexBootstrap) -> Unit
): MybatisFlexBootstrap =
    instant.also { BootstrapScope(it).scope(it) }

/**
 * 启动MybatisFlex
 *
 * @author KAMOsama
 * @since 1.0.5
 */
@MybatisFlexDsl
inline fun runFlex(
    instant: MybatisFlexBootstrap = MybatisFlexBootstrap.getInstance(),
    scope: BootstrapScope.(MybatisFlexBootstrap) -> Unit
): MybatisFlexBootstrap =
    instant.also { BootstrapScope(it).scope(it) }.start()






