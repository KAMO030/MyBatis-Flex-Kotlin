package com.mybatisflex.kotlin.flexStream

import com.mybatisflex.core.query.*
import kotlin.reflect.KProperty0

@Suppress("unused", "FunctionName")
object QueryFunctions {
    /**
     * 详情请见[QueryMethods.length]
     */
    @JvmStatic
    fun length(column0: String): QueryColumn = QueryMethods.length(column0)

    /**
     * 详情请见[QueryMethods.length]
     */
    @JvmStatic
    fun length(column0: QueryColumn): QueryColumn = QueryMethods.length(column0)

    /**
     * 详情请见[QueryMethods.version]
     */
    @JvmStatic
    fun version(): QueryColumn = QueryMethods.version()

    /**
     * 详情请见[QueryMethods.abs]
     */
    @JvmStatic
    fun abs(column0: String): QueryColumn = QueryMethods.abs(column0)

    /**
     * 详情请见[QueryMethods.abs]
     */
    @JvmStatic
    fun abs(column0: QueryColumn): QueryColumn = QueryMethods.abs(column0)

    /**
     * 详情请见[QueryMethods.sin]
     */
    @JvmStatic
    fun sin(column0: QueryColumn): QueryColumn = QueryMethods.sin(column0)

    /**
     * 详情请见[QueryMethods.sin]
     */
    @JvmStatic
    fun sin(column0: String): QueryColumn = QueryMethods.sin(column0)

    /**
     * 详情请见[QueryMethods.cos]
     */
    @JvmStatic
    fun cos(column0: String): QueryColumn = QueryMethods.cos(column0)

    /**
     * 详情请见[QueryMethods.cos]
     */
    @JvmStatic
    fun cos(column0: QueryColumn): QueryColumn = QueryMethods.cos(column0)

    /**
     * 详情请见[QueryMethods.tan]
     */
    @JvmStatic
    fun tan(column0: String): QueryColumn = QueryMethods.tan(column0)

    /**
     * 详情请见[QueryMethods.tan]
     */
    @JvmStatic
    fun tan(column0: QueryColumn): QueryColumn = QueryMethods.tan(column0)

    /**
     * 详情请见[QueryMethods.sqrt]
     */
    @JvmStatic
    fun sqrt(column0: QueryColumn): QueryColumn = QueryMethods.sqrt(column0)

    /**
     * 详情请见[QueryMethods.sqrt]
     */
    @JvmStatic
    fun sqrt(column0: String): QueryColumn = QueryMethods.sqrt(column0)

    /**
     * 详情请见[QueryMethods.log]
     */
    @JvmStatic
    fun log(column0: String): QueryColumn = QueryMethods.log(column0)

    /**
     * 详情请见[QueryMethods.log]
     */
    @JvmStatic
    fun log(column0: QueryColumn): QueryColumn = QueryMethods.log(column0)

    /**
     * 详情请见[QueryMethods.log10]
     */
    @JvmStatic
    fun log10(column0: String): QueryColumn = QueryMethods.log10(column0)

    /**
     * 详情请见[QueryMethods.log10]
     */
    @JvmStatic
    fun log10(column0: QueryColumn): QueryColumn = QueryMethods.log10(column0)

    /**
     * 详情请见[QueryMethods.pow]
     */
    @JvmStatic
    fun pow(column0: QueryColumn, column1: QueryColumn): QueryColumn =
        QueryMethods.pow(column0, column1)

    /**
     * 详情请见[QueryMethods.pow]
     */
    @JvmStatic
    fun pow(column0: String, column1: String): QueryColumn = QueryMethods.pow(column0, column1)

    /**
     * 详情请见[QueryMethods.pow]
     */
    @JvmStatic
    fun pow(column0: String, column1: Int): QueryColumn = QueryMethods.pow(column0, column1)

    /**
     * 详情请见[QueryMethods.pow]
     */
    @JvmStatic
    fun pow(column0: QueryColumn, column1: Int): QueryColumn = QueryMethods.pow(column0,
        column1)

    /**
     * 详情请见[QueryMethods.exp]
     */
    @JvmStatic
    fun exp(column0: QueryColumn): QueryColumn = QueryMethods.exp(column0)

    /**
     * 详情请见[QueryMethods.exp]
     */
    @JvmStatic
    fun exp(column0: String): QueryColumn = QueryMethods.exp(column0)

    /**
     * 详情请见[QueryMethods.min]
     */
    @JvmStatic
    fun min(column0: String): FunctionQueryColumn = QueryMethods.min(column0)

    /**
     * 详情请见[QueryMethods.min]
     */
    @JvmStatic
    fun min(column0: QueryColumn): FunctionQueryColumn = QueryMethods.min(column0)

    /**
     * 详情请见[QueryMethods.max]
     */
    @JvmStatic
    fun max(column0: QueryColumn): FunctionQueryColumn = QueryMethods.max(column0)

    /**
     * 详情请见[QueryMethods.max]
     */
    @JvmStatic
    fun max(column0: String): FunctionQueryColumn = QueryMethods.max(column0)

    /**
     * 详情请见[QueryMethods.floor]
     */
    @JvmStatic
    fun floor(column0: QueryColumn): QueryColumn = QueryMethods.floor(column0)

    /**
     * 详情请见[QueryMethods.floor]
     */
    @JvmStatic
    fun floor(column0: String): QueryColumn = QueryMethods.floor(column0)

    /**
     * 详情请见[QueryMethods.ceil]
     */
    @JvmStatic
    fun ceil(column0: String): QueryColumn = QueryMethods.ceil(column0)

    /**
     * 详情请见[QueryMethods.ceil]
     */
    @JvmStatic
    fun ceil(column0: QueryColumn): QueryColumn = QueryMethods.ceil(column0)

    /**
     * 详情请见[QueryMethods.insert]
     */
    @JvmStatic
    fun insert(
        column0: QueryColumn,
        column1: QueryColumn,
        column2: QueryColumn,
        column3: QueryColumn,
    ): QueryColumn = QueryMethods.insert(column0, column1, column2, column3)

    /**
     * 详情请见[QueryMethods.insert]
     */
    @JvmStatic
    fun insert(
        column0: String,
        column1: String,
        column2: String,
        column3: String,
    ): QueryColumn = QueryMethods.insert(column0, column1, column2, column3)

