package com.subs.service.storage

import com.subs.dto.UserStateDto

interface UserStateStorageService {

    fun getUserStateByChartIdAndStateIsNot(chartId: Long, state: String): UserStateDto?

    fun createOrUpdate(userStateDto: UserStateDto): UserStateDto
}