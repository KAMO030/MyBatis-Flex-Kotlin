# 查询向量

在前一节中，我们简单了解了如何使用查询向量获取实体类，现在我们来对它进行更详细的介绍。

## 向量简介

要使用查询向量，首先要创建查询向量对象。创建一个查询向量非常简单，只需要使用函数`vecOf`即可。

```kotlin
val empVec: QueryVector<Emp> = vecOf<Emp>()  // 以实体类Emp创建了一个查询向量
val accountVec: QueryVector<Account> = vecOf<Account>()  // 以实体类Account创建了一个查询向量
```

> 注意：并非任何类都能直接使用`vecOf`直接创建查询向量。这个类**必须拥有对应的继承自`BaseMapper`的Mapper接口**。换言之，以下代码是无法正常运行的：

```kotlin
val vec: QueryVector<Nothing> = vecOf<Nothing>()  // 此处会抛出异常
```

显然，Nothing类只是kotlin官方库中一个普通类，它并没有对应的Mapper接口。在运行时它会抛出`IllegalArgumentException`。

`vecOf` 函数会返回一个 QueryVector 。正如 QueryWrapper 一样，它并不会在创建时就立刻进行一次查询。只有当我们进行终止操作时，它才会立刻执行查询。

我们可以使用 `toList` 扩展函数将向量指向的数据保存为一个列表：

````kotlin
val vec = vecOf<Emp>()
val emps = vec.toList()
````

我们还能在 `toList` 之前，使用 `filter` 扩展函数来进行筛选：

```kotlin
val vec = vecOf<Emp>()
val emps = vec.filter { it::deptId eq 1 }.toList()
```

此时生成的 SQL 会变成：

````sql
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
WHERE `dept_id` = ?
````

> 为了演示方便，在下面的例子中若我们的查询中包括了所有列，那么将直接使用 * 来替代。

我们来看看查询向量 `QueryVector` 的类中的部分成员：

```kotlin
class QueryVector<E : Any>(
    private val entityClass: Class<E>,
    val data: QueryData,
    val entityInstance: E? = null
) {
    val wrapper: QueryWrapper

    val mapper: BaseMapper<E>
}
```

可以看出，查询向量中包含了一个 `QueryWrapper` 类型和一个 `BaseMapper` 类型的属性。而我们的查询正是基于`QueryWrapper`和`BaseMapper`来共同完成。

查询向量中的大部分功能都是以扩展函数的方式实现的，这些扩展函数大致可以分为两类，中间操作和终止操作，它们对应的意思就和flex中 QueryWrapper 对应的操作一样：

- **中间操作：** 这类函数在执行过后仍然可以继续链式地调用扩展函数来完成功能。它们并不会立即执行查询，而是将数据保存起来，并且在调用时重新返回一个携带着新数据的向量，比如`filter`, `distinct`, `groupBy`等。
- **终止操作：** 这类函数的返回值通常是一个集合或者是某个计算的结果，他们会马上执行一个查询，然后获取它的结果并执行一定的运算，比如 `toList`、`funOf` 等。

## 中间操作

中间操作并不会立即查询，它们都返回一个新的向量对象。`QueryVector` 的中间操作主要有如下几个。

### filter

````kotlin
inline fun <E : Any> QueryVector<E>.filter(predicate: (E) -> QueryCondition): QueryVector<E>
````

当我们使用`filter`进行过滤时，相当于`QueryWrapper`中where的调用。

与`kotlin.sequences`的`filter`函数类似，`QueryVector`的`filter`函数也接受一个闭包作为参数，使用闭包中指定的筛选条件令向量过滤对应的数据。
不同的是，我们的闭包接受当前表对象`E`作为参数，因此我们在闭包中使用`it`访问到的是实体对象。

另外，在使用时，我们应当使用对应属性的引用，即kotlin反射库中的`KProperty<*>`作为要过滤的列而不是直接使用属性。下面是使用`filter`获取部门 1 中的所有员工的例子：

```kotlin
val vec = vecOf<Emp>()
val emp = vec.filter { it::deptId eq 1 }.toList()
```

可以看到，用法几乎与`kotlin.sequences`完全一样。`filter`函数还可以连续使用，此时所有条件就和`QueryWrapper`一样将使用 `and` 运算符进行连接，比如：

```kotlin
val vec = vecOf<Emp>()
val emp = vec
    .filter { it::deptId eq 1 }
    .filter { it::name startsWith "张" }
    .toList()
```

