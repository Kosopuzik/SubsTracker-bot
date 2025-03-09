package com.subs.domain

import com.subs.dto.UserStateDto
import jakarta.persistence.*

@Entity
@Table(name = "T_USER_STATE")
open class UserState(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "C_ID")
    var id: Int? = null,

    @Column(name = "C_CHART_ID")
    val chartId: String? = null,

    @Column(name = "C_STATE")
    val state: String? = null,

    @JoinColumn(name = "C_SUBTRACKER_ID", referencedColumnName = "C_ID")
    @ManyToOne(fetch = FetchType.EAGER)
    val subTracker: SubscriptionTraker? = null
)

fun UserStateDto.toUserState() = UserState(
    id = this.id,
    chartId = this.chartId,
    state = this.state.code,
    subTracker = this.subTracker?.toSubscriptionTraker()
)