    /**
     * 详情请见[QueryMethods.convert]
     */
    @JvmStatic
    fun convert(vararg column0: String): StringFunctionQueryColumn =
        QueryMethods.convert(*column0)

    /**
     * 详情请见[QueryMethods.decode]
     */
    @JvmStatic
    fun decode(column0: String, column1: String): QueryColumn = QueryMethods.decode(column0,
        column1)

    /**
     * 详情请见[QueryMethods.decode]
     */
    @JvmStatic
    fun decode(column0: QueryColumn, column1: QueryColumn): QueryColumn =
        QueryMethods.decode(column0, column1)

    /**
     * 详情请见[QueryMethods.encode]
     */
    @JvmStatic
    fun encode(column0: String, column1: String): QueryColumn = QueryMethods.encode(column0,
        column1)

    /**
     * 详情请见[QueryMethods.encode]
     */
    @JvmStatic
    fun encode(column0: QueryColumn, column1: QueryColumn): QueryColumn =
        QueryMethods.encode(column0, column1)

    /**
     * 详情请见[QueryMethods.substring]
     */
    @JvmStatic
    fun substring(
        column0: String,
        column1: String,
        column2: String,
    ): QueryColumn = QueryMethods.substring(column0, column1, column2)

    /**
     * 详情请见[QueryMethods.substring]
     */
    @JvmStatic
    fun substring(
        column0: QueryColumn,
        column1: QueryColumn,
        column2: QueryColumn,
    ): QueryColumn = QueryMethods.substring(column0, column1, column2)

    /**
     * 详情请见[QueryMethods.replace]
     */
    @JvmStatic
    fun replace(
        column0: String,
        column1: String,
        column2: String,
    ): QueryColumn = QueryMethods.replace(column0, column1, column2)

    /**
     * 详情请见[QueryMethods.replace]
     */
    @JvmStatic
    fun replace(
        column0: QueryColumn,
        column1: QueryColumn,
        column2: QueryColumn,
    ): QueryColumn = QueryMethods.replace(column0, column1, column2)

    /**
     * 详情请见[QueryMethods.trim]
     */
    @JvmStatic
    fun trim(column0: QueryColumn): QueryColumn = QueryMethods.trim(column0)

    /**
     * 详情请见[QueryMethods.repeat]
     */
    @JvmStatic
    fun repeat(column0: String, column1: Int): QueryColumn = QueryMethods.repeat(column0,
        column1)

    /**
     * 详情请见[QueryMethods.repeat]
     */
    @JvmStatic
    fun repeat(column0: QueryColumn, column1: Int): QueryColumn = QueryMethods.repeat(column0,
        column1)

    /**
     * 详情请见[QueryMethods.repeat]
     */
    @JvmStatic
    fun repeat(column0: QueryColumn, column1: QueryColumn): QueryColumn =
        QueryMethods.repeat(column0, column1)

    /**
     * 详情请见[QueryMethods.repeat]
     */
    @JvmStatic
    fun repeat(column0: String, column1: String): QueryColumn = QueryMethods.repeat(column0,
        column1)

    /**
     * 详情请见[QueryMethods.format]
     */
    @JvmStatic
    fun format(column0: String, column1: String): QueryColumn = QueryMethods.format(column0,
        column1)

    /**
     * 详情请见[QueryMethods.format]
     */
    @JvmStatic
    fun format(column0: QueryColumn, column1: QueryColumn): QueryColumn =
        QueryMethods.format(column0, column1)

    /**
     * 详情请见[QueryMethods.count]
     */
    @JvmStatic
    fun count(): FunctionQueryColumn = QueryMethods.count()

    /**
     * 详情请见[QueryMethods.count]
     */
    @JvmStatic
    fun count(column0: QueryColumn): FunctionQueryColumn = QueryMethods.count(column0)

    /**
     * 详情请见[QueryMethods.count]
     */
    @JvmStatic
    fun count(column0: String): FunctionQueryColumn = QueryMethods.count(column0)

    /**
     * 详情请见[QueryMethods.ascii]
     */
    @JvmStatic
    fun ascii(column0: QueryColumn): QueryColumn = QueryMethods.ascii(column0)

    /**
     * 详情请见[QueryMethods.ascii]
     */
    @JvmStatic
    fun ascii(column0: String): QueryColumn = QueryMethods.ascii(column0)

    /**
     * 详情请见[QueryMethods.charset]
     */
    @JvmStatic
    fun charset(column0: String): QueryColumn = QueryMethods.charset(column0)

    /**
     * 详情请见[QueryMethods.charset]
     */
    @JvmStatic
    fun charset(column0: QueryColumn): QueryColumn = QueryMethods.charset(column0)

    /**
     * 详情请见[QueryMethods.concat]
     */
    @JvmStatic
    fun concat(
        column0: String,
        column1: String,
        vararg column2: String,
    ): QueryColumn = QueryMethods.concat(column0, column1, *column2)

    /**
     * 详情请见[QueryMethods.concat]
     */
    @JvmStatic
    fun concat(
        column0: QueryColumn,
        column1: QueryColumn,
        vararg column2: QueryColumn,
    ): QueryColumn = QueryMethods.concat(column0, column1, *column2)

    /**
     * 详情请见[QueryMethods.field]
     */
    @JvmStatic
    fun `field`(
        column0: String,
        column1: String,
        vararg column2: String,
    ): QueryColumn = QueryMethods.field(column0, column1, *column2)

    /**
     * 详情请见[QueryMethods.field]
     */
    @JvmStatic
    fun `field`(
        column0: QueryColumn,
        column1: QueryColumn,
        vararg column2: QueryColumn,
    ): QueryColumn = QueryMethods.field(column0, column1, *column2)

    /**
     * 详情请见[QueryMethods.second]
     */
    @JvmStatic
    fun second(column0: QueryColumn): QueryColumn = QueryMethods.second(column0)

    /**
     * 详情请见[QueryMethods.second]
     */
    @JvmStatic
    fun second(column0: String): QueryColumn = QueryMethods.second(column0)

