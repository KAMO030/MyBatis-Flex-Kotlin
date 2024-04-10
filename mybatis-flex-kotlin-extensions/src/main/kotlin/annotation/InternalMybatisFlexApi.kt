package com.mybatisflex.kotlin.annotation

@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This is an internal MybatisFlex API not intended for public use"
)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class InternalMybatisFlexApi