生成的 SQL 如下：

````sql
SELECT *
FROM `emp`
WHERE `dept_id` = ?
  AND `name` LIKE ?
````

也许你会疑惑： startsWith 是什么东西？这正是我们为`KProperty<String>`定义的扩展方法。如同`kotlin.String`的同名扩展方法一样，它也能用于查询以我们给定字符串开头的字符串。
在上述的例子中，它实际上是 it::name like `张%` 。

在上面的例子中，我们用到了一个重要的终止操作函数`toList`。它还有另外一个相似的终止操作函数`toRows`，但二者的用途和使用场景并不相同。这两个函数的区别我们将在后面的内容中详细介绍。

### filterColumn, filterColumns, filterProperty, filterProperties

```kotlin
inline fun <E: Any> QueryVector<E>.filterColumn(aggregateFun: QueryFunctions.(E) -> QueryColumn): QueryVector<E>
```

```kotlin
inline fun <E: Any> QueryVector<E>.filterColumns(aggregateFun: QueryFunctions.(E) -> Iterable<QueryColumn>): QueryVector<E>
```

```kotlin
inline fun <E: Any> QueryVector<E>.filterProperty(predicate: (E) -> KProperty<*>): QueryVector<E>
```

```kotlin
inline fun <E: Any> QueryVector<E>.filterProperties(predicate: (E) -> Iterable<KProperty<*>>): QueryVector<E>
```

查询向量默认会查询实体类对象的所有列。如果我们不需要查询所有的列，我们可以用这四个方法来指定我们想要的列。其中，闭包返回值为Iterable的函数可以一次性指定多个列，
而`filterColumn`可以让我们使用flex官方`QueryMethods`中提供的静态方法，或是我们自己定义的一些方法来进行更方便地查询。

下面是一个稍复杂的过滤的例子：

```kotlin
val vec = vecOf<Emp>()
val emp = vec
    .filterProperty { it::id }
    .filterColumn { it::deptId + 1 }
    .filterColumn { QueryMethods.length(it::name.toQueryColumn()) }
    .toRows()
```

在上面的例子中，我们使用`filterProperty`来选择类中的对应列，
在内部，我们会使用`KProperty<*>.toQueryColumn`的扩展方法将其转换为`QueryColumn`来在终止操作时装配`QueryWrapper`中的selectColumns属性。

此外，我们还使用了 it::deptId + 1 中的 + 号的运算符重载，它能够衔接`KProperty`和`Number`，并返回一个`QueryColumn`。我们还重载了`QueryColumn`和`KProperty`，
`QueryColumn`和`Number`的四则运算符号，使得它们能够顺利地衔接。

在最后的过滤中，我们显式的使用了`KProperty<*>.toQueryColumn`的扩展方法，将`KProperty`转换为`QueryColumn`以使用flex官方`QueryMethods`中提供的静态方法。

我们发现，函数`filterProperty`及`filterProperties`的闭包参数的入参是`QueryFunctions.(E)`而非单独一个`E`。这里的`QueryFunctions`是一个kotlin object，里面的函数均来自于`QueryMethods`。
不同的是，`QueryFunctions`中的函数均视为实例方法，因此我们可以更轻松地在其他地方使用。

上面的例子生成的 SQL 如下：

````sql
SELECT `id`, `dept_id` + 1, LENGTH(`name`) FROM `emp`
````

这一步例子中我们使用了`toRows`而非`toList`作为终止操作，它会返回一个`List<Row>`而非`List<Emp>`。这是因为我们要查询的列不足以装配为实体类对象，需要使用`toRows`来返回通用查询结果。
关于`toList`和`toRows`的区别，后续的章节会进行详细的介绍。

### sortedBy

```kotlin
inline fun <E: Any, V: Comparable<V>> QueryVector<E>.sortedBy(order: Order = Order.ASC, sortedBy: (E) -> KProperty<V?>): QueryVector<E>
inline fun <E: Any, V: Comparable<V>> QueryVector<E>.sortedBy(sortedBy: (E) -> Iterable<QueryOrderBy>): QueryVector<E>
```

`sortedBy` 函数用于指定查询结果的排序方式，我们在第一个参数order中传入枚举类`Order`的成员来指定我们需要以什么顺序排序，
在闭包中返回一个我们需要排序的列。下面的代码将按 id 降序排列后得出实体类返回值：

```kotlin
val vec = vecOf<Emp>()
vec.sortedBy(Order.DESC) { it::id }.toList()
```

