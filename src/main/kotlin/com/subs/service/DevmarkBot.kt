package com.subs.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class DevmarkBot(
    @Value("\${telegram.token}")
    token: String
) : TelegramLongPollingBot(token) {

    @Value("\${telegram.botName}")
    private val botName: String = ""

    override fun getBotUsername(): String = botName

    override fun onUpdateReceived(update: Update?) {
        TODO("Not yet implemented")
    }
}