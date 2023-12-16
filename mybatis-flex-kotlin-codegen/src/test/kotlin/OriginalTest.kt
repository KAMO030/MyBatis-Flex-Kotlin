import com.alibaba.druid.pool.DruidDataSource
import com.alibaba.excel.EasyExcel
import com.alibaba.excel.read.listener.PageReadListener
import com.alibaba.excel.support.ExcelTypeEnum
import com.mybatisflex.core.BaseMapper
import com.mybatisflex.core.FlexGlobalConfig
import com.mybatisflex.core.MybatisFlexBootstrap
import com.mybatisflex.core.query.BaseQueryWrapper
import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.core.row.Db
import com.mybatisflex.core.table.TableInfoFactory
import com.mybatisflex.kotlin.extensions.db.mapper
import com.mybatisflex.kotlin.extensions.kproperty.allColumns
import com.mybatisflex.kotlin.extensions.kproperty.column
import com.mybatisflex.kotlin.extensions.kproperty.eq
import com.mysql.cj.jdbc.result.ResultSetMetaData
import entity.BigZn
import entity.Dept
import entity.Emp
import entity.EmpWithDeptName
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import mapper.*
import org.apache.ibatis.io.ResolverUtil
import org.apache.ibatis.logging.stdout.StdOutImpl
import org.apache.ibatis.session.ExecutorType
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.thread
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.*

object OriginalTest {
    private val dataSource = DruidDataSource().apply {
        url = "jdbc:mysql://localhost:3306/homo"
        username = "root"
        password = "123456"
    }

    init {
        MybatisFlexBootstrap.getInstance().also {
            println("mybatis-flex location: ${System.getProperty("user.dir")}")
            it.addMapper(EmpMapper::class.java)
            it.addMapper(DeptMapper::class.java)
            it.addMapper(BigZnMapper::class.java)
            it.addMapper(CreditfirstuseMapper::class.java)
            it.addMapper(CreditsamplewindowMapper::class.java)
            it.addMapper(AccountViewMapper::class.java)
            it.dataSource = dataSource
            it.logImpl = StdOutImpl::class.java
        }.start()
    }

    val empMapper = mapper<EmpMapper>()

    val deptMapper = mapper<DeptMapper>()

