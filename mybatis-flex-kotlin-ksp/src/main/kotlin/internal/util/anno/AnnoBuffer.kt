package com.mybatisflex.kotlin.ksp.internal.util.anno

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.mybatisflex.annotation.Column
import com.mybatisflex.annotation.Table

/*
    此文件用于缓存 KSP 创建的代理注解对象，避免重复创建。
 */

private val columnBuffer = HashMap<KSPropertyDeclaration, Column>()

private val tableBuffer = HashMap<KSClassDeclaration, Table>()

/**
 * 从 [KSPropertyDeclaration] 获取可能存在的 [Column] 注解对象。
 *
 * @receiver 要获取 [Column] 注解的 [KSPropertyDeclaration] 。
 * @return [Column] 注解的代理对象，如果没有找到则返回 null。
 */
@OptIn(KspExperimental::class)
val KSPropertyDeclaration.column: Column?
    get() = columnBuffer[this] ?: getAnnotationsByType(Column::class).firstOrNull()?.also {
        columnBuffer[this] = it
    }

/**
 * 从 [KSClassDeclaration] 获取 [Table] 注解对象。
 *
 * @receiver 要获取 [Table] 注解的 [KSClassDeclaration] 。
 * @return [Table] 注解的代理对象。
 */
@OptIn(KspExperimental::class)
val KSClassDeclaration.table: Table
    get() = tableBuffer.getOrPut(this) {
        getAnnotationsByType(Table::class).first()
    }
