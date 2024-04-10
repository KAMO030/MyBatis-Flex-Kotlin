# 简单查询与扩展

> Tips:
> * 阅读本文档时请已核心库使用文档为主，此文档为辅，扩展模块只是基于核心库的提供了符合Kotlin的便捷函数, 并不是另立门户，二者可以混用。
> * xxxWith命名的方法通常入参数为: `condition: () -> QueryCondition` 条件构造函数
> * 有复杂业务场景时请注册自定义Mapper接口，否则可能会导致部分功能无法使用，例如@Table所配置的xxxListener
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
   > 除此之外还有queryOne(只查一个, 自核心库1.7.7起会自动limit 1)，
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
   > paginateAs(带类型转换的分页)

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
   > * 更新的实体类时如果有注册此实体类对象的Mapper，那么实体类需要是open class (核心库对实体类使用了动态代理)
   > * 用set子查询时flex核心库低版本存在[BUG](https://gitee.com/mybatis-flex/mybatis-flex/issues/I96XJA)
       会导致子查询参数丢失问题，需要更新核心库到1.8.4及以上版本
   > * 在不注册自定义Mapper时，用set子查询时flex核心库低版本存在BUG会导致子查询参数丢失问题，需要更新核心库到1.8.7及以上版本
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

## 删除

```kotlin
    @Test
fun testDelete() {
   // 根据返回的条件删除
   deleteWith<Account> { Account::id eq 2 }
   // 根据主键删除
   deleteById<Account>(2)
   // 通过map的key对应的字段比较删除
   deleteByMap(Account::id to 2)
   // 根据aseMapper删除 (需要注册Mapper接口))
   mapper<AccountMapper>().deleteByCondition { Account::id eq 2 }
   // 根据Model的id删除 (需要注册Mapper接口))
   Account(id = 2).removeById()
   all<Account>().forEach(::println)
}
```

> 注意：
> * `deleteById`方法如果是多个主键的情况下，请直接传入多个例如: deleteById(1,"zs",100)
> * 如果没有注册自定义Mapper情况下，在使用`deleteById`方法时需要注意实体类中主键的顺序与传入的id顺序一致

## 插入

1. `insert(E): Int`: 插入单条数据 （无需注册Mapper接口）
    ```kotlin
        insert(Account(3, "kamo", 20, Date()))
    ```
2. `inline fun <reified E : MapperModel<E>> save(build: E.() -> Unit): Boolean`: 插入单条数据,需要类型是MapperModel的子类并注册了Mapper接口
    ```kotlin
        save<Account> {
            id = 3
            userName = "kamo"
            age = 20
            birthday = Date()
        }
    ```
   > 注意: 无匹配的构造方法时可能没办法自动new对象导致错误，可以添加无参构造解决

   > Tips: 此方法命名为save而不是insert，是因为实体类型必须是MapperModel的子类，统一MapperModel的命名
3. 使用原生的baseMapper的扩展方法查询:
      ```kotlin
        Account::class.baseMapper.insert(Account(3, "kamo", 20, Date()))
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

## 更多可能性

> Tips: 以下用法只是基于语言特性提供的参考，具体是否适用，请根据实际需求自行判断。

1. 可以将SQL操作抽离到Mapper接口中，然后在Mapper接口中使用`query`,`filter`等方法:
    ````kotlin
    @JvmDefaultWithCompatibility
    interface AccountMapper : BaseMapper<Account> {
        fun findByAge(age: Int, vararg ids: Int): List<Account> = filter {
            allAnd(
                Account::age eq age,
                (Account::id `in` ids.asList()).`when`(ids.isNotEmpty())
            )
        }
    }
   fun main() {
       mapper<AccountMapper>.findByAge(18, 1, 2, 3)
   }
   ````
   > 注意：此写法必须要打上`@JvmDefaultWithCompatibility`注解，否在不会生成接口的默认实现方法，导致报错
2. 可以将SQL操作写到实体类的伴生对象中，然后通过实体类调用使用`query`,`filter`等方法:
    ````kotlin
    @Table("tb_account")
    class Account(
        @Id var id: Int = -1,
        var userName: String? = null,
        var age: Int? = null,
        var birthday: Date? = null,
    ) {
        companion object  {
            fun findByAge2(age: Int, vararg ids: Int): List<Account> = query {
                whereWith {
                    allAnd(
                        Account::age eq age,
                        (Account::id `in` ids.asList()).`when`(ids.isNotEmpty())
                    )
                }
            }
        }
   }
   fun main() {
       Account.findByAge(18, 1, 2, 3)
   }
   ````
   > 比较适用于没有定义Mapper接口的情况

3. 可以使用委托让SQL操作都挂在实体类上方便调用:
    ````kotlin
    @Table("tb_account")
    class Account(
        @Id var id: Int = -1,
        var userName: String? = null,
        var age: Int? = null,
        var birthday: Date? = null,
    ) {
        companion object : AccountMapper by mapper()
   }
   fun main() {
        // from BaseMapper
        Account.selectListByCondition(Account::age eq 18 and Account::id.`in`(1))
   }
   ````

以上三种用法可以同时存在:

````kotlin
    fun main() {
    // from AccountMapper
    Account.findByAge(18, 1)
    // from Account
    Account.findByAge2(18, 1)
    // from BaseMapper
    Account.selectListByCondition(Account::age eq 18 and Account::id.`in`(1))
}
   ````
