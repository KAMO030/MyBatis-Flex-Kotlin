package com.mybatisflex.kotlin.ksp.internal.config.flex

import com.mybatisflex.kotlin.ksp.illegalValueWarning
import com.mybatisflex.kotlin.ksp.internal.config.flex.Enable.value
import com.mybatisflex.kotlin.ksp.logger
import java.io.File
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException

/**
 * 是否开启 ksp 生成。若 [value] 为 false，则不生成。
 * @author CloudPlayer
 */
internal object Enable : MybatisFlexConfiguration<Boolean> {
    override val key: String = "processor.enable"

    override var value: Boolean = true

    override fun initValue(value: String) {
        Enable.value = value.toBoolean()
    }
}

/**
 * 是否停止向上级合并配置。即在子项目 mybatis-flex.config 中的键值对，会和父项目 mybatis-flex.config 中的键值对进行合并。
 *
 * 假设我们在子项目的 mybatis-flex.config 中进行了如下定义：
 * ```mybatis-flex.config
 * processor.tableDef.classSuffix=KtTableDef
 * ```
 *
 * 而在父项目的 mybatis-flex.config 中进行了如下定义：
 * ```mybatis-flex.config
 * processor.tableDef.classSuffix=TableDef
 * ```
 *
 * 那么经由合并后，ksp为子项目生成的 TableDef 类的后缀将是 `KtTableDef` 而不是 `TableDef` 。
 *
 * 如果父项目中已定义的键值对而子类没有定义，则父项目中此键值对也对子项目有效，反之则子项目会进行合并。
 * 在多数键值对下，这个合并操作指的是重写，即使用子项目的值。
 *
 * @author CloudPlayer
 */
internal object StopBubbling : MybatisFlexConfiguration<Boolean> {
    override val key: String = "processor.stopBubbling"

    override var value: Boolean = false

    override fun initValue(value: String) {
        StopBubbling.value = value.toBoolean()
    }
}

// 暂不支持
@Suppress("unused")
internal object GenPath : MybatisFlexConfiguration<String> {
    override val key: String = "processor.genPath"

    override var value: String = "target/generated-sources/annotations"

    override fun initValue(value: String) {
        GenPath.value = value
        val file = File(value)
        if (!file.exists()) {
            logger.warn("Generated source folder does not exist: `${file.absolutePath}`")
        }
    }
}

/**
 * 生成的文件的字符集。
 * @author CloudPlayer
 */
internal object FlexCharset : MybatisFlexConfiguration<Charset> {
    override val key: String = "processor.charset"

    private var _value: Charset = Charsets.UTF_8

    override val value: Charset
        get() = _value

    override fun initValue(value: String) {
        try {
            val charset = Charset.forName(value)
            _value = charset
        } catch (_: UnsupportedCharsetException) {  // 若字符集不合法则警告
            illegalValueWarning(key, value)
        }
    }
}

/**
 * 是否额外生成一个类，用于存放生成的所有类的静态实例。
 *
 * 即官网中所说的 Tables 类。如有需要，ksp会为此生成一个 kotlin 对象。
 *
 * 如果开启生成，必须配置 [AllInTablesPackage] 来指定包名。
 * @author CloudPlayer
 */
internal object AllInTablesEnable : MybatisFlexConfiguration<Boolean> {
    override val key: String = "processor.allInTables.enable"

    override var value: Boolean = false

    override fun initValue(value: String) {
        AllInTablesEnable.value = value.toBoolean()
    }
}

/**
 * Tables 类在哪个包中生成。如果没有指定包名，则不予生成。
 * @author CloudPlayer
 */
internal object AllInTablesPackage : MybatisFlexConfiguration<String?> {
    override val key: String = "processor.allInTables.package"

    override var value: String? = null

    override fun initValue(value: String) {
        AllInTablesPackage.value = value
    }
}

/**
 * Tables 类的类名。
 * @author CloudPlayer
 */
internal object AllInTablesClassName : MybatisFlexConfiguration<String> {
    override val key: String = "processor.allInTables.className"

    override var value: String = "Tables"

    override fun initValue(value: String) {
        AllInTablesClassName.value = value
    }
}

/**
 * 是否开启 Mapper 接口的生成。
 *
 * @author CloudPlayer
 */
internal object MapperGenerateEnable : MybatisFlexConfiguration<Boolean> {
    override val key: String = "processor.mapper.generateEnable"

    override var value: Boolean = false

    override fun initValue(value: String) {
        MapperGenerateEnable.value = value.toBoolean()
    }
}

/**
 * 是否打上 [@Mapper] 注解。
 *
 * @author CloudPlayer
 */
internal object MapperAnnotation : MybatisFlexConfiguration<Boolean> {
    override val key: String = "processor.mapper.annotation"

    override var value: Boolean = false

    override fun initValue(value: String) {
        MapperAnnotation.value = value.toBoolean()
    }
}

