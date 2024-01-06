package entities

import com.mybatisflex.annotation.Column
import com.mybatisflex.annotation.ColumnAlias
import com.mybatisflex.annotation.Id
import com.mybatisflex.annotation.Table
import com.mybatisflex.core.query.QueryColumn
import com.mybatisflex.core.table.TableDef
import handler.NotingTypeHandler
import java.util.*
import kotlin.properties.Delegates

/**
 * 这是一个测试类。用于测试 KSP 是否会如期生成对应的 [TableDef] 。
 *
 * 生成的类应继承 [TableDef] 类，且对于每一个生成的属性，
 * 其类型均为 [QueryColumn] ，且调用 [QueryColumn] 构造器传入的第一个参数为 this。
 *
 * @property id 其名字是否因为显示指定名字而改为 named_id 。
 * @property userName 其名字是否会因为驼峰改蛇形而变为 user_name 。
 * @property largeProperty 该属性是否会因为是大字段而不会加入到生成的 default column 中。
 * @property hasAliasProperty 该属性是否会因为指定了列别名，其对应的 [QueryColumn] 的构造器由 (tableDef: [TableDef], name: [String]) -> QueryColumn
 * 变为了 (tableDef: [TableDef], name: [String], alias: [String]) -> QueryColumn ，并且在第三个参数中使用了注解中指定的第一个别名。
 * @property ignoreProperty 该属性应当被忽略，不生成对应的 [QueryColumn] 。
 * @property unsupportedTypeProperty 该属性的类型不支持，不生成对应的 [QueryColumn] 。
 * @property supportedTypeProperty 该属性类型虽不支持，但是配置了 TypeHandler ，应当生成对应的 [QueryColumn] 。
 * @property enumProperty 该属性为枚举类，应当生成对应的 [QueryColumn] 。
 * @property noBackingFieldProperty 该属性没有 backing field ，不生成对应的 [QueryColumn] 。
 * @author CloudPlayer
 */
@[Table("tb_account", schema = "homo") Suppress("unused")]
data class TestEntity(
    @[Column("named_id") Id]
    var id: Int = -1,
    var userName: String? = null,
    @Column(isLarge = true)
    var largeProperty: Int? = null,
    @ColumnAlias("my_birthday", "your_birthday", "his_birthday", "her_birthday")
    var hasAliasProperty: Date? = null,
    @Column(ignore = true)
    val ignoreProperty: Int = 0,

    val unsupportedTypeProperty: Nothing,
    @Column(typeHandler = NotingTypeHandler::class)
    val supportedTypeProperty: Nothing,

    val enumProperty: DeprecationLevel = DeprecationLevel.WARNING,
) {
    var noBackingFieldProperty: String by Delegates.notNull()
}
