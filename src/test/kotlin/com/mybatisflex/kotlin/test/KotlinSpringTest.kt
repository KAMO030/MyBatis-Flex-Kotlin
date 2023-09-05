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
package com.mybatisflex.kotlin.test

import com.mybatisflex.kotlin.test.config.AppConfig
import com.mybatisflex.kotlin.test.mapper.AccountMapper
import org.junit.jupiter.api.extension.ExtendWith


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.Test
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [AppConfig::class])
open class KotlinSpringTest  {
	@Autowired
	lateinit var accountMapper: AccountMapper

	@Test
	fun testSelectByQuery() {
		val accounts = accountMapper.findByAge(18,2,1)
		accounts.forEach(::println)
	}


}