    /**
     * 详情请见[QueryMethods.addTime]
     */
    @JvmStatic
    fun addTime(column0: QueryColumn, column1: QueryColumn): QueryColumn =
        QueryMethods.addTime(column0, column1)

    /**
     * 详情请见[QueryMethods.addTime]
     */
    @JvmStatic
    fun addTime(column0: String, column1: String): QueryColumn = QueryMethods.addTime(column0,
        column1)

    /**
     * 详情请见[QueryMethods.hex]
     */
    @JvmStatic
    fun hex(column0: QueryColumn): QueryColumn = QueryMethods.hex(column0)

    /**
     * 详情请见[QueryMethods.hex]
     */
    @JvmStatic
    fun hex(column0: String): QueryColumn = QueryMethods.hex(column0)

    /**
     * 详情请见[QueryMethods.mod]
     */
    @JvmStatic
    fun mod(column0: String, column1: String): QueryColumn = QueryMethods.mod(column0, column1)

    /**
     * 详情请见[QueryMethods.mod]
     */
    @JvmStatic
    fun mod(column0: QueryColumn, column1: QueryColumn): QueryColumn =
        QueryMethods.mod(column0, column1)

    /**
     * 详情请见[QueryMethods.mod]
     */
    @JvmStatic
    fun mod(column0: QueryColumn, column1: Int): QueryColumn = QueryMethods.mod(column0,
        column1)

    /**
     * 详情请见[QueryMethods.mod]
     */
    @JvmStatic
    fun mod(column0: String, column1: Int): QueryColumn = QueryMethods.mod(column0, column1)

    /**
     * 详情请见[QueryMethods.reverse]
     */
    @JvmStatic
    fun reverse(column0: String): QueryColumn = QueryMethods.reverse(column0)

    /**
     * 详情请见[QueryMethods.reverse]
     */
    @JvmStatic
    fun reverse(column0: QueryColumn): QueryColumn = QueryMethods.reverse(column0)

    /**
     * 详情请见[QueryMethods.sum]
     */
    @JvmStatic
    fun sum(column0: String): FunctionQueryColumn = QueryMethods.sum(column0)

    /**
     * 详情请见[QueryMethods.sum]
     */
    @JvmStatic
    fun sum(column0: QueryColumn): FunctionQueryColumn = QueryMethods.sum(column0)

    /**
     * 详情请见[QueryMethods.upper]
     */
    @JvmStatic
    fun upper(column0: QueryColumn): QueryColumn = QueryMethods.upper(column0)

    /**
     * 详情请见[QueryMethods.upper]
     */
    @JvmStatic
    fun upper(column0: String): QueryColumn = QueryMethods.upper(column0)

    /**
     * 详情请见[QueryMethods.lower]
     */
    @JvmStatic
    fun lower(column0: String): QueryColumn = QueryMethods.lower(column0)

    /**
     * 详情请见[QueryMethods.lower]
     */
    @JvmStatic
    fun lower(column0: QueryColumn): QueryColumn = QueryMethods.lower(column0)

    /**
     * 详情请见[QueryMethods.exists]
     */
    @JvmStatic
    fun exists(column0: QueryWrapper): QueryCondition = QueryMethods.exists(column0)

    /**
     * 详情请见[QueryMethods.bin]
     */
    @JvmStatic
    fun bin(column0: QueryColumn): QueryColumn = QueryMethods.bin(column0)

    /**
     * 详情请见[QueryMethods.bin]
     */
    @JvmStatic
    fun bin(column0: String): QueryColumn = QueryMethods.bin(column0)

    /**
     * 详情请见[QueryMethods.distinct]
     */
    @JvmStatic
    fun distinct(vararg column0: QueryColumn): DistinctQueryColumn =
        QueryMethods.distinct(*column0)

    /**
     * 详情请见[QueryMethods.left]
     */
    @JvmStatic
    fun left(column0: QueryColumn, column1: QueryColumn): QueryColumn =
        QueryMethods.left(column0, column1)

    /**
     * 详情请见[QueryMethods.left]
     */
    @JvmStatic
    fun left(column0: String, column1: Int): QueryColumn = QueryMethods.left(column0, column1)

    /**
     * 详情请见[QueryMethods.left]
     */
    @JvmStatic
    fun left(column0: QueryColumn, column1: Int): QueryColumn = QueryMethods.left(column0,
        column1)

    /**
     * 详情请见[QueryMethods.left]
     */
    @JvmStatic
    fun left(column0: String, column1: String): QueryColumn = QueryMethods.left(column0,
        column1)

    /**
     * 详情请见[QueryMethods.right]
     */
    @JvmStatic
    fun right(column0: String, column1: String): QueryColumn = QueryMethods.right(column0,
        column1)

    /**
     * 详情请见[QueryMethods.right]
     */
    @JvmStatic
    fun right(column0: QueryColumn, column1: QueryColumn): QueryColumn =
        QueryMethods.right(column0, column1)

    /**
     * 详情请见[QueryMethods.right]
     */
    @JvmStatic
    fun right(column0: QueryColumn, column1: Int): QueryColumn = QueryMethods.right(column0,
        column1)

    /**
     * 详情请见[QueryMethods.right]
     */
    @JvmStatic
    fun right(column0: String, column1: Int): QueryColumn = QueryMethods.right(column0,
        column1)

    /**
     * 详情请见[QueryMethods.asin]
     */
    @JvmStatic
    fun asin(column0: String): QueryColumn = QueryMethods.asin(column0)

    /**
     * 详情请见[QueryMethods.asin]
     */
    @JvmStatic
    fun asin(column0: QueryColumn): QueryColumn = QueryMethods.asin(column0)

    /**
     * 详情请见[QueryMethods.acos]
     */
    @JvmStatic
    fun acos(column0: String): QueryColumn = QueryMethods.acos(column0)

    /**
     * 详情请见[QueryMethods.acos]
     */
    @JvmStatic
    fun acos(column0: QueryColumn): QueryColumn = QueryMethods.acos(column0)

    /**
     * 详情请见[QueryMethods.atan]
     */
    @JvmStatic
    fun atan(column0: QueryColumn): QueryColumn = QueryMethods.atan(column0)

    /**
     * 详情请见[QueryMethods.atan]
     */
    @JvmStatic
    fun atan(column0: String): QueryColumn = QueryMethods.atan(column0)

