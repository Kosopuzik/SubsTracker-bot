package com.subs.service

import com.subs.dto.BotMessageDto
import com.subs.holder.BotPropHolder
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

@Component
class BotInteractionService(botPropHolder: BotPropHolder) : TelegramLongPollingBot(botPropHolder.botToken) {


    private val botName: String = botPropHolder.botUsername

    override fun getBotUsername(): String = botName

    override fun onUpdateReceived(update: Update?) {
        //LOGGER.info("Success. Message received")

        if (update == null) {
            return;
        }

        if (update.hasMessage() && update.message.hasText()) {

            val userTgMsg: Message = update.message;
            val chatId: Long = update.message.chat.id;

            val userId: Long = update.message.from.id;

            val userIdTxt: String = userId.toString();
            val chatIdTxt: String = chatId.toString();

            val userActualMessage: String = userTgMsg.text

//            LOGGER.info("Received a message from userId: {}, chatId: ", userId, chatId)
//            LOGGER.info("User allowed {} to communicate with the chat", userId)


            //val botResponseForMessage = botScenarioProcessor.process(UserMessagePayload(userIdTxt, userActualMessage))

            val botResponsePayload = BotMessageDto(chatIdTxt, "test111")

            this.send(botResponsePayload)
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
            //LOGGER.error("Error sending message to user thru bot, chatId: {}", payload.chatId);
        }
    }

}