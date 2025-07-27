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
        override val status: Transition.Result.Status,
        override val mode: Transition.Mode,
        override val attempt: Attempt?
    ) : AscensionDto, RankupDto.FromRank.Result, Ascension.Result {

        private constructor(
            attempt: Attempt,
            status: Transition.Result.Status
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
            status: Transition.Result.Status,
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
                Transition.Result.Status.SUCCESS,
                attempt.mode,
                attempt
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
                toRank: Rank,
                source: Transition.Source
            ) = Result(
                player,
                road,
                fromRank,
                toRank,
                source,
                Transition.Result.Status.SUCCESS,
                Transition.Mode.FORCE
            )
        }
    }

    interface Call : RankupDto.FromRank.Call, Ascension.Call {

        override val result: Result?

        override val status: Status

        override val proxy: Api

        interface Api : RankupDto.FromRank.Call.Api, Ascension.Call {

            override val handle: Call

            override val result get() = handle.result?.proxy

            override val status get() = handle.status
        }

        interface Status : RankupDto.FromRank.Call.Status, Ascension.Call.Status {

            enum class Values(
                val success: Boolean,
                val emptyRoad: Boolean,
                val absentFromRank: Boolean,
                val absentToRank: Boolean,
                val ambiguous: Boolean,
                val notOnRoad: Boolean,
                val lastRank: Boolean
            ) : Status {

                Success,

                EmptyRoad(
                    emptyRoad = true,
                    absentFromRank = true,
                    absentToRank = true,
                    ambiguous = false,
                    notOnRoad = true,
                    lastRank = false
                ),

                Ambiguous(
                    emptyRoad = false,
                    absentFromRank = true,
                    absentToRank = true,
                    ambiguous = true,
                    notOnRoad = false,
                    lastRank = true
                ),

                NotOnRoad(
                    emptyRoad = false,
                    absentFromRank = true,
                    absentToRank = false,
                    ambiguous = false,
                    notOnRoad = true,
                    lastRank = false
                ),

                LastRank(
                    emptyRoad = false,
                    absentFromRank = false,
                    absentToRank = true,
                    ambiguous = false,
                    notOnRoad = false,
                    lastRank = true
                );

                constructor() : this(
                    success = true,
                    emptyRoad = false,
                    absentFromRank = false,
                    absentToRank = false,
                    ambiguous = false,
                    notOnRoad = false,
                    lastRank = false
                )

                constructor(
                    emptyRoad: Boolean,
                    absentFromRank: Boolean,
                    absentToRank: Boolean,
                    ambiguous: Boolean,
                    notOnRoad: Boolean,
                    lastRank: Boolean
                ) : this(
                    false,
                    emptyRoad,
                    absentFromRank,
                    absentToRank,
                    ambiguous,
                    notOnRoad,
                    lastRank
                )

                override fun wasSuccessful() = success

                override fun isRoadEmpty() = emptyRoad

                override fun isFromRankAbsent() = absentFromRank

                override fun isToRankAbsent() = absentToRank

                override fun isAmbiguous() = ambiguous

                override fun isNotOnRoad() = notOnRoad

                override fun isLastRank() = lastRank
            }
        }

        companion object {

            fun success(result: Result): Call = Impl(result, Status.Values.Success)

            val EMPTY_ROAD: Call = Impl(Status.Values.EmptyRoad)

            val AMBIGUOUS: Call = Impl(Status.Values.Ambiguous)

            val NOT_ON_ROAD: Call = Impl(Status.Values.NotOnRoad)

            val LAST_RANK: Call = Impl(Status.Values.LastRank)
        }

        private class Impl(
            override val result: Result?,
            override val status: Status
        ) : Call {

            constructor(status: Status) : this(null, status)

            override val proxy = Api(this)

            class Api(
                override val handle: Call
            ) : Call.Api
        }
    }
}
