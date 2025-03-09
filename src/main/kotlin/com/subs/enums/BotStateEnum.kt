package com.subs.enums

enum class BotStateEnum(val code: String, val description: String) {
    HELP("/help", "/create - создание напоминания, /get - получить все подписки, /delete - удалить напоминание"),
    GET("/get", "Все подписки:"),
    DELETE("/delete", "Введите ID подписки, которую хотите удалить"),
    CREATE("/create", "Создание уведомления"),
    END("/end", "Действие завершено"),

    SERVICE_NAME("/serviceName", "Введите название сервиса"),
    SUB_NAME("/subName", "Введите название подписки"),
    SUB_TYPE("/subType", "Введите тип события (1 - Очередное списание, 2 - Истечение бесплатного периода подписки)"),
    AMOUNT("/amount", "Введите сумму списания"),
    TIME_INTEVAL_TYPE("/timeIntervalType", "Введите тип даты окончания подписки (1 - Число каждого месяца, 2 - Число каждого года, 3 - Фиксированная дата)"),
    SUB_DATE("/subDate", "Введите дату окончания подписки"),
    TIME_NEED_WARN("/timeNeedWarn", "Введите за сколько часов предупредить о списании"),
    ;


}

fun String.getBotStateEnumByCode() : BotStateEnum {
    return BotStateEnum.values().find {
        (it.code == this)
    } as BotStateEnum
}