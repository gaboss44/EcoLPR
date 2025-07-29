package com.github.gaboss44.ecolpr.core.transition

import com.github.gaboss44.ecolpr.api.model.road.Road
import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.core.util.cast
import org.bukkit.entity.Player
import java.util.UUID

class ApiTransitionManager(
    val handle: TransitionManager
) : com.github.gaboss44.ecolpr.api.transition.TransitionManager {

    override fun isLocked(uuid: UUID) = this.handle.isLocked(uuid)

    override fun rankup(
        player: Player,
        road: Road,
        options: Transition.Options
    ) = this.handle.rankup(
        player,
        road.cast(),
        Transition.Source.API,
        options
    ).proxy

    override fun ingress(
        player: Player,
        road: Road,
        options: Transition.Options
    ) = this.handle.ingress(
        player,
        road.cast(),
        Transition.Source.API,
        options
    ).proxy

    override fun ascend(
        player: Player,
        road: Road,
        options: Transition.Options
    ) = this.handle.ascend(
        player,
        road.cast(),
        Transition.Source.API,
        options
    ).proxy

    override fun prestige(
        player: Player,
        road: Road,
        options: Transition.Options
    ) = this.handle.prestige(
        player,
        road.cast(),
        Transition.Source.API,
        options
    ).proxy

    override fun recurse(
        player: Player,
        road: Road,
        options: Transition.Options
    ) = this.handle.recurse(
        player,
        road.cast(),
        Transition.Source.API,
        options
    ).proxy

    override fun egress(
        player: Player,
        road: Road,
        options: Transition.Options
    ) = this.handle.egress(
        player,
        road.cast(),
        Transition.Source.API,
        options
    ).proxy

    override fun migrate(
        player: Player,
        road: Road,
        options: Transition.Options
    ) = this.handle.migrate(
        player,
        road.cast(),
        Transition.Source.API,
        options
    ).proxy
}