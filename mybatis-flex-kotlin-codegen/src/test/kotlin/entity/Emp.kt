package entity

import com.mybatisflex.annotation.Column
import com.mybatisflex.annotation.ColumnAlias
import com.mybatisflex.annotation.Id
import com.mybatisflex.annotation.Table
import java.time.LocalDateTime
import java.util.*

@Table("emp")
data class Emp(
    @Id
    @Column("id")
    @ColumnAlias("id1", "id2")
    var id: UInt,
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
)