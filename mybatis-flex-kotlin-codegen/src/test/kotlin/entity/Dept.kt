package entity

import com.mybatisflex.annotation.Id
import com.mybatisflex.annotation.Table
import java.time.LocalDateTime

@Table("dept")
data class Dept(
    @Id
    var id: Int,
    var name: String,
    var createTime: LocalDateTime,
    var updateTime: LocalDateTime,
)