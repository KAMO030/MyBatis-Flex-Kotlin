/*
 *  Copyright (c) 2022-2023, Mybatis-Flex-Kotlin (837080904@qq.com).
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
package com.mybatisflex.kotlin.scope

import com.mybatisflex.core.MybatisFlexBootstrap
import javax.sql.DataSource

class BootstrapScope(private val instant: MybatisFlexBootstrap = MybatisFlexBootstrap.getInstance()) {

    fun dataSources(dataSourceScope: DataSourceScope.() -> Unit) =
        dataSourceScope(DataSourceScope(instant))


    operator fun <T> Class<T>.unaryPlus(): MybatisFlexBootstrap =
        instant.addMapper(this)

    operator fun DataSource.unaryPlus(): MybatisFlexBootstrap =
        instant.setDataSource(this)

    infix fun String.of(dataSource: DataSource): MybatisFlexBootstrap =
        instant.setDataSource(this, dataSource)

}

class DataSourceScope(private val bootstrap: MybatisFlexBootstrap) {


    fun dataSource(dataSourceKey: String, dataSource: DataSource) =
        bootstrap.addDataSource(dataSourceKey, dataSource)

}


inline fun buildBootstrap(
    instant: MybatisFlexBootstrap = MybatisFlexBootstrap.getInstance(),
    scope: BootstrapScope.(MybatisFlexBootstrap) -> Unit
): MybatisFlexBootstrap =
    instant.also { BootstrapScope(it).scope(it) }






