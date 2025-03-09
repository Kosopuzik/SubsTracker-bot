package com.subs.dto

/**
 * Запрос от пользователя
 */
data class UserActionRequestDto (

    val chatId: Long,

    val messageText: String
)