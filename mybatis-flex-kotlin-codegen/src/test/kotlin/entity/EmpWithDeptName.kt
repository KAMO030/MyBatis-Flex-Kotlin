package entity

import com.mybatisflex.annotation.RelationOneToOne
import java.time.LocalDateTime
import java.util.*

data class EmpWithDeptName(
    var id: Int,
    var username: String,
    var password: String,
    var name: String,
    var gender: Int,
    var image: String?,
    var job: Int?,
    var entrydate: Date,
    var deptId: Int?,
    var createTime: LocalDateTime,
    var updateTime: LocalDateTime,
    @RelationOneToOne(selfField = "id", targetField = "deptId")
    var deptName: String,
)