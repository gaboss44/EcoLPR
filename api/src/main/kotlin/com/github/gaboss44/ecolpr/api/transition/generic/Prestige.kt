package com.github.gaboss44.ecolpr.api.transition.generic

import com.github.gaboss44.ecolpr.api.model.road.Road
import com.github.gaboss44.ecolpr.api.transition.Transition
import org.jetbrains.annotations.ApiStatus

@ApiStatus.NonExtendable
interface Prestige : Transition.FromRank {

    val prestigeLevel: Int

    interface Attempt : Prestige, Transition.FromRank.Attempt

    interface Result : Prestige, Transition.FromRank.Result

    interface Call : Transition.FromRank.Call {

        override val result: Result?

        override val status: Status

        interface Status : Transition.FromRank.Call.Status {

            fun isPrestigeTypeNotSpecified() : Boolean

            fun isNotOnRoad() : Boolean

            fun isNotLastRank() : Boolean
        }
    }

    interface ToRank : Prestige, Transition.ToRank {

        interface Attempt : ToRank, Prestige.Attempt, Transition.ToRank.Attempt

        interface Result : ToRank, Prestige.Result, Transition.ToRank.Result {

            override val attempt: Attempt?
        }

        interface Call : Prestige.Call, Transition.ToRank.Call {

            override val result: Result?

            override val status: Status

            interface Status : Prestige.Call.Status, Transition.ToRank.Call.Status
        }
    }

    interface WithTarget : ToRank {

        val prestigeRoad: Road

        interface Attempt : ToRank.Attempt

        interface Result : ToRank.Result {

            override val attempt: Attempt?
        }

        interface Call : ToRank.Call {

            override val result: Result?

            override val status: Status

            interface Status : ToRank.Call.Status {

                fun isPrestigeRoadAbsent() : Boolean

                fun isPrestigeRoadEmpty() : Boolean
            }
        }
    }
}
