package com.github.gaboss44.ecolpr.core.transition.dto.generic

import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.api.transition.generic.Rankup
import com.github.gaboss44.ecolpr.core.model.rank.Rank
import com.github.gaboss44.ecolpr.core.model.road.Road
import com.github.gaboss44.ecolpr.core.transition.dto.TransitionDto
import org.bukkit.entity.Player

interface RankupDto : TransitionDto.ToRank, Rankup {

    override val proxy: Api

    interface Api : TransitionDto.ToRank.Api, Rankup {

        override val handle: RankupDto
    }

    interface Attempt : RankupDto, TransitionDto.ToRank.Attempt, Rankup.Attempt {

        override val proxy: Api

        interface Api : RankupDto.Api, TransitionDto.ToRank.Attempt.Api, Rankup.Attempt {

            override val handle: Attempt
        }
    }

    interface Result : RankupDto, TransitionDto.ToRank.Result, Rankup.Result {

        override val attempt: Attempt?

        override val proxy: Api

        interface Api : RankupDto.Api, TransitionDto.ToRank.Result.Api, Rankup.Result {

            override val handle: Result

            override val attempt get() = handle.attempt?.proxy
        }
    }

    interface Call : TransitionDto.ToRank.Call, Rankup.Call {

        override val result: Result?
        override val proxy: Api

        interface Api : TransitionDto.ToRank.Call.Api, Rankup.Call {
            override val handle: Call
            override val result get() = handle.result?.proxy
        }

        companion object {

            fun success(
                player: Player,
                road: Road,
                result: Result
            ): Call = Impl(
                player = player,
                road = road,
                result = result,
                status = Transition.Call.Status.SUCCESS
            )

            fun emptyRoad(
                player: Player,
                road: Road
            ): Call = Impl(
                player = player,
                road = road,
                status = Transition.Call.Status.EMPTY_ROAD
            )

            fun ambiguousRank(
                player: Player,
                road: Road
            ): Call = Impl(
                player = player,
                road = road,
                status = Transition.Call.Status.AMBIGUOUS_RANK
            )
        }

        private class Impl(
            override val player: Player,
            override val road: Road,
            override val toRank: Rank? = null,
            override val result: Result? = null,
            override val status: Transition.Call.Status
        ) : Call {

            override val proxy = Api(this)

            class Api(override val handle: Call) : Call.Api
        }
    }

    interface FromRank : RankupDto, TransitionDto.FromRank, Rankup.FromRank {

        override val proxy: Api

        interface Api : RankupDto.Api, TransitionDto.FromRank.Api, Rankup.FromRank {

            override val handle: FromRank
        }

        interface Attempt : FromRank, RankupDto.Attempt, TransitionDto.FromRank.Attempt, Rankup.FromRank.Attempt {

            override val proxy: Api

            interface Api : FromRank.Api, RankupDto.Attempt.Api, TransitionDto.FromRank.Attempt.Api, Rankup.FromRank.Attempt {

                override val handle: Attempt
            }
        }

        interface Result : FromRank, RankupDto.Result, TransitionDto.FromRank.Result, Rankup.FromRank.Result {

            override val attempt: Attempt?

            override val proxy: Api

            interface Api: FromRank.Api, RankupDto.Result.Api, TransitionDto.FromRank.Result.Api, Rankup.FromRank.Result {

                override val handle: Result

                override val attempt get() = this.handle.attempt?.proxy
            }
        }

        interface Call : RankupDto.Call, TransitionDto.FromRank.Call, Rankup.FromRank.Call {

            override val result: Result?

            override val proxy: Api

            interface Api : RankupDto.Call.Api, TransitionDto.FromRank.Call.Api, Rankup.FromRank.Call {

                override val handle: Call

                override val result get() = this.handle.result?.proxy
            }
        }
    }
}
