package com.github.gaboss44.ecolpr.api

import com.github.gaboss44.ecolpr.api.model.rank.RankManager
import com.github.gaboss44.ecolpr.api.model.road.RoadManager
import com.github.gaboss44.ecolpr.api.transition.TransitionManager
import org.jetbrains.annotations.ApiStatus

@ApiStatus.NonExtendable
interface EcoLpr {

    val rankManager: RankManager

    val roadManager: RoadManager

    val transitionManager: TransitionManager

}
