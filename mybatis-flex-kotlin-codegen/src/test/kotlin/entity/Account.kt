package entity

import com.mybatisflex.annotation.Id
import com.mybatisflex.annotation.Table
import com.mybatisflex.core.activerecord.Model
import java.util.*

/**
 * 测试用数据类
 *
 * （最好不要写成 data class ，否则没有无参构造某些情况下会报错）
 *
 * （如有需要可以安装官方 noArg 插件）
 *
 * @author KAMOsama
 * @date 2023/8/7
 */
@Table("tb_account")
data class Account(
    @Id var id: Int = -1,
    var userName: String? = null,
    var age: Int? = null,
    var birthday: Date? = null,
) : Model<Account>()

