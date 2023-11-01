import com.mybatisflex.core.MybatisFlexBootstrap
import com.mybatisflex.core.mybatis.Mappers
import com.mysql.cj.jdbc.MysqlDataSource
import entity.Dept
import entity.Emp
import mapper.DeptMapper
import mapper.EmpMapper
import org.apache.ibatis.logging.stdout.StdOutImpl
import org.junit.jupiter.api.Test

class OriginalTest {
    init {
        MybatisFlexBootstrap.getInstance().also {
            it.addMapper(EmpMapper::class.java)
            it.addMapper(DeptMapper::class.java)
            it.dataSource = MysqlDataSource().apply {
                setUrl("jdbc:mysql://localhost:3306/homo")
                user = "root"
                password = "123456"
            }
            it.logImpl = StdOutImpl::class.java
        }.start()
    }

    val empMapper = Mappers.ofEntityClass(Emp::class.java)

    val deptMapper = Mappers.ofEntityClass(Dept::class.java)

    @Test
    fun test() {
        println(empMapper.selectAll())
    }

    @Test
    fun testRelation() {
        println(empMapper.selectAllWithRelations())
    }
}


