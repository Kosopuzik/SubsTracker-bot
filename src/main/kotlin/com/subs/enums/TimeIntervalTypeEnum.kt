package com.subs.enums

enum class TimeIntervalTypeEnum(val code: String, val description: String) {
    MONTH("1", "Число каждого месяца"),
    YEAR("2", "Число каждого года"),
    FIXED_DATE("3", "Фиксированная дата");
}

fun String.getTimeIntervalTypeEnumByCode() : TimeIntervalTypeEnum {
    return TimeIntervalTypeEnum.values().find { it.code == this } as TimeIntervalTypeEnum
}