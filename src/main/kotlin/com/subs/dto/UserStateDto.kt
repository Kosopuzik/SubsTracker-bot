package com.subs.dto

import com.subs.domain.UserState
import com.subs.enums.BotStateEnum
import com.subs.enums.getBotStateEnumByCode

/**
 * DTO состояния обработки пользователя
 */
data class UserStateDto(
    val id: Int? = null,
    val chartId: String? = null,
    val state: BotStateEnum,
    val subTracker: SubscriptionTrakerDto? = null
)

fun UserState.toUserStateDto() = UserStateDto(
    id = this.id,
    chartId = this.chartId,
    state = this.state?.getBotStateEnumByCode() ?: BotStateEnum.END,
    subTracker = this.subTracker?.toSubscriptionTrakerDto()
)