    /**
     * 详情请见[QueryMethods.round]
     */
    @JvmStatic
    fun round(column0: String, column1: String): QueryColumn = QueryMethods.round(column0,
        column1)

    /**
     * 详情请见[QueryMethods.round]
     */
    @JvmStatic
    fun round(column0: QueryColumn, column1: Int): QueryColumn = QueryMethods.round(column0,
        column1)

    /**
     * 详情请见[QueryMethods.round]
     */
    @JvmStatic
    fun round(column0: String): QueryColumn = QueryMethods.round(column0)

    /**
     * 详情请见[QueryMethods.round]
     */
    @JvmStatic
    fun round(column0: String, column1: Int): QueryColumn = QueryMethods.round(column0,
        column1)

    /**
     * 详情请见[QueryMethods.round]
     */
    @JvmStatic
    fun round(column0: QueryColumn, column1: QueryColumn): QueryColumn =
        QueryMethods.round(column0, column1)

    /**
     * 详情请见[QueryMethods.round]
     */
    @JvmStatic
    fun round(column0: QueryColumn): QueryColumn = QueryMethods.round(column0)

    /**
     * 详情请见[QueryMethods.sign]
     */
    @JvmStatic
    fun sign(column0: QueryColumn): QueryColumn = QueryMethods.sign(column0)

    /**
     * 详情请见[QueryMethods.sign]
     */
    @JvmStatic
    fun sign(column0: String): QueryColumn = QueryMethods.sign(column0)

    /**
     * 详情请见[QueryMethods.raw]
     */
    @JvmStatic
    fun raw(column0: String): QueryCondition = QueryMethods.raw(column0)

    /**
     * 详情请见[QueryMethods.raw]
     */
    @JvmStatic
    fun raw(column0: String, vararg column1: Any): QueryCondition = QueryMethods.raw(column0,
        *column1)

    /**
     * 详情请见[QueryMethods.number]
     */
    @JvmStatic
    fun number(column0: Number): QueryColumn = QueryMethods.number(column0)

    /**
     * 详情请见[QueryMethods.space]
     */
    @JvmStatic
    fun space(column0: String): QueryColumn = QueryMethods.space(column0)

    /**
     * 详情请见[QueryMethods.space]
     */
    @JvmStatic
    fun space(column0: QueryColumn): QueryColumn = QueryMethods.space(column0)

    /**
     * 详情请见[QueryMethods.currentTime]
     */
    @JvmStatic
    fun currentTime(): QueryColumn = QueryMethods.currentTime()

    /**
     * 详情请见[QueryMethods.string]
     */
    @JvmStatic
    fun string(column0: String): QueryColumn = QueryMethods.string(column0)

    /**
     * 详情请见[QueryMethods.user]
     */
    @JvmStatic
    fun user(): QueryColumn = QueryMethods.user()

    /**
     * 详情请见[QueryMethods.pi]
     */
    @JvmStatic
    fun pi(): QueryColumn = QueryMethods.pi()

    /**
     * 详情请见[QueryMethods.not]
     */
    @JvmStatic
    fun not(column0: QueryCondition): QueryCondition = QueryMethods.not(column0)

    /**
     * 详情请见[QueryMethods.charLength]
     */
    @JvmStatic
    fun charLength(column0: String): QueryColumn = QueryMethods.charLength(column0)

    /**
     * 详情请见[QueryMethods.charLength]
     */
    @JvmStatic
    fun charLength(column0: QueryColumn): QueryColumn = QueryMethods.charLength(column0)

    /**
     * 详情请见[QueryMethods.notExists]
     */
    @JvmStatic
    fun notExists(column0: QueryWrapper): QueryCondition = QueryMethods.notExists(column0)

    /**
     * 详情请见[QueryMethods.year]
     */
    @JvmStatic
    fun year(column0: QueryColumn): QueryColumn = QueryMethods.year(column0)

    /**
     * 详情请见[QueryMethods.year]
     */
    @JvmStatic
    fun year(column0: String): QueryColumn = QueryMethods.year(column0)

    /**
     * 详情请见[QueryMethods.month]
     */
    @JvmStatic
    fun month(column0: QueryColumn): QueryColumn = QueryMethods.month(column0)

    /**
     * 详情请见[QueryMethods.month]
     */
    @JvmStatic
    fun month(column0: String): QueryColumn = QueryMethods.month(column0)

    /**
     * 详情请见[QueryMethods.day]
     */
    @JvmStatic
    fun day(column0: String): FunctionQueryColumn = QueryMethods.day(column0)

    /**
     * 详情请见[QueryMethods.day]
     */
    @JvmStatic
    fun day(column0: QueryColumn): FunctionQueryColumn = QueryMethods.day(column0)

    /**
     * 详情请见[QueryMethods.hour]
     */
    @JvmStatic
    fun hour(column0: QueryColumn): QueryColumn = QueryMethods.hour(column0)

    /**
     * 详情请见[QueryMethods.hour]
     */
    @JvmStatic
    fun hour(column0: String): QueryColumn = QueryMethods.hour(column0)

    /**
     * 详情请见[QueryMethods.minute]
     */
    @JvmStatic
    fun minute(column0: QueryColumn): QueryColumn = QueryMethods.minute(column0)

    /**
     * 详情请见[QueryMethods.minute]
     */
    @JvmStatic
    fun minute(column0: String): QueryColumn = QueryMethods.minute(column0)

    /**
     * 详情请见[QueryMethods.toDays]
     */
    @JvmStatic
    fun toDays(column0: QueryColumn): QueryColumn = QueryMethods.toDays(column0)

    /**
     * 详情请见[QueryMethods.toDays]
     */
    @JvmStatic
    fun toDays(column0: String): QueryColumn = QueryMethods.toDays(column0)

    /**
     * 详情请见[QueryMethods.union]
     */
    @JvmStatic
    fun union(column0: QueryWrapper): QueryWrapper = QueryMethods.union(column0)

    /**
     * 详情请见[QueryMethods.now]
     */
    @JvmStatic
    fun now(): QueryColumn = QueryMethods.now()

    /**
     * 详情请见[QueryMethods.ceiling]
     */
    @JvmStatic
    fun ceiling(column0: String): QueryColumn = QueryMethods.ceiling(column0)

