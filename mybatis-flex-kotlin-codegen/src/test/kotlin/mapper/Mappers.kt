package mapper

import com.mybatisflex.core.BaseMapper
import com.mybatisflex.core.FlexConsts
import com.mybatisflex.core.mybatis.MappedStatementTypes
import com.mybatisflex.core.provider.EntitySqlProvider
import com.mybatisflex.core.query.QueryWrapper
import entity.*
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.SelectProvider
import org.apache.ibatis.cursor.Cursor
import kotlin.reflect.KClass

interface DeptMapper : BaseMapper<Dept>

interface EmpMapper : BaseMapper<Emp> {
    @SelectProvider(value = EntitySqlProvider::class, method = "selectListByQuery")
    @Suppress("UNCHECKED_CAST")
    fun <R : Any> cursor(@Param(FlexConsts.QUERY) queryWrapper: QueryWrapper, asType: KClass<R>): Cursor<R> = try {
        MappedStatementTypes.setCurrentType(asType.java)
        selectCursorByQuery(queryWrapper) as Cursor<R>
    } finally {
        MappedStatementTypes.clear()
    }

}

interface BigZnMapper : BaseMapper<BigZn>

interface AccountMapper : BaseMapper<Account>