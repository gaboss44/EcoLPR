package com.github.gaboss44.ecolpr.api.transition

import com.github.gaboss44.ecolpr.api.model.road.Road
import com.github.gaboss44.ecolpr.api.transition.generic.*
import com.github.gaboss44.ecolpr.api.transition.specific.*
import org.bukkit.entity.Player
import org.jetbrains.annotations.ApiStatus
import java.util.UUID

@ApiStatus.NonExtendable
interface TransitionManager {

    fun isLocked(player: Player) = this.isLocked(player.uniqueId)

    fun isLocked(uuid: UUID): Boolean

    // RANKUP

    fun rankup(player: Player, road: Road): Rankup.Call =
        rankup(player, road, Transition.Options.normal())

    fun rankup(player: Player, road: Road, mode: Transition.Mode): Rankup.Call =
        rankup(player, road, Transition.Options.mode(mode))

    fun rankup(player: Player, road: Road, options: Transition.Options): Rankup.Call

    // INGRESS

    fun ingress(player: Player, road: Road): Ingression.Call =
        ingress(player, road, Transition.Options.normal())

    fun ingress(player: Player, road: Road, mode: Transition.Mode): Ingression.Call =
        ingress(player, road, Transition.Options.mode(mode))

    fun ingress(player: Player, road: Road, options: Transition.Options): Ingression.Call

    // ASCEND

    fun ascend(player: Player, road: Road): Ascension.Call =
        ascend(player, road, Transition.Options.normal())

    fun ascend(player: Player, road: Road, mode: Transition.Mode): Ascension.Call =
        ascend(player, road, Transition.Options.mode(mode))

    fun ascend(player: Player, road: Road, options: Transition.Options): Ascension.Call

    // PRESTIGE

    fun prestige(player: Player, road: Road): Prestige.Call =
        prestige(player, road, Transition.Options.normal())

    fun prestige(player: Player, road: Road, mode: Transition.Mode): Prestige.Call =
        prestige(player, road, Transition.Options.mode(mode))

    fun prestige(player: Player, road: Road, options: Transition.Options): Prestige.Call

    // RECURSE

    fun recurse(player: Player, road: Road): Recursion.Call =
        recurse(player, road, Transition.Options.normal())

    fun recurse(player: Player, road: Road, mode: Transition.Mode): Recursion.Call =
        recurse(player, road, Transition.Options.mode(mode))

    fun recurse(player: Player, road: Road, options: Transition.Options): Recursion.Call

    // EGRESS

    fun egress(player: Player, road: Road): Egression.Call =
        egress(player, road, Transition.Options.normal())

    fun egress(player: Player, road: Road, mode: Transition.Mode): Egression.Call =
        egress(player, road, Transition.Options.mode(mode))

    fun egress(player: Player, road: Road, options: Transition.Options): Egression.Call

    // MIGRATE

    fun migrate(player: Player, road: Road): Migration.Call =
        migrate(player, road, Transition.Options.normal())

    fun migrate(player: Player, road: Road, mode: Transition.Mode): Migration.Call =
        migrate(player, road, Transition.Options.mode(mode))

    fun migrate(player: Player, road: Road, options: Transition.Options): Migration.Call

    fun migrate(player: Player, road: Road, migrationTarget: Road): Migration.Call =
        migrate(player, road, migrationTarget, Transition.Options.normal())

    fun migrate(player: Player, road: Road, migrationTarget: Road, mode: Transition.Mode): Migration.Call =
        migrate(player, road, migrationTarget, Transition.Options.mode(mode))

    fun migrate(player: Player, road: Road, migrationTarget: Road, options: Transition.Options): Migration.Call
}
