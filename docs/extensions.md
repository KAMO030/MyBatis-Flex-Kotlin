# 简单查询与扩展

## 快速获取mapper与tableInfo

- **mapper**
    - 通过mapper接口类型作为泛型调用 mapper() 方法直接获取
      ````kotlin
       val accountMapper: AccountMapper = mapper()
      ````
    - 通过实体型 KClass 的 baseMapper 属性直接获取
      > 注意：此方式获得的实例类型是 BaseMapper<Account(实体类型)>
      ，并不是 AccountMapper 所以无法使用 AccountMapper 接口定义的方法
       ````kotlin
        val baseMapper: BaseMapper<Account> = Account::class.baseMapper
       ````
- **tableInfo**
    - 通过实体型 KClass 的 tableInfo 属性直接获取
      ````kotlin
       val accountTableInfo: TableInfo = Account::class.tableInfo
      ````

## 快速查询

1. `all<实体类>()` : 查泛型对应的表的所有数据

      ```kotlin
       val accounts: List<Account> = all<Account>()
       // 或者 Account::class.all (需要注册Mapper接口)
      ```

2. `filter<实体类>(vararg QueryColumn,QueryCondition.() -> Unit)`: 按条件查泛型对应的表的数据
      > 第一个入参为查询的列，为空时默认查询全部字段
    
      ```kotlin
        // a and b and (c or d)
        val accounts: List<Account> = filter<Account>(Account::id,Account::userName) {
                and(Account::id eq 1)
                and(Account::id.isNotNull)
                and(Account::age `in` (17..19) or { Account::birthday between (start to end) })
            }
      ```

3. `query<实体类>(QueryScope.()->Unit)`: 较复杂查泛型对应的表的数据 (如分组,排序等)

      ```kotlin
        val accounts: List<Account> = query<Account> {
            where{
                and (Account::age `in` (17..19)
                and (Account::birthday between (start to end)))
            } orderBy -Account::id
        }
      ```