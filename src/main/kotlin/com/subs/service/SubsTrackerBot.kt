package com.subs.service

import com.subs.dto.BotMessageDto
import com.subs.dto.UserActionRequestDto
import com.subs.service.storage.SubscriptionStorageService
import com.subs.service.storage.UserStateStorageService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

private val LOGGER = KotlinLogging.logger {}

@Component
class SubsTrackerBot(
    @Value("\${telegram.token}")
    token: String,
    private val subscriptionStorageService: SubscriptionStorageService,
    private val userMessageActionService: UserMessageActionService
) : TelegramLongPollingBot(token) {

    @Value("\${telegram.botName}")
    private val botName: String = ""

    override fun getBotUsername(): String = botName

    override fun onUpdateReceived(update: Update?) {

        LOGGER.info("Success. Message received")

        if (update == null) {
            return;
        }

        if (update.hasMessage() && update.message.hasText()) {

            val userTgMsg: Message = update.message;
            val chatId: Long = update.message.chat.id;

            val userId: Long = update.message.from.id;

            val userIdTxt: String = userId.toString();
            val chatIdTxt: String = chatId.toString();

            val messageText: String = userTgMsg.text.trim()

            LOGGER.info("Received a message from userId: {}, chatId: ", userId, chatId)
            LOGGER.info("User allowed {} to communicate with the chat", userId)

            val botMessageDto = userMessageActionService.processing(UserActionRequestDto(chatId, messageText))
            send(botMessageDto)
        }

    }

    // отсылание сообщения в формате HTML (для жирного текста/курсива и прочего форматирования)
    private fun send(payload: BotMessageDto) {

        val tgMessage = SendMessage()
        tgMessage.setChatId(payload.chatId)
        tgMessage.text = payload.text
        tgMessage.parseMode = "HTML";

        try {
            execute(tgMessage)
        } catch (e: TelegramApiException) {
            LOGGER.error("Error sending message to user thru bot, chatId: {}", payload.chatId);
        }
    }

    private fun getSub(chatId: Long) {
        val subs = subscriptionStorageService.getSubscriptionByServiceName("name")
        val name: String =
            "Напоминание через <время> о ${subs.subType} ${subs.amount} RUB за подписку ${subs.serviceName} от ${subs.serviceName} (<${subs.id}>)"

//        val subList = subscriptionStorageService.getSubscriptionByChartId(chatId)
//        val text: String = subList.map { "\nНапоминание через <время> о ${it.subType} ${it.amount} RUB за подписку ${it.serviceName} от ${it.subName} (<${it.id}>)" }.toString()

        val subList = subscriptionStorageService.getSubscriptionByChartId(chatId)
        val text: String =
            subList.map { "\nНапоминание через !время! о ${it.subType} ${it.amount} RUB за подписку ${it.serviceName} от ${it.subName} (ID = ${it.id})" }
                .toString()

        val botResponsePayload = BotMessageDto(chatId.toString(), text)
        this.send(botResponsePayload)
    }

    private fun sendMessage(chatId: Long, text: String) {
        execute(SendMessage().apply {
            setChatId(chatId.toString())
            enableMarkdown(true)
            this.text = text
        })
    }
}