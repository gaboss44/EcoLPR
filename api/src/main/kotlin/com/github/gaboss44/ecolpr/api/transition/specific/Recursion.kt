package com.github.gaboss44.ecolpr.api.transition.specific

import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.api.transition.generic.Prestige
import org.jetbrains.annotations.ApiStatus

@ApiStatus.NonExtendable
interface Recursion : Prestige.ToRank {

    override val type get() = Transition.Type.RECURSION

    interface Attempt : Recursion, Prestige.ToRank.Attempt

    interface Result : Recursion, Prestige.ToRank.Result {

        override val attempt: Attempt?
    }

    interface Call : Prestige.ToRank.Call {

        override val result: Result?
    }
}