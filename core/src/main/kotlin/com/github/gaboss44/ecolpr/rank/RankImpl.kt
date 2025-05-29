package com.github.gaboss44.ecolpr.rank

import com.github.gaboss44.ecolpr.EcoLprPlugin
import com.willfp.eco.core.config.interfaces.Config

class RankImpl(
    final override val id: String,
    config : Config,
    plugin: EcoLprPlugin
) : Rank {
    override val idOverride: String = config.getString("id-override").takeUnless { it.isEmpty() } ?: id
    override val displayName: String? = config.getString("display-name")
}