    /**
     * 详情请见[QueryMethods.ceiling]
     */
    @JvmStatic
    fun ceiling(column0: QueryColumn): QueryColumn = QueryMethods.ceiling(column0)

    /**
     * 详情请见[QueryMethods.elt]
     */
    @JvmStatic
    fun elt(
        column0: String,
        column1: String,
        vararg column2: String,
    ): QueryColumn = QueryMethods.elt(column0, column1, *column2)

    /**
     * 详情请见[QueryMethods.elt]
     */
    @JvmStatic
    fun elt(
        column0: QueryColumn,
        column1: QueryColumn,
        vararg column2: QueryColumn,
    ): QueryColumn = QueryMethods.elt(column0, column1, *column2)

    /**
     * 详情请见[QueryMethods.weekday]
     */
    @JvmStatic
    fun weekday(column0: QueryColumn): QueryColumn = QueryMethods.weekday(column0)

    /**
     * 详情请见[QueryMethods.weekday]
     */
    @JvmStatic
    fun weekday(column0: String): QueryColumn = QueryMethods.weekday(column0)

    /**
     * 详情请见[QueryMethods.week]
     */
    @JvmStatic
    fun week(column0: String): QueryColumn = QueryMethods.week(column0)

    /**
     * 详情请见[QueryMethods.week]
     */
    @JvmStatic
    fun week(column0: QueryColumn): QueryColumn = QueryMethods.week(column0)

    /**
     * 详情请见[QueryMethods.dayOfMonth]
     */
    @JvmStatic
    fun dayOfMonth(column0: QueryColumn): QueryColumn = QueryMethods.dayOfMonth(column0)

    /**
     * 详情请见[QueryMethods.dayOfMonth]
     */
    @JvmStatic
    fun dayOfMonth(column0: String): QueryColumn = QueryMethods.dayOfMonth(column0)

    /**
     * 详情请见[QueryMethods.dayOfYear]
     */
    @JvmStatic
    fun dayOfYear(column0: String): QueryColumn = QueryMethods.dayOfYear(column0)

    /**
     * 详情请见[QueryMethods.dayOfYear]
     */
    @JvmStatic
    fun dayOfYear(column0: QueryColumn): QueryColumn = QueryMethods.dayOfYear(column0)

    /**
     * 详情请见[QueryMethods.dayOfWeek]
     */
    @JvmStatic
    fun dayOfWeek(column0: String): QueryColumn = QueryMethods.dayOfWeek(column0)

    /**
     * 详情请见[QueryMethods.dayOfWeek]
     */
    @JvmStatic
    fun dayOfWeek(column0: QueryColumn): QueryColumn = QueryMethods.dayOfWeek(column0)

    /**
     * 详情请见[QueryMethods.localTime]
     */
    @JvmStatic
    fun localTime(): QueryColumn = QueryMethods.localTime()

    /**
     * 详情请见[QueryMethods.dateFormat]
     */
    @JvmStatic
    fun dateFormat(column0: QueryColumn, column1: QueryColumn): QueryColumn =
        QueryMethods.dateFormat(column0, column1)

    /**
     * 详情请见[QueryMethods.dateFormat]
     */
    @JvmStatic
    fun dateFormat(column0: String, column1: String): QueryColumn =
        QueryMethods.dateFormat(column0, column1)

    /**
     * 详情请见[QueryMethods.weekOfYear]
     */
    @JvmStatic
    fun weekOfYear(column0: String): QueryColumn = QueryMethods.weekOfYear(column0)

    /**
     * 详情请见[QueryMethods.weekOfYear]
     */
    @JvmStatic
    fun weekOfYear(column0: QueryColumn): QueryColumn = QueryMethods.weekOfYear(column0)

    /**
     * 详情请见[QueryMethods.getFormat]
     */
    @JvmStatic
    fun getFormat(column0: String, column1: String): QueryColumn =
        QueryMethods.getFormat(column0, column1)

    /**
     * 详情请见[QueryMethods.getFormat]
     */
    @JvmStatic
    fun getFormat(column0: QueryColumn, column1: QueryColumn): QueryColumn =
        QueryMethods.getFormat(column0, column1)

    /**
     * 详情请见[QueryMethods.addDate]
     */
    @JvmStatic
    fun addDate(column0: QueryColumn, column1: QueryColumn): QueryColumn =
        QueryMethods.addDate(column0, column1)

    /**
     * 详情请见[QueryMethods.addDate]
     */
    @JvmStatic
    fun addDate(column0: String, column1: String): QueryColumn = QueryMethods.addDate(column0,
        column1)

    /**
     * 详情请见[QueryMethods.avg]
     */
    @JvmStatic
    fun avg(column0: String): FunctionQueryColumn = QueryMethods.avg(column0)

    /**
     * 详情请见[QueryMethods.avg]
     */
    @JvmStatic
    fun avg(column0: QueryColumn): FunctionQueryColumn = QueryMethods.avg(column0)

    /**
     * 详情请见[QueryMethods.conv]
     */
    @JvmStatic
    fun conv(
        column0: QueryColumn,
        column1: QueryColumn,
        column2: QueryColumn,
    ): QueryColumn = QueryMethods.conv(column0, column1, column2)

    /**
     * 详情请见[QueryMethods.conv]
     */
    @JvmStatic
    fun conv(
        column0: String,
        column1: String,
        column2: String,
    ): QueryColumn = QueryMethods.conv(column0, column1, column2)

    /**
     * 详情请见[QueryMethods.select]
     */
    @JvmStatic
    fun select(vararg column0: QueryColumn): QueryWrapper = QueryMethods.select(*column0)

    /**
     * 详情请见[QueryMethods.column]
     */
    @JvmStatic
    fun column(column0: String): QueryColumn = QueryMethods.column(column0)

    /**
     * 详情请见[QueryMethods.column]
     */
    @JvmStatic
    fun column(column0: String, column1: String): QueryColumn = QueryMethods.column(column0,
        column1)

    /**
     * 详情请见[QueryMethods.column]
     */
    @JvmStatic
    fun column(column0: QueryWrapper): QueryColumn = QueryMethods.column(column0)