    val bigZnMapper = mapper<BigZnMapper>()

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
    fun testDesc() {
        val statement = dataSource.connection.createStatement()
        val resultSet = statement.executeQuery("select * from `emp`")
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

    @Test
    fun wrapper() {
        val wrapper1 = QueryWrapper()
        wrapper1.select(Emp::id.column.`as`("id1"))

        val wrapper2 = QueryWrapper()
        wrapper2.select(Emp::id.column.`as`("id2"))

        println(empMapper.selectListByQuery(wrapper1))
        println(empMapper.selectListByQuery(wrapper2))
    }

    private fun dropAndCreateTable() {
        val stat = dataSource.connection.createStatement()
        stat.execute("drop table if exists big_zn")
        stat.execute(
            """create table if not exists homo.big_zn
(
    date_time      varchar(30) not null,
    date_time_nano bigint      not null,
    last_price     int         not null,
    highest        int         not null,
    lowest         int         not null,
    volume         int         not null,
    amount         long        not null,
    open_interest  int         not null,
    bid_price1     int         not null,
    bid_volume1    int         not null,
    ask_price1     int         not null,
    ask_volume1    int         not null
);"""
        )
    }

    @Test
    fun test() {
        val file =
            File("C:\\Users\\yunwanjia\\IdeaProjects\\MyBatis-Flex-Kotlin\\mybatis-flex-kotlin-codegen\\src\\test\\resources\\zn.csv")
        val list = EasyExcel.read(file, BigZn::class.java, PageReadListener<BigZn> { _ ->
        }).excelType(ExcelTypeEnum.CSV).sheet().doReadSync<BigZn>()
        dropAndCreateTable()
        val other = list.groupBy {
            QueryWrapper()
        }.toList()
        Db.executeBatch(list, BigZn::class.java) { mapper, bigZn ->

        }
        println(list.size)
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
//
//        sqlSessionFactory.openSession(ExecutorType.BATCH, true).use { sqlSession ->
//            val mapper = sqlSession.getMapper(mapperClass)
//            var counter = 0
//            for (data in datas) {
//                consumer(mapper, data)
//                if (++counter == batchSize) {
//                    counter = 0
//                    buf += async(Dispatchers.IO) {
//                        sqlSession.flushStatements().flatMap {
//                            it.updateCounts.asList()
//                        }
//                    }
//                }
//                if (counter != 0) {
//                    buf += async(Dispatchers.IO) {
//                        sqlSession.flushStatements().flatMap {
//                            it.updateCounts.asList()
//                        }
//                    }
//                }
//            }
//
//        }

        return buf
    }

    private fun runDb(list: Collection<BigZn>): Long {
        dropAndCreateTable()
        val now = System.currentTimeMillis()
        Db.executeBatch(list, 1000, BigZnMapper::class.java) { mapper, bigZn ->
            mapper.insert(bigZn)
        }
        val res = System.currentTimeMillis() - now
        dropAndCreateTable()
        return res
    }

    private fun runSuspend(list: Collection<BigZn>): Long {
        val now = System.currentTimeMillis()
        executeBatchSuspend<BigZnMapper, BigZn>(list) { bigZnMapper, bigZn ->
            bigZnMapper.insert(bigZn)
        }
        val res = System.currentTimeMillis() - now
        dropAndCreateTable()
        return res
    }

    @Test
    fun deptTest() {
        val wrapper = QueryWrapper.create()
        wrapper
            .select(Dept::id.column, Dept::name.column, Dept::createTime.column)
            .where(Dept::createTime eq LocalDateTime.parse("2023-11-01T11:24:15"))
            .where(Dept::id.column.`in`(emptyList<Nothing?>()))
        println(deptMapper.selectListByQuery(wrapper))
    }

    @Test
    fun test2() {
        val handler = Thread.UncaughtExceptionHandler { thread: Thread, e: Throwable ->
            System.err.println("[error] The exception is $e, happened in $thread")
        }
        Thread.setDefaultUncaughtExceptionHandler(handler)
        Thread.currentThread().uncaughtExceptionHandler = handler
        thread {
            throw IllegalArgumentException()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun py(): Unit = runBlocking {
        val channel = Channel<Dept>()
        testChannel(channel)
        val random = Random()
        val res = buildList {
            while (true) {
                println("Hello World!")
                val time = random.nextLong(0, 10000)
                delay(time)
                val dept = channel.receive()
                this += dept
                if (channel.isClosedForReceive) {
                    break
                }
            }
        }
        println(res)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun testChannel(channel: SendChannel<Dept>): Unit = runBlocking {
        val wrapper = QueryWrapper()
        wrapper.select().from(Dept::class.java)
        launch(Dispatchers.IO) {
            Db.tx {
                deptMapper.selectCursorByQuery(wrapper).use {
                    launch(Dispatchers.IO) {
                        delay(1000)
                        println("sendChannel launch")
                        for (dept in it) {
                            channel.send(dept)
                        }
                    }
                }
                println("cursor is closed")
                true
            }
            println("tx is closed")
        }
    }

    @Test
    fun sendReceiveTest(): Unit = runBlocking {
        val wrapper = QueryWrapper()
        wrapper.select().from(Dept::class.java)
        val channel = Channel<Dept>()
        Db.tx {
            launch {
                deptMapper.selectCursorByQuery(wrapper).forEach {
                    delay(1000)
                    channel.send(it)
                }
                channel.close()
            }
            true
        }
        for (dept in channel) {
            println(dept)
        }
        TableInfoFactory.ofEntityClass(Emp::class.java)
    }

    @Test
    fun example() {
        val allTypes = AccountViewMapper::class.supertypes
        val baseMapper: KType? = allTypes.find {
            it.classifier == BaseMapper::class
        }
        println(allTypes)
        println(allTypes.map { it.classifier })
        baseMapper?.let {
            val clazz = it.arguments[0].type!!.jvmErasure.java
            println(clazz)
        }
    }

    @Test
    fun resolverUtilTest() {
        val resolver = ResolverUtil<BaseQueryWrapper<*>>()
        resolver.find(ResolverUtil.IsA(BaseQueryWrapper::class.java), "com.mybatisflex.kotlin.vec")
        val classes = resolver.classes
        println(classes)
    }

    @Test
    fun kPropReflation() {
        val dept = Dept()
        val prop = dept::aDept
        val javaClass = prop.javaClass
        println(prop)
        val propertyReference = javaClass.superclass.superclass
        val refMethod = propertyReference.getDeclaredMethod("computeReflected")
        refMethod.isAccessible = true
        val refKCallable = refMethod.invoke(prop) as KCallable<*>
        println(refKCallable.instanceParameter)
        val propImplClass = refKCallable.javaClass
        println(propImplClass.superclass)
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

    @Test
    fun wrapperTest() {
        Db.tx {
            val wrapper = QueryWrapper()
            wrapper.select(Emp::class.allColumns).from(Emp::class.java)
            empMapper.cursor(wrapper, EmpWithDeptName::class).forEach {
                println(it)
            }
            true
        }
    }
}
