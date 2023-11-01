package mapper

import com.mybatisflex.core.BaseMapper
import entity.Dept
import entity.Emp

interface DeptMapper : BaseMapper<Dept>

interface EmpMapper : BaseMapper<Emp>