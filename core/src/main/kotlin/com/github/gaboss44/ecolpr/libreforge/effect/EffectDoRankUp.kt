package com.github.gaboss44.ecolpr.libreforge.effect

import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerParameter

object EffectDoRankUp : Effect<NoCompileData>("do_rank_up") {
    override val arguments = arguments {
        TriggerParameter.PLAYER
        TriggerParameter.LOCATION
        TriggerParameter.EVENT
    }
}