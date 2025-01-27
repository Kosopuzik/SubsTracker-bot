package com.subs.myapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class SubsTrackerApp

fun main(args: Array<String>) {
//    val bot = MyAwesomeBot()

    runApplication<SubsTrackerApp>(*args)
}

//class MyAwesomeBot : TelegramLongPollingBot() {
//
//    override fun getBotUsername(): String = "ManageSubs001Bot"
//
//    override fun getBotToken(): String = "7726090136:AAEckSntsrCC-voSsZFBwYiMAMcZyN8kIXE"
//
//    override fun onUpdateReceived(update: Update?) {
//        if (update == null || update.message == null) return
//
//        val chatId = update.message.chatId.toString()
//        val messageText = update.message.text
//
//        try {
//            execute(SendMessage(chatId, "Вы отправили сообщение: $messageText"))
//        } catch (e: TelegramApiException) {
//            e.printStackTrace()
//        }
//    }
//}
