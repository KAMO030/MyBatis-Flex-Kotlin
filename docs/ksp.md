# MyBatis-Flex KSP 介绍及其配置

在 Kotlin/JVM 项目中，也许我们更希望项目在编译时生成的 `TableDef` 类是 Kotlin 代码而非 Java 代码。
如果您的项目**使用 gradle 进行构建**，而您恰好有这样的需求，那么 KSP 将是一个更好的选择。

MyBatis-Flex KSP 生成的代码结构与 MyBatis-Flex APT 近乎一致，并且在 Kotlin 的角度充分考虑了兼容性。如有需要，您可以放心大胆地把先有的 APT 更换成 KSP，然后根据此文档做进一步地了解和配置。

## 什么是 KSP ？为什么使用 KSP ？

[KSP](https://github.com/google/ksp)，全名 Kotlin Symbol Processor ，是一个专注于 Kotlin 代码的编译时工具，是以 Kotlin 优先的
kapt 替代方案。
KSP 在处理 Kotlin 代码时的速度和性能，对比于 Kapt
会有[显著提升](https://android-developers.googleblog.com/2021/09/accelerated-kotlin-build-times-with.html)。

因此如果您的项目**使用 gradle 进行构建**，并且希望生成的是 Kotlin 代码，那么 KSP 将会是更好的选择。

> 注意：截至此文档编写时，**KSP 尚不支持 Maven 项目**。对于 Maven 项目，您需要使用 Kapt 来帮助您生成代码。
>
> 关于 KSP 的更详细介绍，您可以在[官网](https://book.kotlincn.net/text/ksp-why-ksp.html)，
> 或[安卓开发者网站](https://developer.android.google.cn/studio/build/migrate-to-ksp?hl=zh-cn#groovy)获得更多信息，此处便不再赘述。

## 为您的项目配置 KSP 插件

首先，我们需要在顶级 `build.gradle` 配置文件中声明 KSP 插件。
请务必选择**与项目的 Kotlin 版本一致的 KSP 版本。** 您可以在[KSP GitHub 页面](https://github.com/google/ksp/releases)
上找到对应的版本列表。

这里以 Kotlin 版本 1.9.10 为例。

【Kotlin】

```kotlin
plugins {
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
}
```

【Groovy】

```groovy
plugins {
    id 'com.google.devtools.ksp' version '1.9.10-1.0.13' apply false
}
```

然后，您可以在模块级 `build.gradle` 配置文件中启用 KSP：

【Kotlin】

```kotlin
plugins {
    id("com.google.devtools.ksp")
}
```

【Groovy】

```groovy
plugins {
    id 'com.google.devtools.ksp'
}
```

## 启用 MyBatis-Flex KSP

启用 KSP 插件后，您只需要在依赖项声明中声明为 MyBatis-Flex 编写的 KSP 即可。

【Kotlin】

```kotlin
dependencies {
    ksp("com.mybatis-flex:mybatis-flex-kotlin-ksp:1.0.0")
}
```

【Groovy】

```groovy
dependencies {
    ksp 'com.mybatis-flex:mybatis-flex-kotlin-ksp:1.0.0'
}
```

## KSP 生成代码示例

KSP 与 APT 的产物的结构近乎一致，但仍有一定的区别。我们用一个例子来详细说明这个区别。

我们假设有这样的一个实体类：

```kotlin
@Table("tb_account")
data class Account(
    /**
     * 主键
     * @return id
     */
    @Id var id: Int = -1,
    var userName: String? = null,
    var age: Int? = null,
    var birthday: Date? = null,
)
```

对于这样的实体类，KSP 生成的代码如下：

```kotlin
@file:Suppress("RedundantVisibilityModifier", "MemberVisibilityCanBePrivate", "unused", "RemoveRedundantBackticks")

public object AccountTableDef : TableDef("", "tb_account") {
    @JvmField
    public val ID: QueryColumn = QueryColumn(this,  "ID")

    @JvmField
    public val USER_NAME: QueryColumn = QueryColumn(this,  "USER_NAME")

    @JvmField
    public val AGE: QueryColumn = QueryColumn(this,  "AGE")

    @JvmField
    public val BIRTHDAY: QueryColumn = QueryColumn(this,  "BIRTHDAY")

    @JvmField
    public val ALL_COLUMNS: QueryColumn = QueryColumn(this, "*")

    @JvmField
    public val DEFAULT_COLUMNS: List<QueryColumn> = listOf(`ID`,`USER_NAME`,`AGE`,`BIRTHDAY`)

    @JvmField
    public val ACCOUNT: AccountTableDef = AccountTableDef
}
```

对比于 APT 生成的产物，我们可以很明显地发现以下区别：

1. KSP 的产物中，`AccountTableDef` 是一个 Kotlin 对象，而 APT 的产物中，`AccountTableDef` 是一个普通的 Java 类。公共构造器被移除了。

2. `AccountTableDef` 拥有一个指向自身的 `ACCOUNT` 属性。这个属性是为了兼容 APT 而生，以使得我们不需要做太多的改动即可从 APT 迁移到 KSP。

3. `AccountTableDef` 生成的 `DEFAULT_COLUMNS` 的类型由 `Array` 变为了 `List`。 
这是为了符合 Kotlin 语言常用习惯而特意做的修改。如果您确实需要一个 `Array` ，您可以在稍后的 KSP 配置部分进行配置。

## KSP 相关配置

### 使 KSP 感知 `mybatis-flex.config` 配置文件

KSP 在编写之初就充分考虑了如何兼容 APT 现有配置这一问题。因此，您可以在几乎不需要做任何操作便可以使 KSP 使用您已经编写好的 APT 配置，并如期生成对应的代码。

我们知道，APT 的相关配置需要写在项目根目录下的 `mybatis-flex.config` 文件中。因此，要使得 KSP 能够感知这些配置，
您需要在 `build.gradle` 文件中进行如下配置，以告诉 KSP 您的根目录在哪里。

【Groovy】/【Kotlin】
```
ksp {
    arg("flex.project.path", project.projectDir.absolutePath)
    arg("flex.root.project.path", rootProject.projectDir.absolutePath)
}
```

这里我们将两个参数传入至 KSP 中。其中第一个参数 `flex.project.path` 用于告诉 KSP 当前项目根目录的路径。

而第二个参数 `flex.root.project.path` 用于指定您的根项目的绝对路径。
这个参数的作用是用于 `processor.stopBubbling` 配置以告知 KSP 向上级合并配置时应该到哪里结束合并，
因此如果您没有合并配置的需求，您不必写此参数。

### KSP 配置与 APT 配置的异同点及其相关说明

KSP 几乎完全兼容 APT 的配置。此外，我们针对一些配置还做了进一步地改善和增强。

接下来我们来一起看看对于 KSP 而言不支持的或做了改动的配置。

> 对于下面未曾提到的 APT 配置，KSP 均完全支持。

#### processor.enable

此配置用于指定是否启用 KSP 。我们的 KSP 对此配置实现了完全的支持。
但我们不建议您这样做，而是更推荐您**直接将依赖项中我们的 KSP 行注释掉它或者直接删除它**。这样做最为简单省事，也能更好地节省性能开销。

如果您不想这么做，那么我们推荐您在 `build.gradle` 文件中进行如下配置，以决定是否启用 KSP：

【Groovy】/【Kotlin】
```
ksp {
    arg("flex.ksp.enable", "false")
}
```

我们知道，如果我们不想让 KSP 为 flex 生成代码，那么 `mybatis-flex.config` 配置文件中的内容对于 KSP 来说也是毫无意义的。
在 `build.gradle` 文件中进行配置，可以使得 KSP 在一开始时便知道不需要为 flex 生成代码，因此可以避免很多诸如读取配置文件等不必要的操作以节省性能开销。

#### processor.stopBubbling

此配置用于指定是否停止向上级合并配置。KSP 对此完全支持。

对于所有 `mybatis-flex.config` 中的配置，在向上合并时，对于重复的键，KSP 会采取以下策略：

1. 对于仅能指定一个值的键，KSP 会覆盖原有的值。
2. 对于能够指定多个值的键，例如 `processor.tableDef.ignoreEntitySuffixes`，KSP 会将原有的值与新指定的值进行合并。即这些键的值最终都会成为处理的一部分。

#### processor.genPath

不支持。

#### processor.allInTables.className

KSP 为 `Tables` 类生成的是单例对象，即 `object` ，而不是普通类。此配置 KSP 对此完全支持。

#### processor.allInTables.enable, processor.allInTables.package

如果您指定 KSP 为您生成 `Tables` 单例（即 `processor.allInTables.enable` 的值为 `true`），而又不指定其包名或指定了包名但包名非法，
那么 KSP 会向您发出警告，并且不会生成 `Tables` 类。

#### processor.tableDef.propertiesNameStyle

除了原本的四种风格外，KSP 还支持原始风格。这种风格可以使得 KSP 生成的字段对比于实体类不做任何改变。
假设您在实体类中定义的其中一个属性的名字为 `<!-- 非法的属性名 --!>`，那么 KSP 生成的对应字段的名字也是 `<!-- 非法的属性名 --!>`。

### KSP 配置

KSP 在兼容 APT 配置的基础上，还增添了一些自己独有的配置。下面我们来一起看一看。

#### ksp.type.defaultColumns

在前面 KSP 和 APT 的产物对比中，我们知道 KSP 生成的 `DEFAULT_COLUMNS` 属性的类型由 APT 的 `Array` 变为了 `List`。
如果您确实需要一个 `Array` ，或是其他的类型，您可以在此配置。

我们可以在 `mybatis-flex.config` 配置文件中进行如下配置：

ksp.type.defaultColumns=array

我们一共支持以下四种配置：
1. array，KSP 为此生成的类型将变为 `Array`。
2. list，KSP 为此生成的类型将变为 `List`。
3. set，KSP 为此生成的类型将变为 `Set`。
4. sequence，KSP 为此生成的类型将变为 `Sequence`。

> 需要注意的是，如果你选择的是 array ，那么其泛型将会是**协变的**。
即 `DEFAULT_COLUMNS` 完整的类型描述是 `Array<out QueryColumn>` 。因此，您可能需要在源代码中进行一些调整。
