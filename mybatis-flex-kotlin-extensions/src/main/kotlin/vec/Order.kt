/*
 *  Copyright (c) 2023-Present, Mybatis-Flex-Kotlin (837080904@qq.com).
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
package com.mybatisflex.kotlin.vec

import com.mybatisflex.core.constant.SqlConsts

/**
 * 用于表示排序的顺序。
 * @param sql 对应的 SQL 语句。
 * @author CloudPlayer
 */
enum class Order(val sql: String) {
    /**
     * 倒序。
     * @author CloudPlayer
     */
    DESC(SqlConsts.DESC),

    /**
     * 顺序。
     * @author CloudPlayer
     */
    ASC(SqlConsts.ASC)
}