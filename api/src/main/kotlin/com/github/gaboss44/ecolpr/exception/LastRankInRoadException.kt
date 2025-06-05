package com.github.gaboss44.ecolpr.exception

import com.github.gaboss44.ecolpr.rank.Rank
import com.github.gaboss44.ecolpr.rank.Road

class LastRankInRoadException(
    val road: Road,
    val rank: Rank,
    message: String
) : RankTransitionException(message)