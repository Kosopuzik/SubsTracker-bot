package com.subs.repository

import org.springframework.data.jpa.repository.JpaRepository
import com.subs.domain.SubscriptionTraker
import org.springframework.stereotype.Component
import java.util.*

@Component
interface SubscriptionJpaRepository : JpaRepository<SubscriptionTraker, Long> {

    fun findByServiceName(serviceName: String): SubscriptionTraker

    fun findById(id: Int): Optional<SubscriptionTraker>

    fun findByChartId(chartId: String): List<SubscriptionTraker>
}