package com.github.gaboss44.ecolpr.event

import com.github.gaboss44.ecolpr.rank.Rank
import com.github.gaboss44.ecolpr.rank.RankTransition
import org.jetbrains.annotations.ApiStatus

@ApiStatus.NonExtendable
interface RankTransitionEvent : RankEvent {
    val from: Rank?
    val to: Rank?
    val direction: RankTransition.Direction
    val flow: RankTransition.Flow
    val source: RankTransition.Source
    val status: RankTransition.Status
}