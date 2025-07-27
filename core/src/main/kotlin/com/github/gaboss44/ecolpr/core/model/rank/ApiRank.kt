package com.github.gaboss44.ecolpr.core.model.rank

import com.github.gaboss44.ecolpr.api.model.road.Road

class ApiRank(
    val handle: Rank
) : com.github.gaboss44.ecolpr.api.model.rank.Rank by handle
