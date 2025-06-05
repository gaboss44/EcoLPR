package com.github.gaboss44.ecolpr.rank

class RankTransitionImpl(
    override val road: Road,
    override val from: Rank,
    override val to: Rank,
    override val direction: RankTransition.Direction,
    override val flow: RankTransition.Flow,
    override val source: RankTransition.Source,
    override val status: RankTransition.Status
) : RankTransition