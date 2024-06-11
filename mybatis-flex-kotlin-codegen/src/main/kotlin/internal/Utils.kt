package com.mybatisflex.kotlin.codegen.internal

import com.mybatisflex.annotation.Column
import com.mybatisflex.annotation.InsertListener
import com.mybatisflex.annotation.SetListener
import com.mybatisflex.annotation.Table
import com.mybatisflex.annotation.UpdateListener
import com.mybatisflex.kotlin.codegen.config.GenerateDispatcher
import com.mybatisflex.kotlin.codegen.config.TableConfiguration
import com.mybatisflex.kotlin.codegen.metadata.TableMetadata
import com.squareup.kotlinpoet.*
import org.apache.ibatis.type.JdbcType
import org.apache.ibatis.type.TypeHandler
import org.apache.ibatis.type.UnknownTypeHandler
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.typeOf

fun String.asCamelCase(): String = buildString {
    var prevChar = '!'
    for (it in this@asCamelCase) {
        if (it == '_') {
            prevChar = '_'
            continue
        }
        if (prevChar == '_') {
            append(it.uppercaseChar())
        } else {
            append(it.lowercaseChar())
        }
        prevChar = it
    }
}

fun String.asClassName(): String = asCamelCase().replaceFirstChar(Char::uppercaseChar)

val TableMetadata.configuration: TableConfiguration
    get() = with(GenerateDispatcher) {
        specificConfiguration[tableName] ?: globalTableConfiguration
    }

fun Table(
    value: String,
    schema: String = "",
    camelToUnderline: Boolean = true,
    dataSource: String = "",
    comment: String = "",
    onInsert: Array<KClass<out InsertListener>> = emptyArray(),
    onUpdate: Array<KClass<out UpdateListener>> = emptyArray(),
    onSet: Array<KClass<out SetListener>> = emptyArray(),
    mapperGenerateEnable: Boolean = true,
): Table = Table(
    value = value,
    schema = schema,
    camelToUnderline = camelToUnderline,
    dataSource = dataSource,
    comment = comment,
    onInsert = onInsert,
    onUpdate = onUpdate,
    onSet = onSet,
    mapperGenerateEnable = mapperGenerateEnable
)

fun Column(
    value: String = "",
    ignore: Boolean = false,
    onInsertValue: String = "",
    onUpdateValue: String = "",
    isLarge: Boolean = false,
    isLogicDelete: Boolean = false,
    version: Boolean = false,
    tenantId: Boolean = false,
    jdbcType: JdbcType = JdbcType.UNDEFINED,
    typeHandler: KClass<out TypeHandler<*>> = UnknownTypeHandler::class,
    comment: String = "",
): Column = Column(
    value = value,
    ignore = ignore,
    onInsertValue = onInsertValue,
    onUpdateValue = onUpdateValue,
    isLarge = isLarge,
    isLogicDelete = isLogicDelete,
    version = version,
    tenantId = tenantId,
    jdbcType = jdbcType,
    typeHandler = typeHandler,
    comment = comment
)


fun replaceTypeName(type: TypeName, oldType: TypeName, newType: TypeName): TypeName {
    return when (type) {
        is ClassName -> {  // 说明就是 Nothing 类型，直接返回
            if (type == oldType) {
                return newType
            } else {
                type
            }
        }

        is ParameterizedTypeName -> {
            type.copy(typeArguments = type.typeArguments.map { replaceTypeName(it, oldType, newType) })
        }

        is TypeVariableName -> {
            type.copy(bounds = type.bounds.map { replaceTypeName(it, oldType, newType) })
        }

        is WildcardTypeName -> {
            when {
                type.inTypes.size == 1 -> WildcardTypeName.consumerOf(type.inTypes[0].replaceTypeName(oldType, newType))
                type.outTypes == STAR.outTypes -> type
                else -> WildcardTypeName.producerOf(type.outTypes[0].replaceTypeName(oldType, newType))
            }
        }

        is LambdaTypeName -> {
            LambdaTypeName.get(
                type.receiver?.let { replaceTypeName(it, oldType, newType) },
                type.parameters.map { it.toBuilder(type = replaceTypeName(it.type, oldType, newType)).build() },
                returnType = replaceTypeName(type, oldType, newType)
            )
        }

        is Dynamic -> {
            throw IllegalArgumentException("Dynamic type is not supported.")
        }
    }
}

inline fun <reified T1, reified T2> KType.replaceType(): KType = replaceType(typeOf<T1>(), typeOf<T2>())

fun KType.replaceType(oldType: KType, newType: KType): KType {
    val classifier = classifier ?: return this
    if (classifier !is KClass<*>) return this
    if (classifier == oldType.classifier) return newType
    if (arguments.isEmpty()) return this
    return classifier.createType(arguments = arguments.map { it.copy(type = it.type?.replaceType(oldType, newType)) })
}

fun KType.replaceNothing(newType: KType): KType = replaceType(NothingType, newType)

inline fun <reified T> KType.replaceNothing(): KType = replaceNothing(typeOf<T>())

@JvmName("replaceNothingByReceiver")
fun TypeName.replaceTypeName(oldType: TypeName, newType: TypeName): TypeName = replaceTypeName(this, oldType, newType)

val NothingType: KType = typeOf<ArrayList<Nothing>>().arguments[0].type!!

val VOID = ClassName("java.lang", "Void")

val UNDEFINED = ClassName("com.mybatisflex.kotlin.codegen.internal", "Undefined")

/**
 * 先把 Nothing 替换为 Undefined 是因为此时 KType 中的 Nothing 是真的 Nothing，先用 Undefined 占位。
 * 后面把 Void 换成 Nothing 是平台类型映射，由于 kotlinpoet 通过 KClass<Nothing> 将其解析为 java.lang.Void，所以此处需要映射回 Nothing
 * 最后再把 Undefined 替换为指定的自身类型。
 */
fun KType.replaceWithItselfToTypeName(typeName: String): TypeName =
    replaceNothing<Undefined>()
        .asTypeName()
        .replaceTypeName(VOID, NOTHING)
        .replaceTypeName(UNDEFINED, TypeVariableName(typeName))