生成的 SQL 如下：

````sql
SELECT *
FROM `emp`
ORDER BY `id` DESC
````

如果我们需要根据多个列进行排序，我们可以按照下面的例子来进行操作。

下面是一个稍复杂的排序的例子：

```kotlin
val vec = vecOf<Emp>()
vec.sortedBy {
    listOf(
        it::id.toOrd(Order.DESC),
        it::createTime.toOrd(Order.ASC),
        it::updateTime.toOrd()
    )
}
```

生成的 SQL 如下：

````sql
SELECT *
FROM `emp`
ORDER BY `id` DESC, `create_time` ASC, `update_time` ASC
````

我们使用`KProperty`的扩展函数`toOrd`将其转换为flex中用于对排序的抽象`QueryOrderBy`，并在函数中传入了枚举类`Order`的成员用于指明排序的方向。
其中`toOrd`带有一个默认参数`Order.ASC`，如果我们不提供这个参数，那么它将默认按照升序进行排序。

### drop/take, limit

```kotlin
fun <E: Any> QueryVector<E>.drop(index: Long): QueryVector<E>
fun <E: Any> QueryVector<E>.take(index: Long): QueryVector<E>
```

```kotlin
fun <E: Any> QueryVector<E>.limit(offset: Long, rows: Long): QueryVector<E>
```

`drop` 和 `take` 函数用于实现分页的功能，`drop` 函数会丢弃序列中的前 n 个元素，在 SQL 中相当于指定了行偏移量，`take` 函数会保留前 n 个元素丢弃后面的元素，在 SQL 中相当于指定了行数。

> 注意：仅使用`drop`而不使用`take`是无效的，因为在 SQL 中我们并不能只指定行偏移量而不指定行数。

下面是一个使用`drop`和`take`例子：

```kotlin
val vec = vecOf<Emp>()
vec.drop(1).take(1).toList()
```

如果我们使用 MySQL 数据库，会生成如下 SQL ：

````mysql
SELECT *
FROM `emp`
LIMIT 1, 1
````

需要注意的是，这两个函数依赖于数据库本身的分页功能，然而每种数据库提供商对其都有不同的实现，
因此在实际生成 SQL 语句时，不同的数据库可能会有不同的区别。

相比较于具有函数式风格的`drop`和`take`，我们更建议直接使用`limit`来完成分页查询。

在上面的例子中，如果我们使用`limit`来完成分页查询，我们只需要这么写：

```kotlin
val vec = vecOf<Emp>()
vec.limit(1, 1).toList()
```

两种方法生成的 SQL 语句均是完全相同的。如果我们只想指定行数而不需要指定行偏移量，那么只需使用`take`函数即可。

### distinct

如同`kotlin.sequence`一样，`QueryVector`同样支持去重操作。函数`distinct`正是用于完成去重操作的。

> 注意：该功能目前是实验性的。如果你需要使用，你需要在使用处添加上`@OptIn(ExperimentalDistinct::class)`来使用，或添加上`@ExperimentalDistinct`注解将其传播，否则无法通过编译。

> 截至此文档编写前，flex官方（版本1.6.5）并未提供统一的去重实现，而是在`QueryMethods`提供了一个用于去重的函数`distinct`。
> 这个函数并不能满足我们的需求，因此我们编写了一个`DistinctQueryWrapper`来实现我们的功能。

```kotlin
fun <E: Any> QueryVector<E>.distinct(): QueryVector<E>
```

当调用`distinct`后，我们将会让最终结果去重。

```kotlin
val vec = vecOf<Emp>()
vec.distinct().toList()
```

生成的 SQL 如下：

```sql
SELECT DISTINCT `emp`.* FROM `emp`
```

你会发现，之前在 SELECT 中的一大串的列变成了一个通配符`*`。这是我们设计上的妥协，关于`DistinctQueryWrapper`是如何工作的，请直接查阅此类的注释，这里便不再赘述。

## 终止操作

查询向量的终止操作会立刻计算结果，完成查询并返回。

### toList, toRows

```kotlin
fun <E : Any> QueryVector<E>.toList(): List<E>
```

```kotlin
fun <E : Any> QueryVector<E>.toRows(): List<Row>
```

很明显，它们的返回值很相似。但是它们的应用场景并不相同。

下面是两个具体讲述它们区别的例子：

