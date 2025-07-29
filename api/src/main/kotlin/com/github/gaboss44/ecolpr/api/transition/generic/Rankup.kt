package com.github.gaboss44.ecolpr.api.transition.generic

import com.github.gaboss44.ecolpr.api.transition.Transition

interface Rankup : Transition.ToRank {

    interface Attempt : Rankup, Transition.ToRank.Attempt

    interface Result : Rankup, Transition.ToRank.Result

    interface Call : Transition.ToRank.Call {

        override val result: Result?
    }

    interface FromRank : Rankup, Transition.FromRank {

        interface Attempt : FromRank, Rankup.Attempt, Transition.FromRank.Attempt

        interface Result : FromRank, Rankup.Result, Transition.FromRank.Result {

            override val attempt: Attempt?
        }

        interface Call : Rankup.Call, Transition.FromRank.Call {

            override val result: Result?
        }
    }
}
