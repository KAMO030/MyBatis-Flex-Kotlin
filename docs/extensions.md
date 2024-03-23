# 简单查询与扩展

> Tips: 阅读本文档时请已核心库使用文档为主，此文档为辅，扩展模块只是基于核心库的提供了符合Kotlin的便捷函数,
> 并不是另立门户，二者可以混用。

## 获取mapper与tableInfo

- **mapper**
    - 通过mapper接口类型作为泛型调用 `mapper<M>()` 方法直接获取
      > 注意：范型M为Mapper接口类型，不是实体类型
      ````kotlin
       val accountMapper: AccountMapper = mapper()
      ````
    - 通过实体型 `KClass` 的 `baseMapper` 属性直接获取
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

## 查询

1. `all<E>(): List<E>` : 查泛型对应的表的所有数据 （无需注册Mapper接口）

      ```kotlin
       val accounts: List<Account> = all()
       // 或者 Account::class.all (需要注册Mapper接口)
      ```

2. `filter<E>(vararg KProperty<*>, () -> QueryCondition): List<E>`: 按条件查泛型对应的表的数据 （无需注册Mapper接口）
    * 默认查所有列，可通过 `vararg KProperty<*>` 指定要查的列

   > 除此之外还有filterOne(只查一个)，filterColumn(第一个入参改为Column)，filterOneColumn(前两者结合)

      ```kotlin
        // a and b and (c or d)
        val accounts: List<Account> = filter(Account::id,Account::userName) {
                (Account::id eq 1)
                .and(Account::id.isNotNull)
                .and(Account::age `in` (17..19) or { Account::birthday between (start to end) })
            }
      ```

3. `query<E>(QueryScope.() -> Unit): List<E>`: 较复杂查泛型对应的表的数据 (如分组,分页,排序等)（无需注册Mapper接口）
   > 除此之外还有queryOne(只查一个)，
   > queryRows(强制走默认RowMapper返回值为Row)，
   > queryRow(前两者结合)

      ```kotlin
        val accounts: List<Account> = query {
            select(Account::id, Account::userName)
            where {
                 Account::age `in` (17..19) and (Account::birthday between (start to end))
            }orderBy -Account::id
            limit(2)
        }
      ```
4. `paginate(
   pageNumber: Number,
   pageSize: Number,
   totalRow: Number? = null,
   init: QueryScope.() -> Unit
   ): Page<E>` : 分页查询 （无需注册Mapper接口）
   > 除此之外还有paginateWith(简单按条件分页)，
   > paginateRows(返回值为Row)

      ```kotlin
        val accounts: List<Account> = paginate<Account>(1, 10) {
            select(Account::id, Account::userName)
            orderBy(-Account::id)
        }
      ```
5. 使用原生的baseMapper的扩展方法查询:
      ```kotlin
        Account::class.baseMapper.query {
            select(Account::id, Account::userName)
            whereWith { Account::age ge 18 }
        }
      ```

## 更新

1. `update(scope: UpdateScope<E>.() -> Unit): Int`: 更新单表数据 （无需注册Mapper接口）
      ```kotlin
        update<Account> {
            Account::id set 5
            whereWith { Account::id eq 1 and (Account::userName eq "张三") }
        }
      ```
   其中set方法后可以接QueryWrapper，QueryScope类型的参数用于设置子查询
      ```kotlin
            Account::age set queryScope(Account::age.column){
                from(Account::class)
                and(Account::age `in` (19..20)) 
                limit(1)
            }
      ```
   或者使用更便捷地的setRow方法:
      ```kotlin
            Account::age.setRow(Account::age){
                from(Account::class)
                and(Account::age `in` (19..20)) 
            }
      ```
   > setRow方法会自动select单列并limit 1，所以不需要再使用limit方法

   如果有需求可以写成以下方式，此时会执行两次sql
      ```kotlin
           Account::age set filterOne<Account>(Account::age){
                Account::age `in` (19..20)
            }?.age
      ```
   > 注意：
   > * 更新的实体类必须是open class，例如data class会报错，因为核心库中的`UpdateWrapper`使用了动态代理，而data
       class的构造函数是final，从而导致的
   > * 用set子查询时flex核心库低版本存在[BUG](https://gitee.com/mybatis-flex/mybatis-flex/issues/I96XJA)
       会导致子查询参数丢失问题，需要更新核心库到1.8.2以上(不包含)版本
2. 使用原生的baseMapper的扩展方法更新:
      ```kotlin
        val account = Account(
            id = 5,
            // 此时会执行一次sql
            age = filterOne<Account>(Account::age) {
                Account::age `in` (19..20)
            }?.age
        )
        Account::class.baseMapper.updateByCondition(account){
            Account::age `in` (19..20)
        }
      ```

## 操作符

1. `and`，`or` 等条件关联的中缀方法，返回值为`QueryCondition`
2. `eq`，`ne`，`gt`，`ge`，`lt`，`le`，`in`，`between` 等条件构建符的中缀方法，返回值为`QueryCondition`
3. `allAnd`，`allOr` 多条件相同关联的构建方法，返回值为`QueryCondition`
4. 基于第三点扩展`QueryCondition`和`QueryWrapper`的`andAll`，`orAll` 方法，返回值为`QueryCondition`或`QueryWrapper`
    ````kotlin
        query<Account> {
            andAll(
                Account::id eq 1,
                Account::age le 18,
                Account::userName eq "张三",
            )
        }
   ````
5. `inTriple`，`inPair` 构建多属性组合 IN ，返回值为`QueryCondition`
    ````kotlin
      filter<Account> {
        (Account::id to Account::userName to Account::age).inTriple(
            1 to "张三" to 18,
            2 to "李四" to 19,
        )
      }
   ````
   执行的SQL:
      ```sql
      SELECT * FROM `tb_account`
      WHERE (`id` = 1 AND `user_name` = '张三' AND `age` = 18) 
         OR (`id` = 2 AND `user_name` = '李四' AND `age` = 19)
      ```