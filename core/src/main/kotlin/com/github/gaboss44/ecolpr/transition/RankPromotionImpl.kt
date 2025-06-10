package com.github.gaboss44.ecolpr.transition

import com.github.gaboss44.ecolpr.rank.Rank
import com.github.gaboss44.ecolpr.road.Road
import org.bukkit.entity.Player

class RankPromotionImpl(
    override val player: Player,
    override val source: RankTransition.Source,
    override val status: RankTransition.Status,
    override val road: Road,
    override val from: Rank? = null,
    override val to: Rank
) : RankPromotion{
    override val type = RankTransition.Type.PROMOTION
}