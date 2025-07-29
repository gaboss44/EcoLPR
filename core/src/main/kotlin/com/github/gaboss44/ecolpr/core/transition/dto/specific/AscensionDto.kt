package com.github.gaboss44.ecolpr.core.transition.dto.specific

import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.api.transition.specific.Ascension
import com.github.gaboss44.ecolpr.core.model.rank.Rank
import com.github.gaboss44.ecolpr.core.model.road.Road
import com.github.gaboss44.ecolpr.core.transition.dto.generic.RankupDto
import org.bukkit.entity.Player

interface AscensionDto : RankupDto.FromRank, Ascension {

    override val proxy: Api

    interface Api : RankupDto.FromRank.Api, Ascension {
        override val handle: AscensionDto
    }

    class Attempt(
        override val player: Player,
        override val road: Road,
        override val fromRank: Rank,
        override val toRank: Rank,
        override val source: Transition.Source,
        override val mode: Transition.Mode
    ) : AscensionDto, RankupDto.FromRank.Attempt, Ascension.Attempt {

        override val proxy = Api(this)

        class Api(
            override val handle: Attempt
        ) : AscensionDto.Api, RankupDto.FromRank.Attempt.Api, Ascension.Attempt
    }

    class Result private constructor(
        override val player: Player,
        override val road: Road,
        override val fromRank: Rank,
        override val toRank: Rank,
        override val source: Transition.Source,
        override val status: Transition.Status,
        override val mode: Transition.Mode,
        override val attempt: Attempt?
    ) : AscensionDto, RankupDto.FromRank.Result, Ascension.Result {

        private constructor(
            attempt: Attempt,
            status: Transition.Status
        ) : this(
            attempt.player,
            attempt.road,
            attempt.fromRank,
            attempt.toRank,
            attempt.source,
            status,
            attempt.mode,
            attempt
        )

        private constructor(
            player: Player,
            road: Road,
            fromRank: Rank,
            toRank: Rank,
            source: Transition.Source,
            status: Transition.Status,
            mode: Transition.Mode
        ) : this(
            player,
            road,
            fromRank,
            toRank,
            source,
            status,
            mode,
            null
        )

        override val proxy = Api(this)

        class Api(
            override val handle: Result
        ) : AscensionDto.Api, RankupDto.FromRank.Result.Api, Ascension.Result {
            override val attempt get() = handle.attempt?.proxy
        }

        companion object {

            fun success(
                attempt: Attempt
            ) = Result(
                attempt.player,
                attempt.road,
                attempt.fromRank,
                attempt.toRank,
                attempt.source,
                Transition.Status.SUCCESS,
                attempt.mode,
                attempt
            )

            fun cancelled(attempt: Attempt) = Result(
                attempt,
                Transition.Status.CANCELLED
            )

            fun stale(attempt: Attempt) = Result(
                attempt,
                Transition.Status.STALE
            )

            fun forced(
                player: Player,
                road: Road,
                fromRank: Rank,
                toRank: Rank,
                source: Transition.Source
            ) = Result(
                player,
                road,
                fromRank,
                toRank,
                source,
                Transition.Status.SUCCESS,
                Transition.Mode.FORCE
            )
        }
    }

    interface Call : RankupDto.FromRank.Call, Ascension.Call {

        override val result: Result?

        override val proxy: Api

        interface Api : RankupDto.FromRank.Call.Api, Ascension.Call {

            override val handle: Call

            override val result get() = handle.result?.proxy
        }

        companion object {

            fun success(result: Result): Call = Impl(
                player = result.player,
                road = result.road,
                fromRank = result.fromRank,
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

            fun notOnRoad(
                player: Player,
                road: Road
            ): Call = Impl(
                player = player,
                road = road,
                status = Transition.Call.Status.NOT_ON_ROAD
            )

            fun lastRank(
                player: Player,
                road: Road,
                fromRank: Rank
            ): Call = Impl(
                player = player,
                road = road,
                fromRank = fromRank,
                status = Transition.Call.Status.LAST_RANK
            )
        }

        private class Impl(
            override val player: Player,
            override val road: Road,
            override val fromRank: Rank? = null,
            override val toRank: Rank? = null,
            override val result: Result? = null,
            override val status: Transition.Call.Status
        ) : Call {

            override val proxy = Api(this)

            class Api(override val handle: Call) : Call.Api
        }
    }
}
