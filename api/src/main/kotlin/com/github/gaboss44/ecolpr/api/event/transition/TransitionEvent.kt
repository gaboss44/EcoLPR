package com.github.gaboss44.ecolpr.api.event.transition

import com.github.gaboss44.ecolpr.api.event.EcoLprEvent
import com.github.gaboss44.ecolpr.api.transition.Transition

interface TransitionEvent : EcoLprEvent { val transition: Transition }