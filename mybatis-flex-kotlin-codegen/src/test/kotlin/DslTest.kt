import com.mybatisflex.annotation.Column
import com.mybatisflex.kotlin.codegen.config.extensions.*
import com.mybatisflex.kotlin.codegen.config.generate
import com.mybatisflex.kotlin.codegen.internal.Table
import com.mysql.cj.jdbc.MysqlDataSource
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.KModifier
import org.junit.jupiter.api.Test

class DslTest {
    private val dataSource = MysqlDataSource().apply {
        setUrl("jdbc:mysql://localhost:3306/homo")
        user = "root"
        password = "123456"
    }

    @Test
    fun test() {
        // 单独为表 tb_account 生成实体类，要求为数据类，且拥有 Table 注解和 Column 注解
        val res = generate(dataSource) {
            // 单独为表 tb_account 生成代码
            withTables("tb_account") {
                generateDefault()  // 生成所有默认产物，这些产物会使用各自的默认配置
                // 手动配置实体类生成
                onEntity {
                    dataclass()  // 数据类
                    tableAnnotation()  // 类上新增 Table 注解
                    columnAnnotation()  // 每个属性新增 Column 注解
                }
            }
        }
        res.forEach {
            println(it.build())
            println("---------------------------------------")
        }
    }

    // 上面那个函数手写后的样子
    @OptIn(DelicateKotlinPoetApi::class)
    @Test
    fun testUnwrapped() {
        // 单独为表 tb_account 生成实体类，要求为数据类，且拥有 Table 注解和 Column 注解
        val res = generate(dataSource) {
            withTables("tb_account") {
                generateDefault()
                onEntity {
                    transformType { tableMetadata, builder ->
                        builder.addModifiers(KModifier.DATA)  // 数据类
                        builder.addAnnotation(AnnotationSpec.get(Table(tableMetadata.tableName)))  // 通过注解构造 AnnotationSpec
                    }
                    // 使用原生 KotlinPoet 语法，生成 Column 注解
                    transformProperty { columnMetadata, builder ->
                        val anno = AnnotationSpec.builder(Column::class).addMember("%S", columnMetadata.name).build()
                        builder.addAnnotation(anno)
                    }
                }
            }
        }
        res.forEach {
            println(it.build())
            println("---------------------------------------")
        }
    }
}