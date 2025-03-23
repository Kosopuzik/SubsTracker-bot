package com.subs.service

import com.subs.domain.SubscriptionTraker
import com.subs.repository.SubscriptionJpaRepository
import com.subs.repository.UserStateJpaRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.ZoneId
import java.util.Date

private val LOGGER = KotlinLogging.logger {}

@Service
class DataLoader {

    @Autowired
    lateinit var subscriptionJpaRepository: SubscriptionJpaRepository

    @Autowired
    lateinit var userStateJpaRepository: UserStateJpaRepository

    @Bean
    fun initDatabase(): CommandLineRunner {
        return CommandLineRunner {
            val localDate = Date().toInstant().atZone(ZoneId.of("Europe/Moscow")).toLocalDateTime()

            // Заполнение базы данных тестовыми данными
            val subscriptionTrakerLists = listOf(
                SubscriptionTraker(chartId = "279656922", serviceName = "name", subName = "subName", subType = "1", amount = BigDecimal(3400.0), timeIntervalType = "1", eventTime = Date.from(localDate.plusDays(5).atZone(ZoneId.systemDefault()).toInstant()), timeNeedWarn = 24),
                SubscriptionTraker(chartId = "279656922", serviceName = "name3", subName = "subName3", subType = "1", amount = BigDecimal(1200.0), timeIntervalType = "1", eventTime = Date.from(localDate.plusDays(1).atZone(ZoneId.systemDefault()).toInstant()), timeNeedWarn = 12),
                SubscriptionTraker(chartId = "279656922", serviceName = "name4", subName = "subName4", subType = "1", amount = BigDecimal(500.5), timeIntervalType = "1", eventTime = Date.from(localDate.plusHours(2).atZone(ZoneId.systemDefault()).toInstant()), timeNeedWarn = 1),
                SubscriptionTraker(chartId = "279656922", serviceName = "name2", subName = "subName2", subType = "2", amount = BigDecimal(50000.0), timeIntervalType = "2", eventTime = Date.from(localDate.plusDays(30).atZone(ZoneId.systemDefault()).toInstant()), timeNeedWarn = 48)
            )
           // val userState: UserState = UserState(chartId = "279656922", state = BotStateEnum.SERVICE_NAME.code);

            LOGGER.info("Заполняем базу данных...")
            subscriptionJpaRepository.saveAll(subscriptionTrakerLists)
          //  userStateJpaRepository.save(userState)
            LOGGER.info("База данных заполнена!")
        }
    }
}