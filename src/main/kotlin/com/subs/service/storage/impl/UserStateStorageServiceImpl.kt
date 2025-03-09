package com.subs.service.storage.impl

import com.subs.domain.UserState
import com.subs.domain.toUserState
import com.subs.dto.UserStateDto
import com.subs.dto.toUserStateDto
import com.subs.repository.UserStateJpaRepository
import com.subs.service.storage.UserStateStorageService
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val LOGGER = KotlinLogging.logger {}

@Service
class UserStateStorageServiceImpl(private val userStateJpaRepository: UserStateJpaRepository) :
    UserStateStorageService {

    override fun getUserStateByChartIdAndStateIsNot(chartId: Long, state: String): UserStateDto? {
        LOGGER.info { "Запрос в БД. Поиск состояния пользователя для chartId = $chartId" }

        val findByChartIdAndStateIsNot = userStateJpaRepository.findByChartIdAndStateIsNot(chartId.toString(), state)

        return if (findByChartIdAndStateIsNot.isNotEmpty()) findByChartIdAndStateIsNot.first().toUserStateDto() else null
    }

    override fun createOrUpdate(userStateDto: UserStateDto): UserStateDto {
        val userState: UserState = userStateDto.toUserState()
        return userStateJpaRepository.save(userState).toUserStateDto()
    }
}