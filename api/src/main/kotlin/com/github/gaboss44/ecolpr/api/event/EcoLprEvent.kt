package com.github.gaboss44.ecolpr.api.event

import com.github.gaboss44.ecolpr.api.EcoLpr
import org.jetbrains.annotations.ApiStatus

@ApiStatus.NonExtendable
interface EcoLprEvent { val ecoLpr: EcoLpr }