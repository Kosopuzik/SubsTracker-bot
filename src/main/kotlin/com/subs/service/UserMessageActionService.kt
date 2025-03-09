package com.subs.service

import com.subs.dto.BotMessageDto
import com.subs.dto.UserActionRequestDto

/**
 * Сервис для обработки сообщений пользователя
 */
interface UserMessageActionService {

    fun processing(userActionRequestDto: UserActionRequestDto) : BotMessageDto
}