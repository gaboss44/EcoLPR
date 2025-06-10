package com.github.gaboss44.ecolpr.transition

import com.github.gaboss44.ecolpr.prestige.Prestige
import com.github.gaboss44.ecolpr.rank.Rank
import com.github.gaboss44.ecolpr.road.Road
import org.bukkit.entity.Player

class RankRebootImpl(
    override val player: Player,
    override val source: RankTransition.Source,
    override val status: RankTransition.Status,
    override val road: Road,
    override val from: Rank,
    override val to: Rank,
    override val prestige: Prestige,
    override val level: Int
) : RankReboot {
    override val type = RankTransition.Type.REBOOT
}