import com.mybatisflex.codegen.Generator
import com.mybatisflex.codegen.config.GlobalConfig
import com.mybatisflex.codegen.generator.GeneratorFactory
import com.mybatisflex.kotlin.codegen.config.ClassTableDefGenerator
import com.mybatisflex.kotlin.codegen.internal.asQualifiedNames
import com.mybatisflex.kotlin.codegen.internal.asTypedString
import com.mysql.cj.jdbc.MysqlDataSource
import org.junit.jupiter.api.Test
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
        GeneratorFactory.registerGenerator("kt", ClassTableDefGenerator())
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
}