package entity

import com.mybatisflex.annotation.Id
import com.mybatisflex.annotation.KeyType
import com.mybatisflex.annotation.Table
import java.time.LocalDateTime

@Table("dept")
data class Dept(
    @Id(keyType = KeyType.Auto)
    var id: Int = Int.MIN_VALUE,
    var name: String,
    var createTime: LocalDateTime,
    var updateTime: LocalDateTime,
)