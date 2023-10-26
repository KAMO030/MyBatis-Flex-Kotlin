package com.mybatisflex.kotlin.example


import com.mybatisflex.kotlin.example.config.AppConfig
import com.mybatisflex.kotlin.example.mapper.AccountMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [AppConfig::class])
open class KotlinSpringExample {
    @Autowired
    lateinit var accountMapper: AccountMapper

    @Test
    fun testSelectByQuery() {
        val accounts = accountMapper.findByAge(18, 2, 1)
        accounts.forEach(::println)
    }

}