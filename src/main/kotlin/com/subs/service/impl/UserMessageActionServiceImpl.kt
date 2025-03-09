package com.subs.service.impl

import com.subs.dto.BotMessageDto
import com.subs.dto.SubscriptionTrakerDto
import com.subs.dto.UserActionRequestDto
import com.subs.dto.UserStateDto
import com.subs.enums.BotStateEnum
import com.subs.service.UserMessageActionService
import com.subs.service.storage.SubscriptionStorageService
import com.subs.service.storage.UserStateStorageService
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val LOGGER = KotlinLogging.logger {}

private val CREATING_SUB_QUESTIONS_LIST: List<BotStateEnum> = listOf(
    BotStateEnum.DELETE,
    BotStateEnum.SERVICE_NAME,
    BotStateEnum.SUB_NAME,
    BotStateEnum.SUB_TYPE,
    BotStateEnum.AMOUNT,
    BotStateEnum.TIME_INTEVAL_TYPE,
    BotStateEnum.SUB_DATE,
    BotStateEnum.TIME_NEED_WARN
)

@Service
class UserMessageActionServiceImpl(
    private val subscriptionStorageService: SubscriptionStorageService,
    private val userStateStorageService: UserStateStorageService
) : UserMessageActionService {


    override
    fun processing(userActionRequestDto: UserActionRequestDto): BotMessageDto {
        val chatId: Long = userActionRequestDto.chatId
        val messageText: String = userActionRequestDto.messageText

        val userStateDto = userStateStorageService.getUserStateByChartIdAndStateIsNot(chatId, BotStateEnum.END.code)

        if (userStateDto != null) {
            if (!CREATING_SUB_QUESTIONS_LIST.contains(userStateDto.state)) {
                return BotMessageDto(chatId.toString(), "Не удалось обработать значение")
            }
            when (userStateDto.state) {
                BotStateEnum.DELETE -> return afterDelete(chatId, userStateDto, messageText)
                BotStateEnum.SERVICE_NAME -> return afterServiceName(chatId, userStateDto, messageText)
                BotStateEnum.SUB_NAME -> return afterSubName(chatId, userStateDto, messageText)
                else -> return BotMessageDto(chatId.toString(), "Не удалось обработать значение")
            }
        } else {
            val firstChar: Char = messageText[0]
            if (firstChar == '/') {
                when (messageText) {
                    BotStateEnum.HELP.code -> return help(chatId, BotStateEnum.HELP)
                    BotStateEnum.GET.code -> return getAll(chatId, BotStateEnum.GET)
                    BotStateEnum.DELETE.code -> return beforeAction(chatId, BotStateEnum.DELETE)
                    BotStateEnum.CREATE.code -> return beforeAction(chatId, BotStateEnum.SERVICE_NAME)
                }
            }
        }

        return BotMessageDto(chatId.toString(), "Не удалось обработать значение")
    }

    fun help(chatId: Long, botStateEnum: BotStateEnum): BotMessageDto {
        LOGGER.info { "Пришел запрос на ${botStateEnum.code}" }

        return BotMessageDto(chatId.toString(), botStateEnum.description)
    }

    fun getAll(chatId: Long, botStateEnum: BotStateEnum): BotMessageDto {
        LOGGER.info { "Пришел запрос на ${botStateEnum.code}" }

        subscriptionStorageService.getSubscriptionByChartId(chatId).also {
            val text: String
            if (it.isNotEmpty()) {
                text = it.map { "\nНапоминание через !время! о ${it.subType} ${it.amount} RUB за подписку ${it.serviceName} от ${it.subName} (ID = ${it.id})" }.toString()
            } else {
                text = "Напоминания не найдены"
            }
            return BotMessageDto(chatId.toString(), text)
        }
    }

    fun beforeAction(chatId: Long, botStateEnum: BotStateEnum): BotMessageDto {
        LOGGER.info { "Пришел запрос на ${botStateEnum.code}" }

        val userStateDto = UserStateDto(chartId = chatId.toString(), state = botStateEnum)
        userStateStorageService.createOrUpdate(userStateDto)

        return BotMessageDto(chatId.toString(), botStateEnum.description)
    }

    fun afterDelete(chatId: Long, userStateDto: UserStateDto, messageText: String): BotMessageDto {
        LOGGER.info { "Пришел запрос на удаление записи ID = $messageText" }

        val id: Int = try {
            messageText.toInt()
        } catch (e: Exception) {
            LOGGER.error { "Строка $messageText не является числом, chatId = $chatId" }
            return BotMessageDto(chatId.toString(), "Вы ввели некорректный id = $messageText")
        }

        val text : String

        subscriptionStorageService.getSubscriptionById(id).also {
            if (it != null) {
                subscriptionStorageService.delete(it)
                LOGGER.info { "Удалено напоминание id = $id" }
                text = "Подписка удалена"
            } else {
                LOGGER.info { "Упоминание не найдено id = $id, chartId = $chatId" }
                text = "Подписки с таким id = $id не существует"
            }
        }

        val updateUserStateDto = userStateDto.copy(state = BotStateEnum.END)
        userStateStorageService.createOrUpdate(updateUserStateDto)

        return BotMessageDto(chatId.toString(), text)
    }

    fun afterServiceName(chatId: Long, userStateDto: UserStateDto, messageText: String): BotMessageDto {
        LOGGER.info { "Пришел запрос на сохранение serviceName = $messageText" }

        val subscriptionTrakerDto = SubscriptionTrakerDto(serviceName = messageText)

        subscriptionStorageService.createOrUpdate(subscriptionTrakerDto).also {
            val updateUserStateDto = userStateDto.copy(state = BotStateEnum.SUB_NAME, subTracker = it)
            userStateStorageService.createOrUpdate(updateUserStateDto)

            return BotMessageDto(chatId.toString(), BotStateEnum.SUB_NAME.description)
        }
    }

    fun afterSubName(chatId: Long, userStateDto: UserStateDto, messageText: String): BotMessageDto {
        LOGGER.info { "Пришел запрос на сохранение serviceName = $messageText" }

        userStateDto.subTracker.also {
            if (it == null) {
                return BotMessageDto(chatId.toString(), "Ошибка обработки создания подписки")
            }

            subscriptionStorageService.createOrUpdate(it.copy(subName = messageText)).also {
                val updateUserStateDto = userStateDto.copy(state = BotStateEnum.SUB_TYPE, subTracker = it)
                userStateStorageService.createOrUpdate(updateUserStateDto)

                return BotMessageDto(chatId.toString(), BotStateEnum.SUB_TYPE.description)
            }
        }
    }
}