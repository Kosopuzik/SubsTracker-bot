package com.subs.repository

import com.subs.domain.UserState
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component

@Component
interface UserStateJpaRepository : JpaRepository<UserState, Long> {

    fun findByChartIdAndStateIsNot(chartId: String, state: String): List<UserState>
}