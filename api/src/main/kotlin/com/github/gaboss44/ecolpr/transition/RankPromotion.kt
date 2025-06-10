package com.github.gaboss44.ecolpr.transition

import com.github.gaboss44.ecolpr.rank.Rank

interface RankPromotion : RankTransition {
    override val to: Rank
}