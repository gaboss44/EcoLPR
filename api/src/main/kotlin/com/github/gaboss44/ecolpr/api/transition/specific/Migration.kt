package com.github.gaboss44.ecolpr.api.transition.specific

import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.api.transition.generic.Prestige
import org.jetbrains.annotations.ApiStatus

@ApiStatus.NonExtendable
interface Migration : Prestige.WithTarget {

    override val type get() = Transition.Type.MIGRATION

    interface Attempt : Migration, Prestige.WithTarget.Attempt

    interface Result : Migration, Prestige.WithTarget.Result {

        override val attempt: Attempt?
    }

    interface Call : Prestige.WithTarget.Call {

        override val result: Result?
    }
}