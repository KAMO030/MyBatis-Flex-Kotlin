package entities

import com.mybatisflex.annotation.Column
import com.mybatisflex.annotation.ColumnAlias
import com.mybatisflex.annotation.Table
import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.table.TableDef
import java.io.Serializable
import java.util.*

/**
 * 这是一个测试类。每个属性都测试对应了 KSP 中的一个功能。
 *
 * 生成的类应继承 [TableDef] 类，且对于每一个生成的属性，
 * 其类型均为 [QueryColumn] ，且调用 [QueryColumn] 构造器传入的第一个参数为 this。
 *
 * @param id 其名字是否因为显示指定名字而改为 named_id 。
 * @param userName 其名字是否会因为驼峰改蛇形而变为 user_name 。
 * @param age 该属性是否会因为是大字段而不会加入到生成的 default column 中。
 * @param birthday 该属性是否会因为指定了列别名，其对应的 [QueryColumn] 的构造器由 (tableDef: [TableDef], name: [String]) -> QueryColumn
 * 变为了 (tableDef: [TableDef], name: [String], alias: [String]) -> QueryColumn ，并且在第三个参数中使用了注解中指定的第一个别名。
 * @author CloudPlayer
 */
@Table("tb_account", schema = "homo")
@Suppress("unused")
data class TestAccount(
    @Column("named_id")
    var id: Int = -1,
    var userName: String? = null,
    @Column(isLarge = true)
    var age: Int? = null,
    @ColumnAlias("my_birthday", "your_birthday", "his_birthday", "her_birthday")
    var birthday: Date? = null,
    @Column(ignore = true)
    val ignoreProperty: Int = 0
) : Serializable by 0 {
    @delegate:Column(ignore = true)
    val aDelegateProperty by lazy {
        "a delegate property"
    }
}