package entity

import com.mybatisflex.annotation.Column
import com.mybatisflex.annotation.Id
import com.mybatisflex.annotation.RelationOneToMany
import com.mybatisflex.annotation.Table
import java.time.LocalDateTime
import java.util.*

@Table("emp")
data class Emp(
    @Id
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
    @Column(ignore = true)
    @RelationOneToMany(selfField = "deptId", targetField = "id")
    var dept: MutableList<Dept> = mutableListOf(),
)