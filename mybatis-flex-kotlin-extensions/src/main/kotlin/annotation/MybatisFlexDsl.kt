package com.mybatisflex.kotlin.annotation

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
@DslMarker
annotation class MybatisFlexDsl
