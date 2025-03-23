package com.subs.domain

import com.subs.dto.SubscriptionTrakerDto
import jakarta.persistence.*
import java.math.BigDecimal
import java.util.*

@Entity
@Table(name = "T_SUBSCRIPTION_TRACKER")
open class SubscriptionTraker (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "C_ID")
    var id: Int? = null,

    @Column(name = "C_CHART_ID")
    val chartId: String? = null,

    @Column(name = "C_SERVICE_NAME")
    val serviceName: String? = null,

    @Column(name = "C_SUB_NAME")
    val subName: String? = null,

    @Column(name = "C_SUB_TYPE")
    val subType: String? = null,

    @Column(name = "C_AMOUNT", precision = 10, scale = 4)
    val amount: BigDecimal? = null,

    @Column(name = "C_TIME_INTERVAL_TYPE")
    val timeIntervalType: String? = null,

    @Column(name = "C_EVENT_TIME")
    val eventTime: Date? = null,

    @Column(name = "C_TIME_NEED_WARN")
    val timeNeedWarn: Int? = null
)

fun SubscriptionTrakerDto.toSubscriptionTraker() = SubscriptionTraker(
    id = this.id,
    chartId = this.chartId,
    serviceName = this.serviceName,
    subName = this.subName,
    subType = this.subType?.code,
    amount = this.amount,
    timeIntervalType = this.timeIntervalType?.code,
    eventTime = this.eventTime,
    timeNeedWarn = this.timeNeedWarn
)