package com.github.gaboss44.ecolpr.api.transition.specific

import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.api.transition.generic.Prestige
import org.jetbrains.annotations.ApiStatus

@ApiStatus.NonExtendable
interface Egression : Prestige {

    override val type get() = Transition.Type.EGRESSION

    interface Attempt : Egression, Prestige.Attempt

    interface Result : Egression, Prestige.Result {

        override val attempt: Attempt?
    }

    interface Call : Prestige.Call {

        override val result: Result?
    }
}
