package com.github.gaboss44.ecolpr.api.model.road

interface RoadManager {

    fun getRoad(name: String) : Road?

    fun getLoadedRoads() : Set<Road>
}