/**
 * Mapper 接口的父类。
 *
 * 现阶段合法的 Mapper 父类只有两种：
 *
 * 1，没有定义泛型的 Mapper。
 *
 * 2，定义了有且仅有一个泛型的 Mapper，没有声明协变或逆变，且该泛型传递至 BaseMapper 中。
 *
 * 换言之，以下 Mapper 在现阶段是不合法的：
 * ```kotlin
 * interface MyMapper1<T, V> : BaseMapper<T> // 定义了两个泛型。
 * interface MyMapper3<out T> : BaseMapper<T> // 定义的泛型声明为协变。
 * interface MyMapper5<T> : BaseMapper<Nothing> // 声明的泛型没有传递至 BaseMapper 中。
 * ```
 * @author CloudPlayer
 */
internal object MapperBaseClass : MybatisFlexConfiguration<String> {
    const val BASE_MAPPER: String = "com.mybatisflex.core.BaseMapper"

    override val key: String = "processor.mapper.baseClass"

    override var value: String = BASE_MAPPER

    val packageName: String
        get() = value.substringBeforeLast(".")

    override fun initValue(value: String) {
        MapperBaseClass.value = value
    }

    val isOriginalBaseMapper: Boolean
        get() = value == BASE_MAPPER
}

/**
 * 指定 Mapper 生成在哪个包下。
 * @author CloudPlayer
 */
internal object MapperPackage : MybatisFlexConfiguration<String?> {
    override val key: String = "processor.mapper.package"

    override var value: String? = null

    override fun initValue(value: String) {
        MapperPackage.value = value
    }
}

/**
 * 指定 TableDef 类中属性的命名风格。
 *
 * 目前仅支持以下四种命名风格：
 *
 * 1，upperCase：大写 + 下划线，例如：USER_NAME
 *
 * 2，lowerCase：小写 + 下划线，例如：user_name
 *
 * 3，upperCamelCase：首字母大写的驼峰命名，例如：UserName
 *
 * 4，lowerCamelCase：首字母小写的驼峰命名，例如：userName
 *
 * 5，original：原始字符串，不做任何改动。
 *
 * @author CloudPlayer
 */
internal object TableDefPropertiesNameStyle : MybatisFlexConfiguration<PropertiesNameStyle> {
    override val key: String  = "processor.tableDef.propertiesNameStyle"

    override var value: PropertiesNameStyle = PropertiesNameStyle.upperCase

    override fun initValue(value: String) {
        try {
            TableDefPropertiesNameStyle.value = PropertiesNameStyle.valueOf(value)
        } catch (_: IllegalArgumentException) {
            illegalValueWarning(key, value)
        }
    }
}

/**
 * 指定 TableDef 类中实例属性的后缀。
 *
 * 假设我们为此项设置的值为 `Kotlin`，在 [TableDefPropertiesNameStyle] 中指定其值为 [PropertiesNameStyle.upperCase] 那么ksp在生成实例属性时将是：
 * ```kotlin
 * object MyTableDef : TableDef(..., ...) {
 *      ... // ksp 生成的其他内容
 *      val MY_TABLE_DEF_KOTLIN = MyTableDef  // 实例属性名将连同 `Kotlin` 一同解析
 * }
 * ```
 * @author CloudPlayer
 */
internal object TableDefInstanceSuffix : MybatisFlexConfiguration<String> {
    override val key: String = "processor.tableDef.instanceSuffix"

    override var value = ""

    override fun initValue(value: String) {
        TableDefInstanceSuffix.value = value
    }
}

/**
 * 指定 TableDef 类类名的后缀。
 *
 * 假设我们为此项设定的值为 `KotlinTableDef` ，我们自定义的实体类为 `MyEntity` ， 那么 ksp 会生成如下代码：
 *
 * ```kotlin
 * object MyEntityKotlinTableDef : TableDef(..., ...)
 * ```
 *
 * @author CloudPlayer
 */
internal object TableDefClassSuffix : MybatisFlexConfiguration<String> {
    override val key: String = "processor.tableDef.classSuffix"

    override var value: String = "TableDef"

    override fun initValue(value: String) {
        TableDefClassSuffix.value = value
    }
}

/**
 * 需要忽略的实体类后缀。
 *
 * 子项目与父项目均定义了此项时，二者定义需要忽略的实体类后缀将取二者定义的并集。即都会忽略。
 *
 * @author CloudPlayer
 */
internal object TableDefIgnoreEntitySuffixes : MybatisFlexConfiguration<Set<String>> {
    override val key: String = "processor.tableDef.ignoreEntitySuffixes"

    private val _value = LinkedHashSet<String>()

    override var value: Set<String>
        get() = _value
        set(value) {
            _value += value
        }

    override fun initValue(value: String) {
        _value += value.split(",").map {
            it.trim()
        }
    }
}