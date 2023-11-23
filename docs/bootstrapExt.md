## Bootstrap简化配置
利用Kotlin的Dsl，重载运算符和中缀表达式，简化了MybatisFlexBootstrap的配置，用起来更加简单方便：
```kotlin
runFlex {
    // 此方法体 it 是 MybatisFlexBootstrap 实例
    // 配置Mapper
    // 1.通过+（重写自增）的方式
    +AccountMapper::class
    // 2.通过原始的方式
    // it.addMapper(AccountMapper::class.java)
    
    // 配置单dataSource
    // 1.通过+（重写自增）的方式
    +dataSource
    // 2.通过原始的方式
    // it.setDataSource(dataSource)
    
    // 配置多dataSource
    // 1.通过of（中缀）的方式
    // FlexConsts.NAME of dataSource
    // "dataSource1" of dataSource
    // "dataSource2" of dataSource
    // 2.通过dsl（中缀）的方式
    dataSources {
        // dataSource(FlexConsts.NAME,dataSource)
        // dataSource("dataSource1",dataSource)
        // dataSource("dataSource2",dataSource)
    }
    // 3.通过原始的方式
    // it.addDataSource(FlexConsts.NAME,dataSource)
    // 配置日志打印在控制台
    logImpl = StdOutImpl::class
}
```