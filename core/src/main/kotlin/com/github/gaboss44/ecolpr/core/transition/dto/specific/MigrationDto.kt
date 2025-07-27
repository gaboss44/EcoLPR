package com.github.gaboss44.ecolpr.core.transition.dto.specific

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
        override val prestigeRoad: Road,
        override val fromRank: Rank,
        override val toRank: Rank,
        override val prestigeLevel: Int,
        override val source: Transition.Source,
        override val status: Transition.Result.Status,
        override val mode: Transition.Mode,
        override val attempt: Attempt?
    ) : MigrationDto, PrestigeDto.ToRank.Result, Migration.Result {

        private constructor(
            attempt: Attempt,
            status: Transition.Result.Status
        ) : this(
            attempt.player,
            attempt.road,
            attempt.prestigeRoad,
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
            prestigeRoad: Road,
            fromRank: Rank,
            toRank: Rank,
            prestigeLevel: Int,
            source: Transition.Source,
            status: Transition.Result.Status,
            mode: Transition.Mode
        ) : this(
            player,
            road,
            prestigeRoad,
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
        ) : MigrationDto.Api, PrestigeDto.ToRank.Result.Api, Migration.Result {
            override val attempt get() = handle.attempt?.proxy
        }

        companion object {

            fun success(attempt: Attempt) = Result(attempt, Transition.Result.Status.SUCCESS)

            fun cancelled(attempt: Attempt) = Result(attempt, Transition.Result.Status.CANCELLED)

            fun stale(attempt: Attempt) = Result(attempt, Transition.Result.Status.STALE)

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
                Transition.Result.Status.SUCCESS,
                Transition.Mode.FORCE
            )
        }
    }

    interface Call : PrestigeDto.Call, Migration.Call {

        override val result: Result?

        override val status: Status

        override val proxy: Api

        interface Api : PrestigeDto.Call.Api, Migration.Call {
            override val handle: Call

            override val result get() = handle.result?.proxy

            override val status get() = handle.status
        }

        interface Status : PrestigeDto.Call.Status, Migration.Call.Status {
            enum class Values(
                val success: Boolean,
                val emptyRoad: Boolean,
                val ambiguous: Boolean,
                val absentFromRank: Boolean,
                val absentToRank: Boolean,
                val notOnRoad: Boolean,
                val notLastRank: Boolean,
                val absentPrestigeRoad: Boolean,
                val emptyPrestigeRoad: Boolean,
                val alreadyOnPrestigeRoad: Boolean
            ) : Status {

                Success(
                    success = true,
                    emptyRoad = false,
                    ambiguous = false,
                    absentFromRank = false,
                    absentToRank = false,
                    notOnRoad = false,
                    notLastRank = false,
                    absentPrestigeRoad = false,
                    emptyPrestigeRoad = false,
                    alreadyOnPrestigeRoad = false
                ),

                EmptyRoad(
                    emptyRoad = true,
                    ambiguous = false,
                    absentFromRank = true,
                    absentToRank = true,
                    notOnRoad = true,
                    notLastRank = true,
                    absentPrestigeRoad = false,
                    emptyPrestigeRoad = false,
                    alreadyOnPrestigeRoad = false
                ),

                Ambiguous(
                    emptyRoad = false,
                    ambiguous = true,
                    absentFromRank = true,
                    absentToRank = true,
                    notOnRoad = false,
                    notLastRank = true,
                    absentPrestigeRoad = false,
                    emptyPrestigeRoad = false,
                    alreadyOnPrestigeRoad = false
                ),

                NotOnRoad(
                    emptyRoad = false,
                    ambiguous = false,
                    absentFromRank = true,
                    absentToRank = true,
                    notOnRoad = true,
                    notLastRank = false,
                    absentPrestigeRoad = false,
                    emptyPrestigeRoad = false,
                    alreadyOnPrestigeRoad = false
                ),

                NotLastRank(
                    emptyRoad = false,
                    ambiguous = false,
                    absentFromRank = false,
                    absentToRank = false,
                    notOnRoad = false,
                    notLastRank = true,
                    absentPrestigeRoad = false,
                    emptyPrestigeRoad = false,
                    alreadyOnPrestigeRoad = false
                ),

                AbsentPrestigeRoad(
                    emptyRoad = false,
                    ambiguous = false,
                    absentFromRank = false,
                    absentToRank = false,
                    notOnRoad = false,
                    notLastRank = false,
                    absentPrestigeRoad = true,
                    emptyPrestigeRoad = false,
                    alreadyOnPrestigeRoad = false
                ),

                EmptyPrestigeRoad(
                    emptyRoad = false,
                    ambiguous = false,
                    absentFromRank = false,
                    absentToRank = false,
                    notOnRoad = false,
                    notLastRank = false,
                    absentPrestigeRoad = false,
                    emptyPrestigeRoad = true,
                    alreadyOnPrestigeRoad = false
                ),

                AlreadyOnPrestigeRoad(
                    emptyRoad = false,
                    ambiguous = false,
                    absentFromRank = false,
                    absentToRank = false,
                    notOnRoad = false,
                    notLastRank = false,
                    absentPrestigeRoad = false,
                    emptyPrestigeRoad = false,
                    alreadyOnPrestigeRoad = true
                );

                constructor(
                    emptyRoad: Boolean,
                    ambiguous: Boolean,
                    absentFromRank: Boolean,
                    absentToRank: Boolean,
                    notOnRoad: Boolean,
                    notLastRank: Boolean,
                    absentPrestigeRoad: Boolean,
                    emptyPrestigeRoad: Boolean,
                    alreadyOnPrestigeRoad: Boolean
                ) : this(
                    success = false,
                    emptyRoad = emptyRoad,
                    ambiguous = ambiguous,
                    absentFromRank = absentFromRank,
                    absentToRank = absentToRank,
                    notOnRoad = notOnRoad,
                    notLastRank = notLastRank,
                    absentPrestigeRoad = absentPrestigeRoad,
                    emptyPrestigeRoad = emptyPrestigeRoad,
                    alreadyOnPrestigeRoad = alreadyOnPrestigeRoad
                )

                override fun wasSuccessful() = success

                override fun isRoadEmpty() = emptyRoad

                override fun isAmbiguous() = ambiguous

                override fun isFromRankAbsent() = absentFromRank

                override fun isToRankAbsent() = absentToRank

                override fun isNotOnRoad() = notOnRoad

                override fun isNotLastRank() = notLastRank

                override fun isPrestigeRoadAbsent() = absentPrestigeRoad

                override fun isPrestigeRoadEmpty() = emptyPrestigeRoad

                override fun isAlreadyOnPrestigeRoad() = alreadyOnPrestigeRoad
            }
        }

        companion object {

            fun success(result: Result): Call = Impl(result, Status.Values.Success)

            val EMPTY_ROAD: Call = Impl(Status.Values.EmptyRoad)

            val AMBIGUOUS: Call = Impl(Status.Values.Ambiguous)

            val NOT_ON_ROAD: Call = Impl(Status.Values.NotOnRoad)

            val NOT_LAST_RANK: Call = Impl(Status.Values.NotLastRank)

            val ABSENT_PRESTIGE_ROAD: Call = Impl(Status.Values.AbsentPrestigeRoad)

            val EMPTY_PRESTIGE_ROAD: Call = Impl(Status.Values.EmptyPrestigeRoad)

            val ALREADY_ON_PRESTIGE_ROAD: Call = Impl(Status.Values.AlreadyOnPrestigeRoad)
        }

        private class Impl(
            override val result: Result?,
            override val status: Status
        ) : Call {

            constructor(status: Status) : this(null, status)

            override val proxy = Api(this)

            class Api(override val handle: Call) : Call.Api
        }
    }
}