    /**
     * 详情请见[QueryMethods.column]
     */
    @JvmStatic
    fun column(
        column0: String,
        column1: String,
        column2: String,
    ): QueryColumn = QueryMethods.column(column0, column1, column2)

    /**
     * 详情请见[QueryMethods.rand]
     */
    @JvmStatic
    fun rand(column0: String): QueryColumn = QueryMethods.rand(column0)

    /**
     * 详情请见[QueryMethods.rand]
     */
    @JvmStatic
    fun rand(): QueryColumn = QueryMethods.rand()

    /**
     * 详情请见[QueryMethods.rand]
     */
    @JvmStatic
    fun rand(column0: QueryColumn): QueryColumn = QueryMethods.rand(column0)

    /**
     * 详情请见[QueryMethods.cot]
     */
    @JvmStatic
    fun cot(column0: QueryColumn): QueryColumn = QueryMethods.cot(column0)

    /**
     * 详情请见[QueryMethods.cot]
     */
    @JvmStatic
    fun cot(column0: String): QueryColumn = QueryMethods.cot(column0)

    /**
     * 详情请见[QueryMethods.concatWs]
     */
    @JvmStatic
    fun concatWs(
        column0: String,
        column1: String,
        column2: String,
        vararg column3: String,
    ): QueryColumn = QueryMethods.concatWs(column0, column1, column2, *column3)

    /**
     * 详情请见[QueryMethods.concatWs]
     */
    @JvmStatic
    fun concatWs(
        column0: QueryColumn,
        column1: QueryColumn,
        column2: QueryColumn,
        vararg column3: QueryColumn,
    ): QueryColumn = QueryMethods.concatWs(column0, column1, column2, *column3)

    /**
     * 详情请见[QueryMethods.degrees]
     */
    @JvmStatic
    fun degrees(column0: String): QueryColumn = QueryMethods.degrees(column0)

    /**
     * 详情请见[QueryMethods.degrees]
     */
    @JvmStatic
    fun degrees(column0: QueryColumn): QueryColumn = QueryMethods.degrees(column0)

    /**
     * 详情请见[QueryMethods.lpad]
     */
    @JvmStatic
    fun lpad(
        column0: String,
        column1: String,
        column2: String,
    ): QueryColumn = QueryMethods.lpad(column0, column1, column2)

    /**
     * 详情请见[QueryMethods.lpad]
     */
    @JvmStatic
    fun lpad(
        column0: QueryColumn,
        column1: QueryColumn,
        column2: QueryColumn,
    ): QueryColumn = QueryMethods.lpad(column0, column1, column2)

    /**
     * 详情请见[QueryMethods.radians]
     */
    @JvmStatic
    fun radians(column0: String): QueryColumn = QueryMethods.radians(column0)

    /**
     * 详情请见[QueryMethods.radians]
     */
    @JvmStatic
    fun radians(column0: QueryColumn): QueryColumn = QueryMethods.radians(column0)

    /**
     * 详情请见[QueryMethods.rpad]
     */
    @JvmStatic
    fun rpad(
        column0: String,
        column1: String,
        column2: String,
    ): QueryColumn = QueryMethods.rpad(column0, column1, column2)

    /**
     * 详情请见[QueryMethods.rpad]
     */
    @JvmStatic
    fun rpad(
        column0: QueryColumn,
        column1: QueryColumn,
        column2: QueryColumn,
    ): QueryColumn = QueryMethods.rpad(column0, column1, column2)

    /**
     * 详情请见[QueryMethods.ltrim]
     */
    @JvmStatic
    fun ltrim(column0: String): QueryColumn = QueryMethods.ltrim(column0)

    /**
     * 详情请见[QueryMethods.ltrim]
     */
    @JvmStatic
    fun ltrim(column0: QueryColumn): QueryColumn = QueryMethods.ltrim(column0)

    /**
     * 详情请见[QueryMethods.timeToSec]
     */
    @JvmStatic
    fun timeToSec(column0: QueryColumn): QueryColumn = QueryMethods.timeToSec(column0)

    /**
     * 详情请见[QueryMethods.timeToSec]
     */
    @JvmStatic
    fun timeToSec(column0: String): QueryColumn = QueryMethods.timeToSec(column0)

    /**
     * 详情请见[QueryMethods.rtrim]
     */
    @JvmStatic
    fun rtrim(column0: QueryColumn): QueryColumn = QueryMethods.rtrim(column0)

    /**
     * 详情请见[QueryMethods.rtrim]
     */
    @JvmStatic
    fun rtrim(column0: String): QueryColumn = QueryMethods.rtrim(column0)

    /**
     * 详情请见[QueryMethods.utcTime]
     */
    @JvmStatic
    fun utcTime(): QueryColumn = QueryMethods.utcTime()

    /**
     * 详情请见[QueryMethods.sysDate]
     */
    @JvmStatic
    fun sysDate(): QueryColumn = QueryMethods.sysDate()

    /**
     * 详情请见[QueryMethods.fromUnixTime]
     */
    @JvmStatic
    fun fromUnixTime(column0: String): QueryColumn = QueryMethods.fromUnixTime(column0)

    /**
     * 详情请见[QueryMethods.fromUnixTime]
     */
    @JvmStatic
    fun fromUnixTime(column0: QueryColumn): QueryColumn = QueryMethods.fromUnixTime(column0)

    /**
     * 详情请见[QueryMethods.curTime]
     */
    @JvmStatic
    fun curTime(): QueryColumn = QueryMethods.curTime()

    /**
     * 详情请见[QueryMethods.curDate]
     */
    @JvmStatic
    fun curDate(): QueryColumn = QueryMethods.curDate()

    /**
     * 详情请见[QueryMethods.utcDate]
     */
    @JvmStatic
    fun utcDate(): QueryColumn = QueryMethods.utcDate()

    /**
     * 详情请见[QueryMethods.quarter]
     */
    @JvmStatic
    fun quarter(column0: QueryColumn): QueryColumn = QueryMethods.quarter(column0)

    /**
     * 详情请见[QueryMethods.quarter]
     */
    @JvmStatic
    fun quarter(column0: String): QueryColumn = QueryMethods.quarter(column0)

