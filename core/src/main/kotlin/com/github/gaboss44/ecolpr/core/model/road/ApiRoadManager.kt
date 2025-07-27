package com.github.gaboss44.ecolpr.core.model.road

import com.github.gaboss44.ecolpr.api.model.road.RoadManager

class ApiRoadManager() : RoadManager {

    override fun getRoad(name: String) = Roads.getByID(name)?.proxy

    override fun getLoadedRoads() = Roads.proxyValues()

}