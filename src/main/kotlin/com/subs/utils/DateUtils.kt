package com.subs.utils

import com.subs.dto.SubscriptionTrakerDto
import com.subs.enums.TimeIntervalTypeEnum
import com.subs.holder.DateTimeHolder
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

private val LOGGER = KotlinLogging.logger {}

@Component
open class DateUtils {

    val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")

    val dateTimeHolder = DateTimeHolder()

    fun stringToDate(dateString: String): Date? {
        try {
            return simpleDateFormat.parse(dateString)
        } catch (e: ParseException) {
            return null
        }
    }

    fun dateToString(date: Date): String {
        return simpleDateFormat.format(date)
    }

    /**
     * Расчет ближайшей даты о списании подписки
     */
    fun calcPayDateTime(date: LocalDateTime, sub: SubscriptionTrakerDto): LocalDateTime? {
        if (sub.timeIntervalType == null || sub.eventTime == null) {
            LOGGER.error { "У записи подписки не заполнены поля timeIntervalType или eventTime, id = ${sub.id}" }
            return null
        }

        val recordLocalDate: LocalDateTime = sub.eventTime.toInstant().atZone(dateTimeHolder.zoneId).toLocalDateTime()

        when (sub.timeIntervalType) {
            TimeIntervalTypeEnum.MONTH -> return LocalDateTime.of(
                date.year,
                date.month,
                recordLocalDate.dayOfMonth,
                recordLocalDate.hour,
                recordLocalDate.minute
            )
            TimeIntervalTypeEnum.YEAR -> return LocalDateTime.of(
                date.year,
                recordLocalDate.month,
                recordLocalDate.dayOfMonth,
                recordLocalDate.hour,
                recordLocalDate.minute
            )
            TimeIntervalTypeEnum.FIXED_DATE -> return recordLocalDate
            else -> return null
        }
    }


    /**
     * Расчет даты времени оповещения о подписке
     */
    fun calcNotificationDateTime(request: SubscriptionTrakerDto): Date? {

        val date = Date()

        val localDate = date.toInstant().atZone(dateTimeHolder.zoneId).toLocalDateTime()

        val calcSubDateTime = calcPayDateTime(localDate, request)

        if (calcSubDateTime == null || request.timeNeedWarn == null) {
            return null
        }

        val minusHours = calcSubDateTime.minusHours(request.timeNeedWarn.toLong())

        return Date.from(minusHours.atZone(ZoneId.systemDefault()).toInstant())
    }

    /**
     * Получить сколько осталось времени до уведомления и дата время уведомления о подписке
     */
    fun getNotificationDateTime(request: SubscriptionTrakerDto): String {

        calcNotificationDateTime(request).also {
            if (it == null) {
                return "Не удалось расчитать дату уведомления"
            } else {
                val localDateStart = Date().toInstant().atZone(dateTimeHolder.zoneId).toLocalDateTime()
                val localDateEnd = it.toInstant().atZone(dateTimeHolder.zoneId).toLocalDateTime()
                if (localDateStart > localDateEnd) {
                    return "Подписка неактуальна"
                } else {
                    val duration = Duration.between(localDateStart, localDateEnd)
                    when {
                        duration.toDays() > 1 -> return "${duration.toDays()} дней (дата уведомления ${dateToString(it)})"
                        duration.toHours() > 1 -> return "${duration.toHours()} часов (дата уведомления ${
                            dateToString(
                                it
                            )
                        })"
                        else -> return "${duration.toMinutes()} минут (дата уведомления ${dateToString(it)})"
                    }
                }
            }
        }
    }
}