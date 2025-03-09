package com.subs.dto

import com.subs.domain.SubscriptionTraker
import java.math.BigDecimal

/**
 * DTO подписка
 */
data class SubscriptionTrakerDto(
    val id: Int? = null,
    val chartId: String? = null,
    val serviceName: String? = null,
    val subName: String? = null,
    val subType: String? = null,
    val amount: BigDecimal? = null,
    val timeIntervalType: String? = null,
    val timeNeedWarn: String? = null
)

fun SubscriptionTraker.toSubscriptionTrakerDto() = SubscriptionTrakerDto(
    id = this.id,
    chartId = this.chartId,
    serviceName = this.serviceName,
    subName = this.subName,
    subType = this.subType,
    amount = this.amount,
    timeIntervalType = this.timeIntervalType,
    timeNeedWarn = this.timeNeedWarn
)