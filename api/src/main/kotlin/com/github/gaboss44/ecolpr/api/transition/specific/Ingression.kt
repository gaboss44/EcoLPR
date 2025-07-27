package com.github.gaboss44.ecolpr.api.transition.specific

import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.api.transition.generic.Rankup
import org.jetbrains.annotations.ApiStatus

@ApiStatus.NonExtendable
interface Ingression : Rankup {

    override val type get() = Transition.Type.INGRESSION

    interface Attempt : Ingression, Rankup.Attempt

    interface Result : Ingression, Rankup.Result {

        override val attempt: Attempt?
    }

    interface Call : Rankup.Call {

        override val result: Result?

        override val status: Status

        interface Status : Rankup.Call.Status {

            fun isAlreadyOnRoad() : Boolean
        }
    }
}
