package entity

import com.mybatisflex.annotation.Id
import com.mybatisflex.annotation.KeyType
import com.mybatisflex.annotation.Table
import java.time.LocalDateTime

@Table("dept")
data class Dept(
    @Id(keyType = KeyType.Auto)
    override var id: Int = Int.MIN_VALUE,
    override var name: String = "",
    override var createTime: LocalDateTime = LocalDateTime.now(),
    override var updateTime: LocalDateTime = LocalDateTime.now(),
    val deptSomething: String = ""
) : AbstractDept()