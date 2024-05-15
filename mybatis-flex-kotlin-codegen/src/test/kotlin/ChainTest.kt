import com.mybatisflex.core.BaseMapper
import com.mybatisflex.kotlin.extensions.chain.QueryChain
import com.mybatisflex.kotlin.extensions.kproperty.lt
import com.mybatisflex.kotlin.scope.runFlex
import com.mysql.cj.jdbc.MysqlDataSource
import entity.Account
import org.apache.ibatis.io.ResolverUtil
import org.apache.ibatis.logging.stdout.StdOutImpl
import org.junit.jupiter.api.Test

object ChainTest {
    private val dataSource = MysqlDataSource().apply {
        setUrl("jdbc:mysql://localhost:3306/homo")
        user = "root"
        password = "123456"
    }

    init {
        runFlex {
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
    fun test() {
        val chain = QueryChain<Account>()
        chain.select().where(Account::id lt 1)
        println(chain.oneAs(Any::class.java))
    }
}