    /**
     * 详情请见[QueryMethods.findInSet]
     */
    @JvmStatic
    fun findInSet(column0: QueryColumn, column1: QueryColumn): QueryColumn =
        QueryMethods.findInSet(column0, column1)

    /**
     * 详情请见[QueryMethods.findInSet]
     */
    @JvmStatic
    fun findInSet(column0: String, column1: String): QueryColumn =
        QueryMethods.findInSet(column0, column1)

    /**
     * 详情请见[QueryMethods.currentTimestamp]
     */
    @JvmStatic
    fun currentTimestamp(): QueryColumn = QueryMethods.currentTimestamp()

    /**
     * 详情请见[QueryMethods.unixTimestamp]
     */
    @JvmStatic
    fun unixTimestamp(column0: QueryColumn): QueryColumn = QueryMethods.unixTimestamp(column0)

    /**
     * 详情请见[QueryMethods.unixTimestamp]
     */
    @JvmStatic
    fun unixTimestamp(column0: String): QueryColumn = QueryMethods.unixTimestamp(column0)

    /**
     * 详情请见[QueryMethods.unixTimestamp]
     */
    @JvmStatic
    fun unixTimestamp(): QueryColumn = QueryMethods.unixTimestamp()

    /**
     * 详情请见[QueryMethods.dayName]
     */
    @JvmStatic
    fun dayName(column0: QueryColumn): QueryColumn = QueryMethods.dayName(column0)

    /**
     * 详情请见[QueryMethods.dayName]
     */
    @JvmStatic
    fun dayName(column0: String): QueryColumn = QueryMethods.dayName(column0)

    /**
     * 详情请见[QueryMethods.secToTime]
     */
    @JvmStatic
    fun secToTime(column0: QueryColumn): QueryColumn = QueryMethods.secToTime(column0)

    /**
     * 详情请见[QueryMethods.secToTime]
     */
    @JvmStatic
    fun secToTime(column0: String): QueryColumn = QueryMethods.secToTime(column0)

    /**
     * 详情请见[QueryMethods.currentDate]
     */
    @JvmStatic
    fun currentDate(): QueryColumn = QueryMethods.currentDate()

    /**
     * 详情请见[QueryMethods.strcmp]
     */
    @JvmStatic
    fun strcmp(column0: String, column1: String): QueryColumn = QueryMethods.strcmp(column0,
        column1)

    /**
     * 详情请见[QueryMethods.strcmp]
     */
    @JvmStatic
    fun strcmp(column0: QueryColumn, column1: QueryColumn): QueryColumn =
        QueryMethods.strcmp(column0, column1)

    /**
     * 详情请见[QueryMethods.fromDays]
     */
    @JvmStatic
    fun fromDays(column0: String): QueryColumn = QueryMethods.fromDays(column0)

    /**
     * 详情请见[QueryMethods.fromDays]
     */
    @JvmStatic
    fun fromDays(column0: QueryColumn): QueryColumn = QueryMethods.fromDays(column0)

    /**
     * 详情请见[QueryMethods.localTimestamp]
     */
    @JvmStatic
    fun localTimestamp(): QueryColumn = QueryMethods.localTimestamp()

    /**
     * 详情请见[QueryMethods.dateDiff]
     */
    @JvmStatic
    fun dateDiff(column0: QueryColumn, column1: QueryColumn): QueryColumn =
        QueryMethods.dateDiff(column0, column1)

    /**
     * 详情请见[QueryMethods.dateDiff]
     */
    @JvmStatic
    fun dateDiff(column0: String, column1: String): QueryColumn =
        QueryMethods.dateDiff(column0, column1)

    /**
     * 详情请见[QueryMethods.monthName]
     */
    @JvmStatic
    fun monthName(column0: String): QueryColumn = QueryMethods.monthName(column0)

    /**
     * 详情请见[QueryMethods.monthName]
     */
    @JvmStatic
    fun monthName(column0: QueryColumn): QueryColumn = QueryMethods.monthName(column0)

    /**
     * 详情请见[QueryMethods.inetAton]
     */
    @JvmStatic
    fun inetAton(column0: QueryColumn): QueryColumn = QueryMethods.inetAton(column0)

    /**
     * 详情请见[QueryMethods.inetAton]
     */
    @JvmStatic
    fun inetAton(column0: String): QueryColumn = QueryMethods.inetAton(column0)

    /**
     * 详情请见[QueryMethods.connectionId]
     */
    @JvmStatic
    fun connectionId(): QueryColumn = QueryMethods.connectionId()

    /**
     * 详情请见[QueryMethods.inetNtoa]
     */
    @JvmStatic
    fun inetNtoa(column0: String): QueryColumn = QueryMethods.inetNtoa(column0)

    /**
     * 详情请见[QueryMethods.inetNtoa]
     */
    @JvmStatic
    fun inetNtoa(column0: QueryColumn): QueryColumn = QueryMethods.inetNtoa(column0)

    /**
     * 详情请见[QueryMethods.subTime]
     */
    @JvmStatic
    fun subTime(column0: QueryColumn, column1: QueryColumn): QueryColumn =
        QueryMethods.subTime(column0, column1)

    /**
     * 详情请见[QueryMethods.subTime]
     */
    @JvmStatic
    fun subTime(column0: String, column1: String): QueryColumn = QueryMethods.subTime(column0,
        column1)

    /**
     * 详情请见[QueryMethods.lastInsertId]
     */
    @JvmStatic
    fun lastInsertId(): QueryColumn = QueryMethods.lastInsertId()

    /**
     * 详情请见[QueryMethods.case_]
     */
    @JvmStatic
    fun case_(): CaseQueryColumn.Builder = QueryMethods.case_()

    /**
     * 详情请见[QueryMethods.case_]
     */
    @JvmStatic
    fun case_(column0: QueryColumn): CaseSearchQueryColumn.Builder =
        QueryMethods.case_(column0)

    /**
     * 详情请见[QueryMethods.collation]
     */
    @JvmStatic
    fun collation(column0: String): QueryColumn = QueryMethods.collation(column0)

    /**
     * 详情请见[QueryMethods.collation]
     */
    @JvmStatic
    fun collation(column0: QueryColumn): QueryColumn = QueryMethods.collation(column0)

    /**
     * 详情请见[QueryMethods.database]
     */
    @JvmStatic
    fun database(): QueryColumn = QueryMethods.database()

