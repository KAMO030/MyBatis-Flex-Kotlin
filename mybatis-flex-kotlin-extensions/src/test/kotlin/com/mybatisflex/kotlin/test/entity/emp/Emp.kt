package com.mybatisflex.kotlin.test.entity.emp

import com.mybatisflex.annotation.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Table("emp")
data class Emp(
    /**
     * id号
     * homo 114514
     */
    var id: Int = -1,
    var username: String? = null,
    var password: Int = -1,
    var name: String? = null,
    var gender: Int = -1,
    var image: String? = null,
    var job: Int = -1,
    var entrydate: LocalDate? = null,
    var deptId: Int = -1,
    var createTime: LocalDateTime? = null,
    var updateTime: LocalDateTime? = null,
)