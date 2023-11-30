package handler

import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet

class ULongTypeHandler : BaseTypeHandler<ULong>() {
    override fun setNonNullParameter(ps: PreparedStatement, i: Int, parameter: ULong, jdbcType: JdbcType) {
        ps.setLong(i, parameter.toLong())
    }

    override fun getNullableResult(rs: ResultSet, columnName: String): ULong {
        return rs.getLong(columnName).toULong()
    }

    override fun getNullableResult(rs: ResultSet, columnIndex: Int): ULong {
        return rs.getLong(columnIndex).toULong()
    }

    override fun getNullableResult(cs: CallableStatement, columnIndex: Int): ULong {
        return cs.getLong(columnIndex).toULong()
    }
}