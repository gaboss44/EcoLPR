package com.github.gaboss44.ecolpr.transition

import com.github.gaboss44.ecolpr.rank.Rank
import com.github.gaboss44.ecolpr.road.Road

interface RankGraduation : RankTransition, PrestigeAugment {
    override val from: Rank
    val nextRoad: Road?
}