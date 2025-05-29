package com.github.gaboss44.ecolpr.rank

import com.willfp.eco.core.registry.KRegistrable

interface Rank : KRegistrable {
    val idOverride: String
    val displayName: String?
}