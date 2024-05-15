package com.mybatisflex.kotlin.ksp.internal.util

import java.math.BigDecimal
import java.math.BigInteger
import java.sql.Time
import java.sql.Timestamp
import java.time.*
import java.time.chrono.JapaneseDate
import java.util.*


val DEFAULT_SUPPORT_COLUMN_TYPES by lazy {
    setOf(
        Int::class.qualifiedName,
        Short::class.qualifiedName,
        Long::class.qualifiedName,
        Float::class.qualifiedName,
        Double::class.qualifiedName,
        Boolean::class.qualifiedName,
        Date::class.qualifiedName,
        java.sql.Date::class.qualifiedName,
        Time::class.qualifiedName,
        Timestamp::class.qualifiedName,
        Instant::class.qualifiedName,
        LocalDate::class.qualifiedName,
        LocalDateTime::class.qualifiedName,
        LocalTime::class.qualifiedName,
        OffsetDateTime::class.qualifiedName,
        OffsetTime::class.qualifiedName,
        ZonedDateTime::class.qualifiedName,
        Year::class.qualifiedName,
        Month::class.qualifiedName,
        YearMonth::class.qualifiedName,
        JapaneseDate::class.qualifiedName,
        ByteArray::class.qualifiedName,
        Array<Byte>::class.qualifiedName,
        Byte::class.qualifiedName,
        BigInteger::class.qualifiedName,
        BigDecimal::class.qualifiedName,
        String::class.qualifiedName,
        Char::class.qualifiedName
    )
}