```kotlin
val vec = vecOf<Emp>()
val emp = vec
    .filter { it::name startsWith "张" }
    .filter { it::password eq 123456 }
    .filter { it::entrydate between LocalDate.of(2020, 1, 1)..LocalDate.now() }
    .distinct()
    .limit(0, 1)
    .toList()
```

```kotlin
val vec = vecOf<Emp>()
val emp = vec
    .filterProperty { it::id }
    .filterProperty { it::job }
    .filter { it::id gt 5 }
    .toRows()
```

在上面的两个例子中，第一个例子尽管使用了诸多的扩展函数，如`filter`, `distinct`, `limit`等，但 **它并没有指定我们要查询的列。换言之，它查询了所有列**。
此时我们返回的结果中包含了所有的列，而这些列足以向量装配成我们自己定义的实体类，因此我们可以使用`toList`来返回一个列表。

在第二个例子中，我们**指定了我们要查询的列。换言之，我们没有查询所有的列**。此时我们查询得到的列中**不足以为实体类中的每个属性进行装配**。
此时如果使用`toList`进行返回，那么**实体类中没有被查询的列的属性的值将会是我们定义时的默认值**。
这样的实体类在大多数情况下未必是我们想要的结果，而我们也仅仅需要被查询的列的结果而已。此时我们可以使用`toRows`方法来仅返回我们想要的结果。

也许你会疑惑：`Row`是什么？`Row`是flex官方定义的通用的用于返回查询结果的结果集，它是`java.util.LinkedHashMap`的子类，其中键的类型是`String`，而值的类型是`Any`。
在第二个例子中，如果我们使用`println`函数输出`emp`，那么它的结果是这样的：

```
[{id=6, job=3}, {id=7, job=1}, {id=8, job=1}, {id=9, job=1}, {id=10, job=1}, {id=18, job=1}, {id=19, job=1}, {id=21}]
```

我们会发现，在返回的 List 当中，最后一个 job 没有被返回。这是因为最后一条数据在 SQL 中 job 的值为`null`。因此在使用`toRows`时需要格外注意。

### elementAt/first/last/find/findLast/get

这一系列函数用于获取序列中指定位置的元素，它们的用法也与 `kotlin.sequences` 的同名函数一模一样，具体可以参考 Kotlin 标准库的相关文档。

这些函数会使用分页功能，只查询一条数据。假如我们使用 MySQL ，并且使用 `elementAt(10)` 获取下标为 10 的记录的话，会生成 `limit 10, 1` 这样的 SQL 。

另外，除了基本的形式外，这些函数还具有许多的变体，这里就不一一列举了。

### maxOf/maxBy/minOf/minBy/sumOf/avgOf

这些函数的名字非常相似，甚至有两个与max有关的函数和与min有关的函数。我们以`maxBy`和`maxOf`为例，讲述它们的区别。下面这两个函数的定义：

```kotlin
inline fun <reified E : Any, reified R : Comparable<R>> QueryVector<E>.maxOf(selector: (E) -> KProperty<R?>): R
inline fun <reified E : Any, reified R : Comparable<R>> QueryVector<E>.maxBy(selector: (E) -> KProperty<R?>): E?
```

很显然，它们函数签名上的区别仅仅只有名称不同和返回值类型不同。`maxOf`返回的类型是对应列的**属性类型**，而`maxBy`返回的是**实体类对象**。

下面将通过一个示例讲述它们的区别：

```kotlin
val vec = vecOf<Emp>()
println(vec.maxOf { it::id })
println(vec.maxBy { it::id })
```

`maxOf`函数生成的 SQL 如下：

```sql
SELECT MAX(`id`) FROM `emp`
```

而`maxBy`函数生成的 SQL 如下：

```sql
SELECT *
FROM `emp`
WHERE `id` = (SELECT MAX(`id`) FROM `emp`)
LIMIT 0, 1
```

我们可以很明显地发现，`maxOf`只根据列 id 查询了这一列中的最大值，而`maxBy`则在前者的基础上，还把最大值所在行的所有数据查询出来并装配成实体类对象。

由此我们可以得出以 Of 结尾的函数的作用，就是**查询对应列中的对应值**，而以 By 结尾的函数，则**在前者的基础上使用子查询，将其所在行的所有列也一并查询并装配成实体类。**

`minOf`, `minBy`用于查询最小值。`avgOf`则是平均值，它和用于查询总和的`sumOf`一样都要求我们的列是`Number`的子类。换言之，只有对应的属性是`Number`的子类时，才能使用。