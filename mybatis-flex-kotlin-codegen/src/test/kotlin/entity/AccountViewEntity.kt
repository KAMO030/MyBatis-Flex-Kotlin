package entity

import com.mybatisflex.annotation.Column
import com.mybatisflex.annotation.Id
import com.mybatisflex.annotation.KeyType
import com.mybatisflex.annotation.Table
import java.util.*

/**
 * 实体类。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Table(value = "account_view")
data class AccountViewEntity(
    @Column(value = "id")
    @Id(keyType = KeyType.Auto)
    var id: Int? = null,
    @Column(value = "user_name")
    var userName: String? = null,
    @Column(value = "age")
    var age: Int? = null,
    @Column(value = "birthday")
    var birthday: Date? = null
)