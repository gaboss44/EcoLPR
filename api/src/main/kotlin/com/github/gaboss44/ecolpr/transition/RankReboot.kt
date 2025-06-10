package com.github.gaboss44.ecolpr.transition

import com.github.gaboss44.ecolpr.rank.Rank

interface RankReboot : RankTransition, PrestigeAugment {
    override val from: Rank
    override val to: Rank
}