import com.mybatisflex.codegen.Generator
import com.mybatisflex.codegen.config.GlobalConfig
import com.mysql.cj.jdbc.MysqlDataSource
import org.junit.jupiter.api.Test

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
        val globalConfig = GlobalConfig()
        globalConfig.templateConfig
        val generator = Generator(dataSource, globalConfig)
        generator.generate()
    }

    @Test
    fun typeTest() {
    }
}