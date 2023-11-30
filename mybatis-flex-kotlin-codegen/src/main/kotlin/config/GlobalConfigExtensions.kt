package com.mybatisflex.kotlin.codegen.config

import com.mybatisflex.codegen.config.GlobalConfig
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

private val configsExtensions = ConcurrentHashMap<GlobalConfig, KConfig>()

val GlobalConfig.kEntityConfig
    get() = configsExtensions.getOrPut(this, ::KConfig).entityConfig

fun main(): Unit = runBlocking {
    launch {
        val res = async {
            delay(1000)
            println("await: ${Thread.currentThread().name}")
            114514
        }
        println(res.await())
    }
    launch {
        println("mainBlock: ${Thread.currentThread().name}")
    }
}