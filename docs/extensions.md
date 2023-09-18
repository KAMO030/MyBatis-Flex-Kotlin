# 简单查询与扩展

## 快速获取mapper与tableInfo
- **mapper**
  - 通过mapper接口类型作为泛型调用 mapper() 方法直接获取
    ````kotlin
     val accountMapper: AccountMapper = mapper()
    ````
  - 通过实体型 KClass 的 baseMapper 属性直接获取
    >注意：此方式获得的实例类型是 BaseMapper<Account(实体类型)>
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
