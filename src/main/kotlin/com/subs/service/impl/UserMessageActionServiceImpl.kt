package com.subs.service.impl

import com.subs.dto.*
import com.subs.enums.*
import com.subs.service.UserMessageActionService
import com.subs.service.storage.SubscriptionStorageService
import com.subs.service.storage.UserStateStorageService
import com.subs.utils.DateUtils
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

private val LOGGER = KotlinLogging.logger {}

private val CREATING_SUB_QUESTIONS_LIST: List<BotStateEnum> = listOf(
    BotStateEnum.DELETE,
    BotStateEnum.SERVICE_NAME,
    BotStateEnum.SUB_NAME,
    BotStateEnum.SUB_TYPE,
    BotStateEnum.AMOUNT,
    BotStateEnum.TIME_INTERVAL_TYPE,
    BotStateEnum.SUB_END_DATE,
    BotStateEnum.TIME_NEED_WARN
)

@Service
class UserMessageActionServiceImpl(
    private val subscriptionStorageService: SubscriptionStorageService,
    private val userStateStorageService: UserStateStorageService,
    private val dateUtils: DateUtils
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
                BotStateEnum.SERVICE_NAME -> return setServiceName(chatId, userStateDto, messageText)
                BotStateEnum.SUB_NAME -> return setSubName(chatId, userStateDto, messageText)
                BotStateEnum.AMOUNT -> return setAmount(chatId, userStateDto, messageText)
                BotStateEnum.SUB_TYPE -> return setSubType(chatId, userStateDto, messageText)
                BotStateEnum.TIME_INTERVAL_TYPE -> return setTimeIntervalType(chatId, userStateDto, messageText)
                BotStateEnum.SUB_END_DATE -> return setSubEndDate(chatId, userStateDto, messageText)
                BotStateEnum.TIME_NEED_WARN -> return setTimeNeedWarn(chatId, userStateDto, messageText)
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
//                text =
//                    it.map { subscriptionTrakerDto -> "\n${subscriptionTrakerDto.getString()}" }
//                        .toString()
                var itemIndex = 0
                text = botStateEnum.description + it.joinToString(";") { subscriptionTrakerDto -> "\n<b>${++itemIndex}</b>) ${subscriptionTrakerDto.getString()}" }
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

        val text: String

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

    fun setServiceName(chatId: Long, userStateDto: UserStateDto, messageText: String): BotMessageDto {
        LOGGER.info { "Пришел запрос на сохранение serviceName = $messageText" }

        val subscriptionTrakerDto = SubscriptionTrakerDto(chartId = chatId.toString(), serviceName = messageText)

        subscriptionStorageService.createOrUpdate(subscriptionTrakerDto).also {
            val updateUserStateDto = userStateDto.copy(state = BotStateEnum.SUB_NAME, subTracker = it)
            userStateStorageService.createOrUpdate(updateUserStateDto)

            return BotMessageDto(chatId.toString(), BotStateEnum.SUB_NAME.description)
        }
    }

    fun setSubName(chatId: Long, userStateDto: UserStateDto, messageText: String): BotMessageDto {
        LOGGER.info { "Пришел запрос на сохранение subName = $messageText" }

        userStateDto.subTracker.also { it ->
            if (it == null) {
                return BotMessageDto(chatId.toString(), "Ошибка обработки создания подписки")
            }

            subscriptionStorageService.createOrUpdate(it.copy(subName = messageText)).also {
                val updateUserStateDto = userStateDto.copy(state = BotStateEnum.AMOUNT, subTracker = it)
                userStateStorageService.createOrUpdate(updateUserStateDto)

                return BotMessageDto(chatId.toString(), BotStateEnum.AMOUNT.description)
            }
        }
    }

    fun setAmount(chatId: Long, userStateDto: UserStateDto, messageText: String): BotMessageDto {
        LOGGER.info { "Пришел запрос на сохранение amount = $messageText" }

        userStateDto.subTracker.also {
            if (it == null) {
                return BotMessageDto(chatId.toString(), "Ошибка обработки создания подписки")
            }

            try {
                val amount: BigDecimal = messageText.toBigDecimal()

                subscriptionStorageService.createOrUpdate(it.copy(amount = amount)).also { subscriptionTrakerDto ->
                    val updateUserStateDto =
                        userStateDto.copy(state = BotStateEnum.SUB_TYPE, subTracker = subscriptionTrakerDto)
                    userStateStorageService.createOrUpdate(updateUserStateDto)

                    return BotMessageDto(chatId.toString(), BotStateEnum.SUB_TYPE.description)
                }
            } catch (ex: NumberFormatException) {
                LOGGER.error { "Строка $messageText не является числом, chatId = $chatId" }
                return BotMessageDto(chatId.toString(), "Вы ввели некорректную сумму списания - $messageText")
            }
        }
    }

    fun setSubType(chatId: Long, userStateDto: UserStateDto, messageText: String): BotMessageDto {
        LOGGER.info { "Пришел запрос на сохранение subType = $messageText" }

        userStateDto.subTracker.also {
            if (it == null) {
                return BotMessageDto(chatId.toString(), "Ошибка обработки создания подписки")
            }

            try {
                val subTypeEnum: SubTypeEnum = messageText.getSubTypeEnumByCode()

                subscriptionStorageService.createOrUpdate(it.copy(subType = subTypeEnum))
                    .also { subscriptionTrakerDto ->
                        val updateUserStateDto = userStateDto.copy(
                            state = BotStateEnum.TIME_INTERVAL_TYPE,
                            subTracker = subscriptionTrakerDto
                        )
                        userStateStorageService.createOrUpdate(updateUserStateDto)

                        return BotMessageDto(chatId.toString(), BotStateEnum.TIME_INTERVAL_TYPE.description)
                    }
            } catch (ex: NullPointerException) {
                LOGGER.error { "Строка $messageText не является типом события, chatId = $chatId" }
                return BotMessageDto(chatId.toString(), "Вы ввели некорректный тип события - $messageText")
            }
        }
    }

    fun setTimeIntervalType(chatId: Long, userStateDto: UserStateDto, messageText: String): BotMessageDto {
        LOGGER.info { "Пришел запрос на сохранение timeIntervalType = $messageText" }

        userStateDto.subTracker.also {
            if (it == null) {
                return BotMessageDto(chatId.toString(), "Ошибка обработки создания подписки")
            }

            try {
                val timeIntervalTypeEnum: TimeIntervalTypeEnum = messageText.getTimeIntervalTypeEnumByCode()

                subscriptionStorageService.createOrUpdate(it.copy(timeIntervalType = timeIntervalTypeEnum))
                    .also { subscriptionTrakerDto ->
                        val updateUserStateDto =
                            userStateDto.copy(state = BotStateEnum.SUB_END_DATE, subTracker = subscriptionTrakerDto)
                        userStateStorageService.createOrUpdate(updateUserStateDto)

                        return BotMessageDto(chatId.toString(), BotStateEnum.SUB_END_DATE.description)
                    }
            } catch (ex: NullPointerException) {
                LOGGER.error { "Строка $messageText не является типом даты окончания подписки, chatId = $chatId" }
                return BotMessageDto(
                    chatId.toString(),
                    "Вы ввели некорректный тип даты окончания подписки - $messageText"
                )
            }
        }
    }

    fun setSubEndDate(chatId: Long, userStateDto: UserStateDto, messageText: String): BotMessageDto {
        LOGGER.info { "Пришел запрос на сохранение subEndDate = $messageText" }

        userStateDto.subTracker.also {
            if (it == null) {
                return BotMessageDto(chatId.toString(), "Ошибка обработки создания подписки")
            }

            val dateTime: Date? = dateUtils.stringToDate(messageText)

            if (dateTime == null) {
                LOGGER.error { "Строка $messageText не является датой, chatId = $chatId" }
                return BotMessageDto(
                    chatId.toString(),
                    "Вы ввели некорректную дату окончания подписки - $messageText"
                )
            }

            subscriptionStorageService.createOrUpdate(it.copy(eventTime = dateTime)).also { subscriptionTrakerDto ->
                val updateUserStateDto =
                    userStateDto.copy(state = BotStateEnum.TIME_NEED_WARN, subTracker = subscriptionTrakerDto)
                userStateStorageService.createOrUpdate(updateUserStateDto)

                return BotMessageDto(chatId.toString(), BotStateEnum.TIME_NEED_WARN.description)
            }
        }
    }

    fun setTimeNeedWarn(chatId: Long, userStateDto: UserStateDto, messageText: String): BotMessageDto {
        LOGGER.info { "Пришел запрос на сохранение timeNeedWarn = $messageText" }

        userStateDto.subTracker.also {
            if (it == null) {
                return BotMessageDto(chatId.toString(), "Ошибка обработки создания подписки")
            }

            try {
                val timeNeedWarn: Int = messageText.toInt()

                subscriptionStorageService.createOrUpdate(it.copy(timeNeedWarn = timeNeedWarn))
                    .also { subscriptionTrakerDto ->
                        val updateUserStateDto =
                            userStateDto.copy(state = BotStateEnum.END, subTracker = subscriptionTrakerDto)
                        userStateStorageService.createOrUpdate(updateUserStateDto)

                        return BotMessageDto(chatId.toString(), BotStateEnum.END.description)
                    }
            } catch (ex: NumberFormatException) {
                LOGGER.error { "Строка $messageText не является числом, chatId = $chatId" }
                return BotMessageDto(chatId.toString(), "Вы ввели некорректное количество часов - $messageText")
            }
        }
    }

}