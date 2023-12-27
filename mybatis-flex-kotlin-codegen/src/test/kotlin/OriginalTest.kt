import com.mybatisflex.core.BaseMapper
import com.mybatisflex.core.FlexGlobalConfig
import com.mybatisflex.core.query.BaseQueryWrapper
import com.mybatisflex.core.row.Db
import com.mybatisflex.kotlin.extensions.db.mapper
import com.mybatisflex.kotlin.scope.runFlex
import com.mysql.cj.jdbc.MysqlDataSource
import com.mysql.cj.jdbc.result.ResultSetMetaData
import entity.Dept
import mapper.DeptMapper
import mapper.EmpMapper
import org.apache.ibatis.io.ResolverUtil
import org.apache.ibatis.logging.stdout.StdOutImpl
import org.apache.ibatis.session.ExecutorType
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.valueParameters

object OriginalTest {
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

    val empMapper = mapper<EmpMapper>()

    val deptMapper = mapper<DeptMapper>()

    @Test
    fun testExecuteBatch() {
        val list = listOf(
            Dept(name = "管理部", createTime = LocalDateTime.now(), updateTime = LocalDateTime.now()),
            Dept(name = "后勤部", createTime = LocalDateTime.now(), updateTime = LocalDateTime.now()),
            Dept(name = "技术部", createTime = LocalDateTime.now(), updateTime = LocalDateTime.now()),
        )
        Db.executeBatch(list, DeptMapper::class.java) { mapper, dept ->
            mapper.insert(dept)
        }
        list.forEach {
            println(it.id)
        }
    }

    @Test
    fun testMetaData() {
        val statement = dataSource.connection.createStatement()
        val resultSet = statement.executeQuery("select * from `tb_account` where 0")
        val metadata = resultSet.metaData as ResultSetMetaData
        println(metadata::class)
        val al = ArrayList<Pair<String, Class<*>>>()
        val count = metadata.columnCount
        for (i in 1..count) {
            al += metadata.getColumnName(i) to Class.forName(metadata.getColumnClassName(i))
        }
        println(al)
    }

    @Test
    fun testJdbc() {
        val statement = dataSource.connection.createStatement()
        val resultSet = statement.executeQuery("select * from `tb_account`")
        val res = ArrayList<ArrayList<Any?>>()
        while (!resultSet.isClosed && resultSet.next()) {
            val row = ArrayList<Any?>()
            for (i in 1..resultSet.metaData.columnCount) {
                row += resultSet.getObject(i)
            }
            res += row
        }
        println(res)
    }

    private inline fun <reified M : BaseMapper<*>, E : Any> executeBatchSuspend(
        datas: Collection<E>,
        batchSize: Int = 1000,
        consumer: (M, E) -> Unit
    ): IntArray {
        val sqlSessionFactory = FlexGlobalConfig.getDefaultConfig().sqlSessionFactory
        val buf = IntArray(datas.size)
        var bufPos = 0
        var counter = 0
        sqlSessionFactory.openSession(ExecutorType.BATCH, true).use { sqlSession ->
            val mapper = sqlSession.getMapper(M::class.java)
            datas.forEach { data ->
                consumer(mapper, data)
                if (++counter == batchSize) {
                    counter = 0
                    val stat = sqlSession.flushStatements()
                    stat.forEach { batchResult ->
                        val counts = batchResult.updateCounts
                        counts.copyInto(buf, bufPos)
                        bufPos += counts.size
                    }
                }
            }
            if (counter != 0) {
                val stat = sqlSession.flushStatements()
                stat.forEach { batchResult ->
                    val counts = batchResult.updateCounts
                    counts.copyInto(buf, bufPos)
                    bufPos += counts.size
                }
            }
        }
        return buf
    }

    @Test
    fun resolverUtilTest() {
        val resolver = ResolverUtil<BaseQueryWrapper<*>>()
        resolver.find(ResolverUtil.IsA(BaseQueryWrapper::class.java), "com.mybatisflex.kotlin.vec")
        val classes = resolver.classes
        println(classes)
    }

    @Test
    fun kProp() {
        val dept = Dept(id = 114514)
        val constructor = ::Dept
        val properties = Dept::class.declaredMemberProperties
        val map = constructor.valueParameters.associateWith { parm ->
            properties.find {
                it.visibility
                it.name == parm.name
            }?.get(dept)
        }
        println(constructor.callBy(map))
    }
}
