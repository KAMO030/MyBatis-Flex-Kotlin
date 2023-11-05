import com.mybatisflex.core.BaseMapper
import com.mybatisflex.core.MybatisFlexBootstrap
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.core.row.Db
import com.mybatisflex.kotlin.extensions.db.mapper
import com.mybatisflex.kotlin.extensions.kproperty.column
import com.mysql.cj.jdbc.MysqlDataSource
import entity.Dept
import entity.Emp
import mapper.DeptMapper
import mapper.EmpMapper
import org.apache.ibatis.logging.stdout.StdOutImpl
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.reflect.full.isSubclassOf

class OriginalTest {
    private val dataSource = MysqlDataSource().apply {
        setUrl("jdbc:mysql://localhost:3306/homo")
        user = "root"
        password = "123456"
    }

    init {
        MybatisFlexBootstrap.getInstance().also {
            it.addMapper(EmpMapper::class.java)
            it.addMapper(DeptMapper::class.java)
            it.dataSource = dataSource
            it.logImpl = StdOutImpl::class.java
        }.start()
    }

    val empMapper = mapper<EmpMapper>()

    val deptMapper = mapper<DeptMapper>()

    @Test
    fun testRelation() {
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
    fun testSelect() {
        val statement = dataSource.connection.createStatement()
        val resultSet = statement.executeQuery("select * from `emp` where 0")
        val metadata = resultSet.metaData
        val al = ArrayList<Pair<String, String>>()
        val count = metadata.columnCount
        for (i in 1..count) {
            al += metadata.getColumnName(i) to metadata.getColumnClassName(i)
        }
        println(al)
    }

    @Test
    fun testDesc() {
        val statement = dataSource.connection.createStatement()
        val resultSet = statement.executeQuery("desc `emp`")
        val res = ArrayList<Any?>()
        while (!resultSet.isClosed && resultSet.next()) {
            val obj: Any? = resultSet.getObject("Default")
            res += obj
        }
        println(res)
    }

    @Test
    fun wrapper() {
        val wrapper1 = QueryWrapper()
        wrapper1.select(Emp::id.column().`as`("id1"))

        val wrapper2 = QueryWrapper()
        wrapper2.select(Emp::id.column().`as`("id2"))

        println(empMapper.selectListByQuery(wrapper1))
        println(empMapper.selectListByQuery(wrapper2))
    }

    @Test
    fun test() {
        assert(EmpMapper::class.isSubclassOf(BaseMapper::class))
    }
}


