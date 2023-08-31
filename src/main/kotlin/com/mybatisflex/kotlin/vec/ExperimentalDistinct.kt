package com.mybatisflex.kotlin.vec

/**
 * 去重功能目前是实验性的。我们目前并没有什么好的方案去替代。
 */
@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
annotation class ExperimentalDistinct
