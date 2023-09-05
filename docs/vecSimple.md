# 使用实体类进行查询

mybatis-flex的kotlin扩展模块提供了一套名为”查询矢量”的 API，用来从数据库中获取实体对象。它的风格和使用方式与 Kotlin 标准库中的序列 API 极其相似并提供了许多类似的扩展函数。

与flex自身的QueryWrapper不同，查询矢量在操作时并不直接基于apt为我们生成的TableDef类，而是基于实体类中的属性引用（即kotlin反射库中的`KProperty`）进行操作。

## 获取查询矢量

要使用查询矢量，首先要创建查询矢量对象。创建一个查询矢量非常简单，只需要使用函数`vecOf`即可。

```kotlin
val empVec: QueryVector<Emp> = vecOf<Emp>()  // 以实体类Emp创建了一个查询矢量
val accountVec: QueryVector<Account> = vecOf<Account>()  // 以实体类Account创建了一个查询矢量
```

> 注意：并非任何类都能直接使用`vecOf`直接创建查询矢量。这个类**必须拥有其对应的`TableDef`类和继承自`BaseMapper`的Mapper接口**。换言之，以下代码是无法正常运行的：

```kotlin
val vec: QueryVector<Nothing> = vecOf<Nothing>()  // 此处在运行时会抛出异常
```

显然，Nothing类只是kotlin官方库中一个普通类，它并没有对应的TableDef，也没有对应的Mapper接口。在运行时它会抛出`IllegalArgumentException`。

下面是一个使用 `first` 函数从矢量中根据名字获取一个对应的 Emp 对象的例子：

```kotlin
val vec = vecOf<Emp>()
val emp = vec.first { it::name eq "张三" }
```

这种写法十分自然，就像使用 Kotlin 标准库中的函数从一个集合中筛选符合条件的元素一样。
与QueryWrapper不同的是，这里it的类型并不是apt为我们生成的`TableDef`，而是我们自己定义的实体类Emp。name的类型也由QueryColumn变为了`String`。

这解决了QueryWrapper中缺少对列的类型约束的缺点，使得我们的代码在一开始时便更难以出现错误。

`first` 函数接收一个类型为 `(E) -> QueryCondition` 的闭包。其中E为我们定义的实体类，QueryCondition便是flex的查询条件。
这个函数可以帮我们找到表中符合条件的第一条数据并返回，然后将这条数据包装成实体类。

上述代码生成的 SQL 如下：

```sql
SELECT `id`,
       `username`,
       `password`,
       `name`,
       `gender`,
       `image`,
       `job`,
       `entrydate`,
       `dept_id`,
       `create_time`,
       `update_time`
FROM `emp`
WHERE `name` = ? LIMIT 0, 1
```

除了 `first` 函数外，查询矢量还提供了许多方便的函数，如使用 `filter` 对元素进行过滤、使用 `groupBy` 进行分组、使用 `distinct` 进行去重等。

> 注意：在kotlin中使用mybatis-flex进行查询时，我们更推荐使用`QueryVector`来替代QueryWrapper进行查询。
> `QueryVector`能很方便的转换为`QueryWrapper`，且它在使用时，更具有函数式的风格，具备类型限制，因此我们建议大家在kotlin中使用时优先使用。

关于查询矢量更多的内容，请移步[查询矢量介绍](vec.md)