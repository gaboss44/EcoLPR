package com.github.gaboss44.ecolpr.transition

import com.github.gaboss44.ecolpr.road.Road
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.UUID

interface RankTransitioner {

    fun isLocked(uuid: UUID): Boolean

    fun isLocked(player: OfflinePlayer): Boolean =
        isLocked(player.uniqueId)

    fun promote(player: Player): RankTransitionResult<RankPromotion>

    fun promote(player: Player, selectMostSatisfactoryRoad: Boolean): RankTransitionResult<RankPromotion>

    fun promote(player: Player, road: Road): RankTransitionResult<RankPromotion>

    fun demote(player: Player): RankTransitionResult<RankDemotion>

    fun demote(player: Player, selectMostSatisfactoryRoad: Boolean): RankTransitionResult<RankDemotion>

    fun demote(player: Player, road: Road): RankTransitionResult<RankDemotion>

    fun graduate(player: Player): RankTransitionResult<RankGraduation>

    fun graduate(player: Player, selectMostSatisfactoryRoad: Boolean): RankTransitionResult<RankGraduation>

    fun graduate(player: Player, road: Road): RankTransitionResult<RankGraduation>

    fun graduate(player: Player, selectMostSatisfactoryRoad: Boolean, nextRoad: Road?): RankTransitionResult<RankGraduation>

    fun graduate(player: Player, road: Road, nextRoad: Road?): RankTransitionResult<RankGraduation>

    fun reboot(player: Player): RankTransitionResult<RankReboot>

    fun reboot(player: Player, selectMostSatisfactoryRoad: Boolean): RankTransitionResult<RankReboot>

    fun reboot(player: Player, road: Road): RankTransitionResult<RankReboot>
}