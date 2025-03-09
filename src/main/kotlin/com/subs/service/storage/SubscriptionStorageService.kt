package com.subs.service.storage

import com.subs.domain.SubscriptionTraker
import com.subs.dto.SubscriptionTrakerDto

interface SubscriptionStorageService {

    fun getSubscriptionByServiceName(serviceName: String): SubscriptionTraker

    fun getSubscriptionByChartId(chartId: Long): List<SubscriptionTrakerDto>

    fun getSubscriptionById(id: Int): SubscriptionTrakerDto?

    fun delete(subscriptionTrakerDto : SubscriptionTrakerDto)

    fun createOrUpdate(subscriptionTrakerDto: SubscriptionTrakerDto): SubscriptionTrakerDto
}