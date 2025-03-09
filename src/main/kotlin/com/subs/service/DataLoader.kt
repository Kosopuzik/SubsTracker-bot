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
            // Заполнение базы данных тестовыми данными
            val subscriptionTrakerLists = listOf(
                SubscriptionTraker(chartId = "279656922", serviceName = "name", subName = "subName", subType = "type", amount = null, timeIntervalType = null, timeNeedWarn = null)                ,
                SubscriptionTraker(chartId = "279656922", serviceName = "name2", subName = "subName2", subType = "type", amount = BigDecimal(50000.0), timeIntervalType = null, timeNeedWarn = null)
            )
           // val userState: UserState = UserState(chartId = "279656922", state = BotStateEnum.SERVICE_NAME.code);

            LOGGER.info("Заполняем базу данных...")
            subscriptionJpaRepository.saveAll(subscriptionTrakerLists)
          //  userStateJpaRepository.save(userState)
            LOGGER.info("База данных заполнена!")
        }
    }
}