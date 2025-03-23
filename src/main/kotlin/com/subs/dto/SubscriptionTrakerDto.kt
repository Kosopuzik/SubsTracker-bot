package com.subs.dto

import com.subs.domain.SubscriptionTraker
import com.subs.enums.SubTypeEnum
import com.subs.enums.TimeIntervalTypeEnum
import com.subs.enums.getSubTypeEnumByCode
import com.subs.enums.getTimeIntervalTypeEnumByCode
import com.subs.utils.DateUtils
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

/**
 * DTO подписка
 */
data class SubscriptionTrakerDto(
    val id: Int? = null,
    val chartId: String? = null,
    val serviceName: String? = null,
    val subName: String? = null,
    val subType: SubTypeEnum? = null,
    val amount: BigDecimal? = null,
    val timeIntervalType: TimeIntervalTypeEnum? = null,
    val eventTime: Date? = null,
    val timeNeedWarn: Int? = null
)

fun SubscriptionTraker.toSubscriptionTrakerDto() = SubscriptionTrakerDto(
    id = this.id,
    chartId = this.chartId,
    serviceName = this.serviceName,
    subName = this.subName,
    subType = this.subType?.getSubTypeEnumByCode(),
    amount = this.amount,
    timeIntervalType = this.timeIntervalType?.getTimeIntervalTypeEnumByCode(),
    eventTime = this.eventTime,
    timeNeedWarn = this.timeNeedWarn
)

fun SubscriptionTrakerDto.getString(): String {
    val dateUtils = DateUtils()

    return "Напоминание через <b>${dateUtils.getNotificationDateTime(this)}</b> о <b>${this.subType?.description} ${
        this.amount?.setScale(
            2,
            RoundingMode.HALF_UP
        ).toString()
    } RUB</b> за подписку <b>${this.serviceName}</b> от <b>${this.subName}</b> (ID = <b>${this.id}</b>)"
}