package com.github.gaboss44.ecolpr.core.transition.dto.specific

import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.api.transition.specific.Ingression
import com.github.gaboss44.ecolpr.core.model.rank.Rank
import com.github.gaboss44.ecolpr.core.model.road.Road
import com.github.gaboss44.ecolpr.core.transition.dto.generic.RankupDto
import org.bukkit.entity.Player

interface IngressionDto : RankupDto, Ingression {

    override val proxy: Api

    interface Api : RankupDto.Api, Ingression {
        override val handle: IngressionDto
    }

    class Attempt(
        override val player: Player,
        override val road: Road,
        override val toRank: Rank,
        override val source: Transition.Source,
        override val mode: Transition.Mode
    ) : IngressionDto, RankupDto.Attempt, Ingression.Attempt {

        override val proxy = Api(this)

        class Api(
            override val handle: Attempt
        ) : RankupDto.Attempt.Api, IngressionDto.Api, Ingression.Attempt
    }

    class Result private constructor(
        override val player: Player,
        override val road: Road,
        override val toRank: Rank,
        override val source: Transition.Source,
        override val status: Transition.Status,
        override val mode: Transition.Mode,
        override val attempt: Attempt?
    ) : RankupDto.Result, IngressionDto, Ingression.Result {

        private constructor(
            attempt: Attempt,
            status: Transition.Status
        ) : this(
            attempt.player,
            attempt.road,
            attempt.toRank,
            attempt.source,
            status,
            attempt.mode,
            attempt
        )

        private constructor(
            player: Player,
            road: Road,
            toRank: Rank,
            source: Transition.Source,
            status: Transition.Status,
            mode: Transition.Mode
        ) : this(
            player,
            road,
            toRank,
            source,
            status,
            mode,
            null
        )

        override val proxy = Api(this)

        class Api(
            override val handle: Result
        ) : RankupDto.Result.Api, IngressionDto.Api, Ingression.Result {
            override val attempt get() = handle.attempt?.proxy
        }

        companion object {

            fun success(attempt: Attempt) = Result(attempt, Transition.Status.SUCCESS,)

            fun cancelled(attempt: Attempt) = Result(attempt, Transition.Status.CANCELLED)

            fun stale(attempt: Attempt) = Result(attempt, Transition.Status.STALE)

            fun forced(
                player: Player,
                road: Road,
                toRank: Rank,
                source: Transition.Source
            ) = Result(
                player,
                road,
                toRank,
                source,
                Transition.Status.SUCCESS,
                Transition.Mode.FORCE
            )
        }
    }

    interface Call : RankupDto.Call, Ingression.Call {

        override val result: Result?

        override val proxy: Api

        interface Api : RankupDto.Call.Api, Ingression.Call {

            override val handle: Call

            override val result get() = this.handle.result?.proxy
        }

        companion object {

            fun success(result: Result): Call = Impl(
                player = result.player,
                road = result.road,
                toRank = result.toRank,
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

            fun alreadyOnRoad(
                player: Player,
                road: Road
            ): Call = Impl(
                player = player,
                road = road,
                status = Transition.Call.Status.ALREADY_ON_ROAD
            )
        }

        private class Impl(
            override val player: Player,
            override val road: Road,
            override val toRank: Rank? = null,
            override val result: Result? = null,
            override val status: Transition.Call.Status,
        ) : Call {

            class Api(override val handle: Call) : Call.Api

            override val proxy = Api(this)
        }
    }
}
