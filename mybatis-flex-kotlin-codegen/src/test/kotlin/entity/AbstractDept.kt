package entity

import com.mybatisflex.annotation.Id
import com.mybatisflex.annotation.KeyType
import java.time.LocalDateTime

abstract class AbstractDept(
    @Id(keyType = KeyType.Auto)
    open var id: Int = Int.MIN_VALUE,
    open var name: String = "",
    open var createTime: LocalDateTime = LocalDateTime.now(),
    open var updateTime: LocalDateTime = LocalDateTime.now(),
//    @Column("a_dept")
    val aDept: Map<String, String> = emptyMap(),
)