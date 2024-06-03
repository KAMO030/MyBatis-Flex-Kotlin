import com.mybatisflex.core.BaseMapper
import com.mybatisflex.kotlin.codegen.metadata.DataSourceMetadata
import com.mybatisflex.kotlin.codegen.metadata.provider.DefaultProvider
import com.mybatisflex.kotlin.scope.runFlex
import com.mysql.cj.jdbc.MysqlDataSource
import org.apache.ibatis.io.ResolverUtil
import org.apache.ibatis.logging.stdout.StdOutImpl
import org.junit.jupiter.api.Test

class OriginalTest {
    private val dataSource = MysqlDataSource().apply {
        setUrl("jdbc:mysql://localhost:3306/homo")
        user = "root"
        password = "123456"
    }

    init {
        runFlex {
            println("mybatis-flex location: ${System.getProperty("user.dir")}")
            val resolverUtil = ResolverUtil<BaseMapper<*>>()
            resolverUtil.find(ResolverUtil.IsA(BaseMapper::class.java), "mapper")
            resolverUtil.classes.forEach { mapperClass ->
                it.addMapper(mapperClass)
            }
            it.dataSource = dataSource
            it.logImpl = StdOutImpl::class.java
        }
    }

    @Test
    fun testResult() {
        val res = DefaultProvider().provideMetadata(DataSourceMetadata(dataSource))
        res.forEach {
            println(it)
        }
        println("----------------------------------")
        res.map { it.columns }.forEach {
            println(it)
        }
    }
}
