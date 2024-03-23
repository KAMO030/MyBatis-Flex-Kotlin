package com.mybatisflex.kotlin.extensions.condition.annotation

@RequiresOptIn("EmptyCondition 单例仍在测试，请避免将其使用至生产项目中。" +
        "如若遇到问题，请改用 QueryCondition.createEmpty() 方法。")
annotation class ExperimentalEmptyCondition
