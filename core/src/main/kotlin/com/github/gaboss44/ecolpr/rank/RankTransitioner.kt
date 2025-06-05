package com.github.gaboss44.ecolpr.rank

import com.github.gaboss44.ecolpr.EcoLprPlugin
import com.github.gaboss44.ecolpr.exception.LastRankInRoadException
import com.github.gaboss44.ecolpr.exception.NoRanksInRoadException
import com.github.gaboss44.ecolpr.exception.NotInRoadException
import org.bukkit.entity.Player

class RankTransitioner(private val plugin: EcoLprPlugin) {
    fun transit(player: Player, road: Road, direction: RankTransition.Direction, source: RankTransition.Source) {
        val ranks = road.ranks.also {
            if (it.isEmpty()) throw NoRanksInRoadException(
                road,
                "Cannot transition ranks for player ${player.name} in road ${road.id} because no ranks were found."
            )
        }

        val currentRank = ranks.firstOrNull { it.isHeldBy(player) }

        val targetRank = when {
            currentRank == null -> when (direction) {
                RankTransition.Direction.UP -> ranks.firstOrNull()
                RankTransition.Direction.DOWN -> throw NotInRoadException(
                    road,
                    "Cannot transition downwards for player ${player.name} in road ${road.id} because they are not in any rank."
                )
                RankTransition.Direction.LOOP -> throw NotInRoadException(
                    road,
                    "Cannot transition in a loop for player ${player.name} in road ${road.id} because they are not in any rank."
                )
            }
            else -> when (direction) {
                RankTransition.Direction.UP -> ranks.getNext(currentRank) ?: throw LastRankInRoadException(
                    road,
                    currentRank,
                    "Cannot transition upwards for player ${player.name} in road ${road.id} because they are already at the last rank."
                )
                RankTransition.Direction.DOWN -> ranks.getPrevious(currentRank)
                RankTransition.Direction.LOOP -> null
            }
        }

        // Determinar el flujo basado en los rangos
        val flow = when {
            // Si no tiene rango actual pero hay un target, está entrando
            currentRank == null && targetRank != null -> RankTransition.Flow.IN
            // Si tiene rango actual pero no hay target, está saliendo
            currentRank != null && targetRank == null -> RankTransition.Flow.OUT
            // Si tiene ambos rangos, está transitando dentro
            currentRank != null && targetRank != null -> RankTransition.Flow.WITHIN
            // Si no tiene ninguno de los dos, es un estado inválido
            else -> throw IllegalStateException("Invalid state: both currentRank and targetRank are null")
        }

    }
}