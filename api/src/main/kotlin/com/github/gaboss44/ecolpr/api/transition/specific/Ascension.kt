package com.github.gaboss44.ecolpr.api.transition.specific

import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.api.transition.generic.Rankup
import org.jetbrains.annotations.ApiStatus

@ApiStatus.NonExtendable
interface Ascension : Rankup.FromRank {

    override val type get() = Transition.Type.ASCENSION

    interface Attempt : Ascension, Rankup.FromRank.Attempt

    interface Result : Ascension, Rankup.FromRank.Result {

        override val attempt: Attempt?
    }

    interface Call : Rankup.FromRank.Call {

        override val result: Result?

        override val status: Status

        interface Status : Rankup.FromRank.Call.Status {

            fun isNotOnRoad() : Boolean

            fun isLastRank() : Boolean
        }
    }
}
