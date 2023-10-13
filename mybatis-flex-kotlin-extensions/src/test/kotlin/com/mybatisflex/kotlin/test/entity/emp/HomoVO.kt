package com.mybatisflex.kotlin.test.entity.emp

import com.mybatisflex.annotation.Id
import com.mybatisflex.annotation.Table
import java.util.*

@Table("homo")
data class HomoVO(
    @Id var id: Int = -1,
    var userName: String? = null,
    var age: Int? = null,
    var birthday: Date? = null,
)

