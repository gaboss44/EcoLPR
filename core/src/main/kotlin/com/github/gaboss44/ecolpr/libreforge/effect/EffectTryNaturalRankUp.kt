package com.github.gaboss44.ecolpr.libreforge.effect

import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerParameter

object EffectTryNaturalRankUp : Effect<NoCompileData>("try_rank_up") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.VALUE,
        TriggerParameter.TEXT,
        TriggerParameter.EVENT
    )

    override val arguments = arguments {

    }
}