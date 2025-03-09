package com.subs.service.storage.impl

import com.subs.domain.SubscriptionTraker
import com.subs.domain.toSubscriptionTraker
import com.subs.dto.SubscriptionTrakerDto
import com.subs.dto.toSubscriptionTrakerDto
import com.subs.repository.SubscriptionJpaRepository
import com.subs.service.storage.SubscriptionStorageService
import mu.KotlinLogging
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

private val LOGGER = KotlinLogging.logger {}

@Service
class SubscriptionStorageServiceImpl(
    private val subscriptionJpaRepository: SubscriptionJpaRepository
) : SubscriptionStorageService {

    override fun getSubscriptionByServiceName(serviceName: String): SubscriptionTraker {
        LOGGER.info { "Запрос в БД. Поиск напоминания по serviceName = $serviceName" }
        return subscriptionJpaRepository.findByServiceName(serviceName)
    }

    override fun getSubscriptionByChartId(chartId: Long): List<SubscriptionTrakerDto> {
        LOGGER.info { "Запрос в БД. Поиск напоминания по chartId = $chartId" }
        return subscriptionJpaRepository.findByChartId(chartId.toString()).map { el -> el.toSubscriptionTrakerDto() }
            .toList()
            ?: emptyList()
    }

    override fun getSubscriptionById(id: Int): SubscriptionTrakerDto? {
        LOGGER.info { "Запрос в БД. Поиск напоминания по id = $id" }
        return subscriptionJpaRepository.findById(id)
            .also {
                if (it.isPresent) {
                    LOGGER.info { "В БД найдено напоминание по id = $id" }
                } else {
                    LOGGER.info { "В БД не найдено напоминание по id = $id" }
                }
            }
            .getOrNull()?.toSubscriptionTrakerDto()
    }

    override fun delete(subscriptionTrakerDto: SubscriptionTrakerDto) {
        subscriptionJpaRepository.delete(subscriptionTrakerDto.toSubscriptionTraker())
    }

    override fun createOrUpdate(subscriptionTrakerDto: SubscriptionTrakerDto): SubscriptionTrakerDto {
        return subscriptionJpaRepository.save(subscriptionTrakerDto.toSubscriptionTraker()).toSubscriptionTrakerDto()
    }
}