    /**
     * 详情请见[QueryMethods.timeFormat]
     */
    @JvmStatic
    fun timeFormat(column0: String, column1: String): QueryColumn =
        QueryMethods.timeFormat(column0, column1)

    /**
     * 详情请见[QueryMethods.timeFormat]
     */
    @JvmStatic
    fun timeFormat(column0: QueryColumn, column1: QueryColumn): QueryColumn =
        QueryMethods.timeFormat(column0, column1)

    /**
     * 详情请见[QueryMethods.subDate]
     */
    @JvmStatic
    fun subDate(column0: String, column1: String): QueryColumn = QueryMethods.subDate(column0,
        column1)

    /**
     * 详情请见[QueryMethods.subDate]
     */
    @JvmStatic
    fun subDate(column0: QueryColumn, column1: QueryColumn): QueryColumn =
        QueryMethods.subDate(column0, column1)

    /**
     * 详情请见[QueryMethods.schema]
     */
    @JvmStatic
    fun schema(): QueryColumn = QueryMethods.schema()

    /**
     * 详情请见[QueryMethods.oct]
     */
    @JvmStatic
    fun oct(column0: String): QueryColumn = QueryMethods.oct(column0)

    /**
     * 详情请见[QueryMethods.oct]
     */
    @JvmStatic
    fun oct(column0: QueryColumn): QueryColumn = QueryMethods.oct(column0)

    /**
     * 详情请见[QueryMethods.password]
     */
    @JvmStatic
    fun password(column0: QueryColumn): QueryColumn = QueryMethods.password(column0)

    /**
     * 详情请见[QueryMethods.password]
     */
    @JvmStatic
    fun password(column0: String): QueryColumn = QueryMethods.password(column0)

    /**
     * 详情请见[QueryMethods.md5]
     */
    @JvmStatic
    fun md5(column0: QueryColumn): QueryColumn = QueryMethods.md5(column0)

    /**
     * 详情请见[QueryMethods.md5]
     */
    @JvmStatic
    fun md5(column0: String): QueryColumn = QueryMethods.md5(column0)

    /**
     * 详情请见[QueryMethods.null_]
     */
    @JvmStatic
    fun null_(): QueryColumn = QueryMethods.null_()

    /**
     * 详情请见[QueryMethods.true_]
     */
    @JvmStatic
    fun true_(): QueryColumn = QueryMethods.true_()

    /**
     * 详情请见[QueryMethods.noCondition]
     */
    @JvmStatic
    fun noCondition(): QueryCondition = QueryMethods.noCondition()

    /**
     * 详情请见[QueryMethods.false_]
     */
    @JvmStatic
    fun false_(): QueryColumn = QueryMethods.false_()

    /**
     * 详情请见[QueryMethods.selectOne]
     */
    @JvmStatic
    fun selectOne(): QueryWrapper = QueryMethods.selectOne()

    /**
     * 详情请见[QueryMethods.selectCountOne]
     */
    @JvmStatic
    fun selectCountOne(): QueryWrapper = QueryMethods.selectCountOne()


    /**
     * 详情请见[QueryMethods.selectCount]
     */
    @JvmStatic
    fun selectCount(): QueryWrapper = QueryMethods.selectCount()

    /**
     * 详情请见[QueryMethods.ifNull]
     */
    @JvmStatic
    fun ifNull(column0: QueryColumn, column1: QueryColumn): QueryColumn =
        QueryMethods.ifNull(column0, column1)

    /**
     * 详情请见[QueryMethods.ifNull]
     */
    @JvmStatic
    fun ifNull(column0: String, column1: String): QueryColumn = QueryMethods.ifNull(column0,
        column1)

    /**
     * 详情请见[QueryMethods.if_]
     */
    @JvmStatic
    fun if_(
        column0: QueryCondition,
        column1: QueryColumn,
        column2: QueryColumn,
    ): QueryColumn = QueryMethods.if_(column0, column1, column2)

    /**
     * 详情请见[QueryMethods.if_]
     */
    @JvmStatic
    fun if_(
        column0: QueryCondition,
        column1: String,
        column2: String,
    ): QueryColumn = QueryMethods.if_(column0, column1, column2)

    /**
     * 详情请见[QueryMethods.truncate]
     */
    @JvmStatic
    fun truncate(column0: QueryColumn, column1: Int): QueryColumn =
        QueryMethods.truncate(column0, column1)

    /**
     * 详情请见[QueryMethods.truncate]
     */
    @JvmStatic
    fun truncate(column0: String, column1: Int): QueryColumn = QueryMethods.truncate(column0,
        column1)

    /**
     * 详情请见[QueryMethods.truncate]
     */
    @JvmStatic
    fun truncate(column0: String, column1: String): QueryColumn =
        QueryMethods.truncate(column0, column1)

    /**
     * 详情请见[QueryMethods.truncate]
     */
    @JvmStatic
    fun truncate(column0: QueryColumn, column1: QueryColumn): QueryColumn =
        QueryMethods.truncate(column0, column1)

    /**
     * 详情请见[QueryMethods.power]
     */
    @JvmStatic
    fun power(column0: QueryColumn, column1: Int): QueryColumn = QueryMethods.power(column0,
        column1)

    /**
     * 详情请见[QueryMethods.power]
     */
    @JvmStatic
    fun power(column0: String, column1: Int): QueryColumn = QueryMethods.power(column0,
        column1)

    /**
     * 详情请见[QueryMethods.power]
     */
    @JvmStatic
    fun power(column0: String, column1: String): QueryColumn = QueryMethods.power(column0,
        column1)

    /**
     * 详情请见[QueryMethods.power]
     */
    @JvmStatic
    fun power(column0: QueryColumn, column1: QueryColumn): QueryColumn =
        QueryMethods.power(column0, column1)

    /**
     * 详情请见[QueryMethods.instr]
     */
    @JvmStatic
    fun instr(column0: String, column1: String): QueryColumn = QueryMethods.instr(column0,
        column1)

    /**
     * 详情请见[QueryMethods.instr]
     */
    @JvmStatic
    fun instr(column0: QueryColumn, column1: QueryColumn): QueryColumn =
        QueryMethods.instr(column0, column1)

}