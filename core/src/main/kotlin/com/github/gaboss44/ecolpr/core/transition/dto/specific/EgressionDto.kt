package com.github.gaboss44.ecolpr.core.transition.dto.specific

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
        override val status: Transition.Result.Status,
        override val mode: Transition.Mode,
        override val attempt: Attempt?
    ) : PrestigeDto.Result, EgressionDto, Egression.Result {

        private constructor(
            attempt: Attempt,
            status: Transition.Result.Status
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
            status: Transition.Result.Status,
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
                Transition.Result.Status.SUCCESS
            )

            fun cancelled(attempt: Attempt) = Result(
                attempt,
                Transition.Result.Status.CANCELLED
            )

            fun stale(attempt: Attempt) = Result(
                attempt,
                Transition.Result.Status.STALE
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
                Transition.Result.Status.SUCCESS,
                Transition.Mode.FORCE
            )
        }
    }

    interface Call : PrestigeDto.Call, Egression.Call {

        override val result: Result?

        override val status: Status

        override val proxy: Api

        interface Api : PrestigeDto.Call.Api, Egression.Call {

            override val handle: Call

            override val result get() = handle.result?.proxy

            override val status get() = handle.status
        }

        interface Status : PrestigeDto.Call.Status, Egression.Call.Status {
            enum class Values(
                val success: Boolean,
                val emptyRoad: Boolean,
                val ambiguous: Boolean,
                val absentFromRank: Boolean,
                val notOnRoad: Boolean,
                val notLastRank: Boolean
            ) : Status {

                Success(
                    success = true,
                    emptyRoad = false,
                    ambiguous = false,
                    absentFromRank = false,
                    notOnRoad = false,
                    notLastRank = false
                ),

                EmptyRoad(
                    emptyRoad = true,
                    ambiguous = false,
                    absentFromRank = true,
                    notOnRoad = true,
                    notLastRank = true
                ),

                Ambiguous(
                    emptyRoad = false,
                    ambiguous = true,
                    absentFromRank = true,
                    notOnRoad = false,
                    notLastRank = true
                ),

                NotOnRoad(
                    emptyRoad = false,
                    ambiguous = false,
                    absentFromRank = true,
                    notOnRoad = true,
                    notLastRank = false
                ),

                NotLastRank(
                    emptyRoad = false,
                    ambiguous = false,
                    absentFromRank = false,
                    notOnRoad = false,
                    notLastRank = true
                );

                constructor(
                    emptyRoad: Boolean,
                    ambiguous: Boolean,
                    absentFromRank: Boolean,
                    notOnRoad: Boolean,
                    notLastRank: Boolean
                ) : this(
                    false,
                    emptyRoad,
                    ambiguous,
                    absentFromRank,
                    notOnRoad,
                    notLastRank
                )

                override fun wasSuccessful() = success

                override fun isRoadEmpty() = emptyRoad

                override fun isAmbiguous() = ambiguous

                override fun isFromRankAbsent() = absentFromRank

                override fun isNotOnRoad() = notOnRoad

                override fun isNotLastRank() = notLastRank
            }
        }

        companion object {

            fun success(result: Result): Call = Impl(result, Status.Values.Success)

            val EMPTY_ROAD: Call = Impl(Status.Values.EmptyRoad)

            val AMBIGUOUS: Call = Impl(Status.Values.Ambiguous)

            val NOT_ON_ROAD: Call = Impl(Status.Values.NotOnRoad)

            val NOT_LAST_RANK: Call = Impl(Status.Values.NotLastRank)
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
