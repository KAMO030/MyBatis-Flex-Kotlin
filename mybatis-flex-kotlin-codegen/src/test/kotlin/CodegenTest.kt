import com.mybatisflex.codegen.Generator
import com.mybatisflex.codegen.config.GlobalConfig
import com.mybatisflex.codegen.generator.GeneratorFactory
import com.mybatisflex.kotlin.ksp.config.KTableDefGenerator
import com.mysql.cj.jdbc.MysqlDataSource
import org.junit.jupiter.api.Test
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class CodegenTest {
    private val dataSource = MysqlDataSource().apply {
        setUrl("jdbc:mysql://localhost:3306/homo")
        password = "123456"
        user = "root"
    }

    private fun GlobalConfig.generateAllCode() {
        basePackage = "kotlin"
        isEntityGenerateEnable = true
        isControllerGenerateEnable = true
        isMapperGenerateEnable = true
        isServiceGenerateEnable = true
        isTableDefGenerateEnable = true
        isServiceImplGenerateEnable = true
    }

    @Test
    fun test() {
        GeneratorFactory.registerGenerator("kt", KTableDefGenerator())
        val globalConfig = GlobalConfig()
        globalConfig.templateConfig
        val generator = Generator(dataSource, globalConfig)
        generator.generate()
    }

    @Test
    fun typeTest() {
        val type = typeOf<HashMap<List<String>, ArrayList<in String>>>()
        println(type.asTypedString())
        println(type.asQualifiedNames())
        println(type)
    }

    /**
     * 去除全名中的包名，只携带类名。
     * 例如 "kotlin.String" 变为 "String"，
     * "kotlin.collections.List<kotlin.String>" 变为 "List<String>"，
     *
     * “java.util.HashMap<kotlin.collections.List<kotlin.String>, kotlin.collections.Iterable<kotlin.Int>>”
     * 变为 “HashMap<List<String>, Iterable<Int>>”。
     *
     *
     */
    private fun KType.asTypedString(): String {
        val name = toString()
        val regex = Regex("""\w+\.""")
        return name.replace(regex, "")
    }

    /**
     * 将全名中的全名提取出来。
     *
     * typeOf<HashMap<List<String>, ArrayList<in String>>>()
     * 变为 [
     * java.util.HashMap,
     * kotlin.collections.List,
     * kotlin.String,
     * java.util.ArrayList,
     * kotlin.String]。
     */
    private fun KType.asQualifiedNames(): List<String> =
        Regex("""(\w+\.?)+""").findAll(toString()).mapTo(ArrayList()) { it.value }
}