package com.github.gaboss44.ecolpr.api.transition.generic

import com.github.gaboss44.ecolpr.api.model.road.PrestigeType
import com.github.gaboss44.ecolpr.api.model.road.Road
import com.github.gaboss44.ecolpr.api.transition.Transition
import org.jetbrains.annotations.ApiStatus

@ApiStatus.NonExtendable
interface Prestige : Transition.FromRank {

    val prestigeLevel: Int

    interface Attempt : Prestige, Transition.FromRank.Attempt

    interface Result : Prestige, Transition.FromRank.Result

    interface Call : Transition.FromRank.Call {

        val prestigeType: PrestigeType?

        override val result: Result?
    }

    interface ToRank : Prestige, Transition.ToRank {

        interface Attempt : ToRank, Prestige.Attempt, Transition.ToRank.Attempt

        interface Result : ToRank, Prestige.Result, Transition.ToRank.Result {

            override val attempt: Attempt?
        }

        interface Call : Prestige.Call, Transition.ToRank.Call {

            override val result: Result?
        }
    }

    interface WithTarget : ToRank {

        val prestigeRoad: Road

        interface Attempt : ToRank.Attempt

        interface Result : ToRank.Result {

            override val attempt: Attempt?
        }

        interface Call : ToRank.Call {

            val prestigeRoad: Road?

            override val result: Result?
        }
    }
}
