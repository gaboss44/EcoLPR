package com.github.gaboss44.ecolpr.core.transition.dto.specific

import com.github.gaboss44.ecolpr.api.model.road.PrestigeType
import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.api.transition.specific.Recursion
import com.github.gaboss44.ecolpr.core.model.rank.Rank
import com.github.gaboss44.ecolpr.core.model.road.Road
import com.github.gaboss44.ecolpr.core.transition.dto.generic.PrestigeDto
import org.bukkit.entity.Player

interface RecursionDto : PrestigeDto.ToRank, Recursion {
    override val proxy: Api

    interface Api : PrestigeDto.ToRank.Api, Recursion {
        override val handle: RecursionDto
    }

    class Attempt(
        override val player: Player,
        override val road: Road,
        override val fromRank: Rank,
        override val toRank: Rank,
        override val prestigeLevel: Int,
        override val source: Transition.Source,
        override val mode: Transition.Mode,
    ) : RecursionDto, PrestigeDto.ToRank.Attempt, Recursion.Attempt {
        override val proxy = Api(this)

        class Api(
            override val handle: Attempt
        ) : RecursionDto.Api, PrestigeDto.ToRank.Attempt.Api, Recursion.Attempt
    }

    class Result private constructor(
        override val player: Player,
        override val road: Road,
        override val fromRank: Rank,
        override val toRank: Rank,
        override val prestigeLevel: Int,
        override val source: Transition.Source,
        override val status: Transition.Status,
        override val mode: Transition.Mode,
        override val attempt: Attempt?
    ) : RecursionDto, PrestigeDto.ToRank.Result, Recursion.Result {

        private constructor(
            attempt: Attempt,
            status: Transition.Status
        ) : this(
            attempt.player,
            attempt.road,
            attempt.fromRank,
            attempt.toRank,
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
            toRank: Rank,
            prestigeLevel: Int,
            source: Transition.Source,
            status: Transition.Status,
            mode: Transition.Mode
        ) : this(
            player,
            road,
            fromRank,
            toRank,
            prestigeLevel,
            source,
            status,
            mode,
            null
        )

        override val proxy = Api(this)

        class Api(
            override val handle: Result
        ) : RecursionDto.Api, PrestigeDto.ToRank.Result.Api, Recursion.Result {
            override val attempt get() = handle.attempt?.proxy
        }

        companion object {

            fun success(attempt: Attempt) = Result(attempt, Transition.Status.SUCCESS)

            fun cancelled(attempt: Attempt) = Result(attempt, Transition.Status.CANCELLED)

            fun stale(attempt: Attempt) = Result(attempt, Transition.Status.STALE)

            fun forced(
                player: Player,
                road: Road,
                fromRank: Rank,
                toRank: Rank,
                prestigeLevel: Int,
                source: Transition.Source
            ) = Result(
                player,
                road,
                fromRank,
                toRank,
                prestigeLevel,
                source,
                Transition.Status.SUCCESS,
                Transition.Mode.FORCE
            )
        }
    }

    interface Call : PrestigeDto.ToRank.Call, Recursion.Call {

        override val result: Result?

        override val prestigeType get() = PrestigeType.RECURSION

        override val proxy: Api

        interface Api : PrestigeDto.ToRank.Call.Api, Recursion.Call {

            override val handle: Call

            override val result get() = handle.result?.proxy
        }

        companion object {

            fun success(result: Result) : Call = Impl(
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
            ) : Call = Impl(
                player = player,
                road = road,
                status = Transition.Call.Status.EMPTY_ROAD
            )

            fun ambiguousRank(
                player: Player,
                road: Road
            ) : Call = Impl(
                player = player,
                road = road,
                status = Transition.Call.Status.AMBIGUOUS_RANK
            )

            fun notOnRoad(
                player: Player,
                road: Road
            ) : Call = Impl(
                player = player,
                road = road,
                status = Transition.Call.Status.NOT_ON_ROAD
            )

            fun notLastRank(
                player: Player,
                road: Road,
                fromRank: Rank
            ) : Call = Impl(
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
            override val toRank: Rank? = null,
            override val result: Result? = null,
            override val status: Transition.Call.Status
        ) : Call {

            override val proxy = Api(this)

            class Api(override val handle: Call) : Call.Api
        }
    }
}
