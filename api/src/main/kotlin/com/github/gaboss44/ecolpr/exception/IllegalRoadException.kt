package com.github.gaboss44.ecolpr.exception

import com.github.gaboss44.ecolpr.road.Road

class IllegalRoadException(road: Road, message: String) : RankTransitionException(message)