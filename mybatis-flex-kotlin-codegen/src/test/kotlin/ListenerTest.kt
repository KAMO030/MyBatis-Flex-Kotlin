import com.alibaba.druid.pool.DruidDataSource
import com.mybatisflex.core.BaseMapper
import com.mybatisflex.core.MybatisFlexBootstrap
import com.mybatisflex.core.query.QueryColumnBehavior
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.kotlin.extensions.db.mapper
import com.mybatisflex.kotlin.extensions.kproperty.isNull
import entity.Account
import mapper.AccountMapper
import org.apache.ibatis.io.ResolverUtil
import org.apache.ibatis.logging.stdout.StdOutImpl
import org.junit.jupiter.api.Test

object ListenerTest {

    private val dataSource = DruidDataSource().apply {
        url = "jdbc:mysql://localhost:3306/homo"
        username = "root"
        password = "123456"
    }

    init {
        MybatisFlexBootstrap.getInstance().also {
            println("mybatis-flex location: ${System.getProperty("user.dir")}")
            val resolverUtil = ResolverUtil<BaseMapper<*>>()
            resolverUtil.find(ResolverUtil.IsA(BaseMapper::class.java), "mapper")
            resolverUtil.classes.forEach { mapperClass ->
                it.addMapper(mapperClass)
            }
            it.dataSource = dataSource
            it.logImpl = StdOutImpl::class.java
        }.start()
    }

    @Test
    fun insertAccount() {
        val account = Account()
        val mapper = mapper<AccountMapper>()
        println(mapper.insertSelective(account))
        println(account)
    }

    @Test
    fun updateWrapper() {
        QueryColumnBehavior.setIgnoreFunction {
            requireNotNull(it)
            if (it is Collection<*> && it.isEmpty()) {
                throw IllegalArgumentException()
            }
            false
        }
        val wrapper = QueryWrapper()
        wrapper.where(Account::id.isNull)
        println(wrapper.toSQL())
    }
}