package com.github.gaboss44.ecolpr.exception

import com.github.gaboss44.ecolpr.prestige.Prestige

class PrestigeWithoutLevelsException(val prestige: Prestige, message: String): RankTransitionException(message)