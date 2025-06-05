package com.github.gaboss44.ecolpr.event

import com.github.gaboss44.ecolpr.rank.Rank
import com.github.gaboss44.ecolpr.rank.Road
import org.jetbrains.annotations.ApiStatus

/**
 * Implementations justify the absence of a [Rank] getter in this interface
 */
@ApiStatus.NonExtendable
interface RankEvent {
    val road: Road
}