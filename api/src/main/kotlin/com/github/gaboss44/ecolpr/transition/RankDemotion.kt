package com.github.gaboss44.ecolpr.transition

import com.github.gaboss44.ecolpr.rank.Rank

interface RankDemotion : RankTransition {
    override val from: Rank
}