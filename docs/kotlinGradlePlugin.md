# Mybatis-Flex Kotlin Gradle Plugin

> Kotlin Plugin for Mybatis-Flex
>
> 此插件主要是为在使用 Kotlin 进行 Mybatis-Flex 开发时提供兼容支持

### 为什么需要此插件？

* 在 Mybatis 中，会大量使用反射构建实体类, 反射构建实体类时需要调用无参数的构造函数,
  如果实体类没有无参数的构造函数, 再在调用有参数构造时,因反射无法得到构造参数的参数名导致无法正确映射数据库的数据从而无法构造对象;
* 在 Mybatis-Flex 中, 使用 `UpdateChain` 或 扩展模块的 `UpdateScope` 时, 会动态生产实体类的子类来进行动态代理暂存数据;

> 恰巧 Kotlin 中常被使用作为实体类的 `data class` 他是默认没有无参数的构造函数,并且无法被继承的 final class ,
> 所以我们需要使用 Kotlin 官方提供的插件来帮我们在编译时
> 给@Table注解的实体类添加无参数的构造函数( [noarg](https://kotlinlang.org/docs/no-arg-plugin.html) )
> 与打开继承( [allopen](https://kotlinlang.org/docs/all-open-plugin.html) )

### 此插件的作用:

整合了 `noarg` 与 `allopen` 插件对添加 `@Table` 注解的实体类生成无参数的构造函数与打开继承

### 使用方式:

添加依赖:

**【Kotlin】**

```kotlin
plugins {
    id("com.mybatis-flex.kotlin") version "$version"
}
```

**【Groovy】**

```groovy
plugins {
    id 'com.mybatis-flex.kotlin' version '$version'
}
```

### Kotlin 官方插件配置:

> 如果不使用本插件, 想使用 Kotlin 官方插件单独自定义配置, 请参考如下配置:

**【Kotlin】**

```kotlin
plugins {
  kotlin("plugin.allopen") version "$kotlin_version"
  kotlin("plugin.noArg") version "$kotlin_version"
}

noArg {
  annotation("com.mybatisflex.annotation.Table")
}

allOpen {
  annotation("com.mybatisflex.annotation.Table")
}
```

**【Groovy】**

```groovy
plugins {
  id "org.jetbrains.kotlin.plugin.allopen" version "$kotlin_version"
  id "org.jetbrains.kotlin.plugin.noArg" version "$kotlin_version"
}

noArg {
  annotation("com.mybatisflex.annotation.Table")
}

allOpen {
  annotation("com.mybatisflex.annotation.Table")
}
```

