import com.mybatisflex.core.BaseMapper
import com.mybatisflex.core.query.QueryColumnBehavior
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.kotlin.extensions.kproperty.column
import com.mybatisflex.kotlin.extensions.kproperty.isNull
import com.mybatisflex.kotlin.extensions.wrapper.selectProperties
import com.mybatisflex.kotlin.scope.runFlex
import com.mysql.cj.jdbc.MysqlDataSource
import entity.Account
import org.apache.ibatis.io.ResolverUtil
import org.apache.ibatis.logging.stdout.StdOutImpl
import org.junit.jupiter.api.Test

object ListenerTest {
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
    fun testSelectProperties() {
        val account = Account()
        val wrapper = QueryWrapper()
        wrapper.selectProperties(account::id, Account::id)
        println(wrapper.toSQL())
    }

    @Test
    fun testBehavior() {
        QueryColumnBehavior.setIgnoreFunction {
            requireNotNull(it)
            if (it is Collection<*> && it.isEmpty()) {
                throw IllegalArgumentException()
            }
            false
        }
        val wrapper = QueryWrapper()
        wrapper.where(Account::id.isNull).select(Account::id.column())
        println(wrapper.toSQL())
    }
}