package handler

import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet

class UIntTypeHandler : BaseTypeHandler<UInt>() {
    override fun getNullableResult(cs: CallableStatement, columnIndex: Int): UInt {
        return cs.getInt(columnIndex).toUInt()
    }

    override fun getNullableResult(rs: ResultSet, columnIndex: Int): UInt {
        return rs.getInt(columnIndex).toUInt()
    }

    override fun getNullableResult(rs: ResultSet, columnName: String): UInt {
        return rs.getInt(columnName).toUInt()
    }

    override fun setNonNullParameter(ps: PreparedStatement, i: Int, parameter: UInt, jdbcType: JdbcType) {
        ps.setInt(i, parameter.toInt())
    }
}