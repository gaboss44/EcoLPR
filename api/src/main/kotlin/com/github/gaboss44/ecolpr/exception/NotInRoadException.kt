package com.github.gaboss44.ecolpr.exception

import com.github.gaboss44.ecolpr.rank.Road

class NotInRoadException(val road: Road, message: String) : RankTransitionException(message)