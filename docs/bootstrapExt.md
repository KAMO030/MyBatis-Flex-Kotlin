## runFlex:简化配置，一键启动
利用Kotlin的Dsl，重载运算符和中缀表达式，简化了MybatisFlexBootstrap的配置，用起来更加简单方便：

> Tips:
> 如果是SpringBoot等容器环境，无需使用此方法，请参考[核心库配置](https://mybatis-flex.com/zh/base/configuration.html)
```kotlin
runFlex {
    //  此方法体 it 是 MybatisFlexBootstrap 实例
    //  配置Mapper
    //  1.通过+（重写自增）的方式 
    +AccountMapper::class
    //  2.通过原始的方式
    //  it.addMapper(AccountMapper::class.java)
    //  3.通过扫描包路径自动注册 
    //  接口需要继承BaseMapper或打上@Mapper注解, 开启@Mapper注解扫描需要修改第二个参数（needScanAnnotated）为true
    scanPackages("com.mybatisflex.kotlin.example.mapper")


    //  配置单dataSource
    //   1.通过+（重写自增）的方式
    +dataSource
    //  2.通过原始的方式
    //  it.setDataSource(dataSource)
    //  3.通过dsl的方式配置简易的内置数据源
    //  defaultPooledDataSources {
    //    driver可以不写，默认为第一个注册的驱动
    //    driver = com.mysql.cj.jdbc.Driver::class
    //    url = "xxx"
    //    username = "xxx"
    //    password = "xxx"
    //  }

    //  配置多dataSource
    //  1.通过of（中缀）的方式
    //  FlexConsts.NAME of dataSource
    //  "dataSource1" of dataSource
    //  "dataSource2" of dataSource
    //  2.通过dsl的方式配置简易的内置数据源
    //  defaultPooledDataSources("name") {
    //    driver可以不写，默认为第一个注册的驱动
    //    driver = com.mysql.cj.jdbc.Driver::class
    //    url = "xxx"
    //    username = "xxx"
    //    password = "xxx"
    //  }
    //  3.通过原始的方式
    //  it.addDataSource(FlexConsts.NAME,dataSource)

    //  配置日志打印在控制台
    logImpl = StdOutImpl::class
}
```