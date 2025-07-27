package com.github.gaboss44.ecolpr.core.util

import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.core.model.rank.ApiRank
import com.github.gaboss44.ecolpr.core.model.rank.Rank
import com.github.gaboss44.ecolpr.core.model.road.ApiRoad
import com.github.gaboss44.ecolpr.core.model.road.Road
import com.github.gaboss44.ecolpr.core.transition.dto.TransitionDto

fun Transition.asFromRankOrNull() : TransitionDto.FromRank? {
    if (this is TransitionDto.FromRank) return this
    if (this is TransitionDto.FromRank.Api) return this.handle
    return null
}

fun Transition.isFromRank() = this.asFromRankOrNull() != null

fun Transition.asToRankOrNull() : TransitionDto.ToRank? {
    if (this is TransitionDto.ToRank) return this
    if (this is TransitionDto.ToRank.Api) return this.handle
    return null
}

fun Transition.isToRank() = this.asToRankOrNull() != null

fun Transition.asResultOrNull() : TransitionDto.Result? {
    if (this is TransitionDto.Result) return this
    if (this is TransitionDto.Result.Api) return this.handle
    return null
}

fun com.github.gaboss44.ecolpr.api.model.road.Road.cast(): Road {
    if (this is ApiRoad) return this.handle
    if (this is Road) return this
    throw IllegalStateException("Road '$this' was not obtained from EcoLPR")
}

fun ApiRoad.cast() = this.handle

fun Road.cast() = this

fun com.github.gaboss44.ecolpr.api.model.rank.Rank.cast(): Rank {
    if (this is ApiRank) return this.handle
    if (this is Rank) return this
    throw IllegalStateException("Rank $this was not obtained from EcoLPR")
}

fun ApiRank.cast() = this

fun Rank.cast() = this