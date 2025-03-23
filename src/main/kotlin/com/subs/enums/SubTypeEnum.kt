package com.subs.enums

enum class SubTypeEnum(val code: String, val description: String) {
    PAID("1", "Очередное списание"),
    FREE_PERIOD("2", "Истечение бесплатного периода подписки");
}

fun String.getSubTypeEnumByCode() : SubTypeEnum {
    return SubTypeEnum.values().find {
        (it.code == this)
    } as SubTypeEnum
}