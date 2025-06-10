package com.github.gaboss44.ecolpr.exception

import com.github.gaboss44.ecolpr.road.Road

class NoRanksInRoadException(val road: Road, message: String) : RankTransitionException(message)