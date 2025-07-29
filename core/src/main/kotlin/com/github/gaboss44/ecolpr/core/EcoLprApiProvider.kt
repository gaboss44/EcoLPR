package com.github.gaboss44.ecolpr.core

import com.github.gaboss44.ecolpr.api.EcoLpr
import com.github.gaboss44.ecolpr.core.model.rank.ApiRankManager
import com.github.gaboss44.ecolpr.core.model.road.ApiRoadManager
import com.github.gaboss44.ecolpr.core.transition.ApiTransitionManager

class EcoLprApiProvider(plugin: EcoLprPlugin) : EcoLpr {

    override val rankManager = ApiRankManager()

    override val roadManager = ApiRoadManager()

    override val transitionManager = ApiTransitionManager(plugin.transitionManager)
}