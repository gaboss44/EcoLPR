package com.github.gaboss44.ecolpr.exception

import org.bukkit.entity.Player

class NotInAnyRoadException(val player: Player, message: String) : RankTransitionException(message)