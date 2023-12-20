package mapper

import com.mybatisflex.core.BaseMapper
import entity.Account
import entity.Dept
import entity.Emp

interface DeptMapper : BaseMapper<Dept>

interface EmpMapper : BaseMapper<Emp>

interface AccountMapper : BaseMapper<Account>