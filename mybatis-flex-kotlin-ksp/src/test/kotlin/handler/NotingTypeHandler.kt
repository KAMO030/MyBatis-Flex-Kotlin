package handler

import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import org.apache.ibatis.type.MappedJdbcTypes
import org.apache.ibatis.type.MappedTypes
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet

@MappedJdbcTypes(JdbcType.OTHER)
@MappedTypes(Nothing::class)
class NotingTypeHandler : BaseTypeHandler<Nothing>() {
    override fun getNullableResult(rs: ResultSet?, columnName: String?): Nothing {
        TODO("Not yet implemented")
    }

    override fun getNullableResult(rs: ResultSet?, columnIndex: Int): Nothing {
        TODO("Not yet implemented")
    }

    override fun getNullableResult(cs: CallableStatement?, columnIndex: Int): Nothing {
        TODO("Not yet implemented")
    }

    override fun setNonNullParameter(ps: PreparedStatement?, i: Int, parameter: Nothing?, jdbcType: JdbcType?) {
        TODO("Not yet implemented")
    }
}