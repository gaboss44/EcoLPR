package com.github.gaboss44.ecolpr.exception

import com.github.gaboss44.ecolpr.prestige.Prestige

class PrestigeMaxLevelException(val prestige: Prestige, val level: Int, message: String): RankTransitionException(message)