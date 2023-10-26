# MyBatis-Flex KSP 介绍及其配置

在纯 Kotlin/JVM 环境或是 Kotlin 和 Java 的混合环境下，也许我们更希望项目在编译时生成的是 Kotlin 代码而非 Java 代码。
如果您的项目**使用 gradle 进行构建**，而您恰好有这样的需求，那么 KSP 将是一个更好的选择。

KSP 生成的代码结构与 APT 近乎一致，并且在 Kotlin 的角度充分考虑了兼容性。如有需要，您可以放心大胆地把先有的 APT 更换成 KSP，然后根据此文档做进一步地了解和配置。

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
    /**
     * 主键
     * @return id
     */
    public val ID: QueryColumn = QueryColumn(this, "id")

    public val USER_NAME: QueryColumn = QueryColumn(this, "user_name")

    public val AGE: QueryColumn = QueryColumn(this, "age")

    public val BIRTHDAY: QueryColumn = QueryColumn(this, "birthday")

    public val ALL_COLUMNS: QueryColumn = QueryColumn(this, "*")

    public val DEFAULT_COLUMNS: List<QueryColumn> = listOf(`ID`, `USER_NAME`, `AGE`, `BIRTHDAY`)

    @JvmField
    public val ACCOUNT: AccountTableDef = AccountTableDef

    /**
     * A constructor used to mock objects for code compatibility. It returns itself on each call.
     */
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun invoke() = this
}
```

对比于 APT 生成的产物，我们可以很明显地发现，
KSP 的产物中，`AccountTableDef` 是一个 Kotlin 对象，
而 APT 的产物中，`AccountTableDef` 是一个普通的 Java 类。

`AccountTableDef` 还拥有一个 `invoke` 方法，和一个 `ACCOUNT` 属性。
这个方法和属性均是为了兼容 APT 而生， 以使得我们不需要做太多的改动即可从 APT 迁移到 KSP。

此外，`AccountTableDef` 生成的 `DEFAULT_COLUMNS` 的类型由 `Array` 变为了 `List`。
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

而第二个参数 `flex.root.project.path` 用于指定您的项目最外层的项目所在。
这个参数的作用是用于 `processor.stopBubbling` 配置以告知 KSP 向上级合并配置时应该到哪里结束合并。
因此如果您没有合并配置的需求，您可以不写此参数。

### KSP 配置与 APT 配置的异同点及其相关说明

KSP 几乎完全兼容 APT 的配置。而且我们针对一些配置做了进一步地改善和增强。接下来我们来一起看看 KSP 究竟做了哪些优化。

1. **processor.enable**

此配置用于指定是否启用 KSP 。我们的 KSP 对此配置实现了完全的支持。
但我们不建议您这样做，而是更推荐您将此配置项写入 `build.gradle` 文件中，或通过其他方法来决定是否关闭 KSP。

您应该在 `build.gradle` 文件中进行如下配置，以决定是否启用 KSP：

【Groovy】/【Kotlin】
```
ksp {
    arg("flex.ksp.enable", "false")
}
```

我们知道，如果我们不想让 KSP 为 flex 生成代码，那么 `mybatis-flex.config` 配置文件中的内容对于 KSP 来说也是毫无意义的。
在 `build.gradle` 文件中进行配置，可以使得 KSP 在一开始便知道不需要为 flex 生成代码，因此可以节省很多诸如读取配置文件等不必要的性能开销。

