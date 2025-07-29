package com.github.gaboss44.ecolpr.core.transition.dto.specific

import com.github.gaboss44.ecolpr.api.model.road.PrestigeType
import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.api.transition.specific.Egression
import com.github.gaboss44.ecolpr.core.model.rank.Rank
import com.github.gaboss44.ecolpr.core.model.road.Road
import com.github.gaboss44.ecolpr.core.transition.dto.generic.PrestigeDto
import org.bukkit.entity.Player

interface EgressionDto : PrestigeDto, Egression {

    override val proxy: Api

    interface Api : PrestigeDto.Api, Egression {
        override val handle: EgressionDto
    }

    class Attempt(
        override val player: Player,
        override val road: Road,
        override val fromRank: Rank,
        override val prestigeLevel: Int,
        override val source: Transition.Source,
        override val mode: Transition.Mode,
    ) : EgressionDto, PrestigeDto.Attempt, Egression.Attempt {

        override val proxy = Api(this)

        class Api(
            override val handle: Attempt
        ) : PrestigeDto.Attempt.Api, EgressionDto.Api, Egression.Attempt
    }

    class Result private constructor(
        override val player: Player,
        override val road: Road,
        override val fromRank: Rank,
        override val prestigeLevel: Int,
        override val source: Transition.Source,
        override val status: Transition.Status,
        override val mode: Transition.Mode,
        override val attempt: Attempt?
    ) : PrestigeDto.Result, EgressionDto, Egression.Result {

        private constructor(
            attempt: Attempt,
            status: Transition.Status
        ) : this(
            attempt.player,
            attempt.road,
            attempt.fromRank,
            attempt.prestigeLevel,
            attempt.source,
            status,
            attempt.mode,
            attempt
        )

        private constructor(
            player: Player,
            road: Road,
            fromRank: Rank,
            prestigeLevel: Int,
            source: Transition.Source,
            status: Transition.Status,
            mode: Transition.Mode
        ) : this(
            player,
            road,
            fromRank,
            prestigeLevel,
            source,
            status,
            mode,
            null,
        )

        override val proxy = Api(this)

        class Api(
            override val handle: Result
        ) : PrestigeDto.Result.Api, EgressionDto.Api, Egression.Result {
            override val attempt get() = handle.attempt?.proxy
        }

        companion object {

            fun success(attempt: Attempt) = Result(
                attempt,
                Transition.Status.SUCCESS
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
                prestigeLevel: Int,
                source: Transition.Source
            ) = Result(
                player,
                road,
                fromRank,
                prestigeLevel,
                source,
                Transition.Status.SUCCESS,
                Transition.Mode.FORCE
            )
        }
    }

    interface Call : PrestigeDto.Call, Egression.Call {

        override val result: Result?

        override val prestigeType get() = PrestigeType.EGRESSION

        override val proxy: Api

        interface Api : PrestigeDto.Call.Api, Egression.Call {

            override val handle: Call

            override val result get() = handle.result?.proxy
        }

        companion object {

            fun success(result: Result): Call = Impl(
                player = result.player,
                road = result.road,
                fromRank = result.fromRank,
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

            fun notLastRank(
                player: Player,
                road: Road,
                fromRank: Rank
            ): Call = Impl(
                player = player,
                road = road,
                fromRank = fromRank,
                status = Transition.Call.Status.NOT_LAST_RANK
            )
        }

        private class Impl(
            override val player: Player,
            override val road: Road,
            override val fromRank: Rank? = null,
            override val result: Result? = null,
            override val status: Transition.Call.Status,
        ) : Call {

            class Api(override val handle: Call) : Call.Api

            override val proxy = Api(this)
        }
    }
}
