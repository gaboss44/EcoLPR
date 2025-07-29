package com.github.gaboss44.ecolpr.core.transition.dto.specific

import com.github.gaboss44.ecolpr.api.model.road.PrestigeType
import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.api.transition.specific.Migration
import com.github.gaboss44.ecolpr.core.model.rank.Rank
import com.github.gaboss44.ecolpr.core.model.road.Road
import com.github.gaboss44.ecolpr.core.transition.dto.generic.PrestigeDto
import org.bukkit.entity.Player

interface MigrationDto : PrestigeDto.WithTarget, Migration {

    override val prestigeRoad: Road

    override val proxy: Api

    interface Api : PrestigeDto.WithTarget.Api, Migration {
        override val handle: MigrationDto
    }

    class Attempt(
        override val player: Player,
        override val road: Road,
        override val prestigeRoad: Road,
        override val fromRank: Rank,
        override val toRank: Rank,
        override val prestigeLevel: Int,
        override val source: Transition.Source,
        override val mode: Transition.Mode,
    ) : MigrationDto, PrestigeDto.WithTarget.Attempt, Migration.Attempt {

        override val proxy: Api = Api(this)

        class Api(
            override val handle: Attempt
        ) : MigrationDto.Api, PrestigeDto.WithTarget.Attempt.Api, Migration.Attempt
    }

    class Result private constructor(
        override val player: Player,
        override val road: Road,
        override val fromRank: Rank,
        override val toRank: Rank,
        override val prestigeLevel: Int,
        override val prestigeRoad: Road,
        override val source: Transition.Source,
        override val status: Transition.Status,
        override val mode: Transition.Mode,
        override val attempt: Attempt?
    ) : MigrationDto, PrestigeDto.WithTarget.Result, Migration.Result {

        private constructor(attempt: Attempt, status: Transition.Status) : this(
            attempt.player,
            attempt.road,
            attempt.fromRank,
            attempt.toRank,
            attempt.prestigeLevel,
            attempt.prestigeRoad,
            attempt.source,
            status,
            attempt.mode,
            attempt
        )

        private constructor(
            player: Player,
            road: Road,
            prestigeRoad: Road,
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
            prestigeRoad,
            source,
            status,
            mode,
            null
        )

        override val proxy = Api(this)

        class Api(
            override val handle: Result
        ) : MigrationDto.Api, PrestigeDto.WithTarget.Result.Api, Migration.Result {
            override val attempt get() = handle.attempt?.proxy
        }

        companion object {

            fun success(attempt: Attempt) = Result(attempt, Transition.Status.SUCCESS)

            fun cancelled(attempt: Attempt) = Result(attempt, Transition.Status.CANCELLED)

            fun stale(attempt: Attempt) = Result(attempt, Transition.Status.STALE)

            fun forced(
                player: Player,
                road: Road,
                prestigeRoad: Road,
                fromRank: Rank,
                toRank: Rank,
                prestigeLevel: Int,
                source: Transition.Source
            ) = Result(
                player,
                road,
                prestigeRoad,
                fromRank,
                toRank,
                prestigeLevel,
                source,
                Transition.Status.SUCCESS,
                Transition.Mode.FORCE
            )
        }
    }

    interface Call : PrestigeDto.WithTarget.Call, Migration.Call {

        override val result: Result?

        override val prestigeType get() = PrestigeType.MIGRATION

        override val proxy: Api

        interface Api : PrestigeDto.WithTarget.Call.Api, Migration.Call {

            override val handle: Call

            override val result get() = handle.result?.proxy
        }

        companion object {

            fun success(result: Result) : Call = Impl(
                player = result.player,
                road = result.road,
                fromRank = result.fromRank,
                toRank = result.toRank,
                prestigeRoad = result.prestigeRoad,
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

            fun noPrestigeRoad(
                player: Player,
                road: Road,
                fromRank: Rank
            ) : Call = Impl(
                player = player,
                road = road,
                fromRank = fromRank,
                status = Transition.Call.Status.NO_PRESTIGE_ROAD
            )

            fun emptyPrestigeRoad(
                player: Player,
                road: Road,
                fromRank: Rank,
                prestigeRoad: Road
            ) : Call = Impl(
                player = player,
                road = road,
                fromRank = fromRank,
                prestigeRoad = prestigeRoad,
                status = Transition.Call.Status.EMPTY_PRESTIGE_ROAD
            )

            fun ambiguousPrestigeRank(
                player: Player,
                road: Road,
                fromRank: Rank,
                prestigeRoad: Road
            ) : Call = Impl(
                player = player,
                road = road,
                fromRank = fromRank,
                prestigeRoad = prestigeRoad,
                status = Transition.Call.Status.AMBIGUOUS_PRESTIGE_RANK
            )

            fun alreadyOnPrestigeRoad(
                player: Player,
                road: Road,
                fromRank: Rank,
                prestigeRoad: Road
            ) : Call = Impl(
                player = player,
                road = road,
                prestigeRoad = prestigeRoad,
                fromRank = fromRank,
                status = Transition.Call.Status.ALREADY_ON_PRESTIGE_ROAD
            )
        }

        private class Impl(
            override val player: Player,
            override val road: Road,
            override val fromRank: Rank? = null,
            override val toRank: Rank? = null,
            override val prestigeRoad: Road? = null,
            override val result: Result? = null,
            override val status: Transition.Call.Status
        ) : Call {

            override val proxy = Api(this)

            class Api(override val handle: Call) : Call.Api
        }
    }
}
