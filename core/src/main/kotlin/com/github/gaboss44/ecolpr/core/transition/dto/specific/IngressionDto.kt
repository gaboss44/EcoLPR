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
        override val status: Transition.Result.Status,
        override val mode: Transition.Mode,
        override val attempt: Attempt?
    ) : RankupDto.Result, IngressionDto, Ingression.Result {

        private constructor(
            attempt: Attempt,
            status: Transition.Result.Status
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
            status: Transition.Result.Status,
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

            fun success(
                attempt: Attempt
            ) = Result(
                attempt.player,
                attempt.road,
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
                toRank: Rank,
                source: Transition.Source
            ) = Result(
                player,
                road,
                toRank,
                source,
                Transition.Result.Status.SUCCESS,
                Transition.Mode.FORCE
            )
        }
    }

    interface Call : RankupDto.Call, Ingression.Call {

        override val result: Result?

        override val status: Status

        override val proxy: Api

        interface Api : RankupDto.Call.Api, Ingression.Call {

            override val handle: Call

            override val result get() = this.handle.result?.proxy

            override val status get() = this.handle.status
        }

        interface Status : RankupDto.Call.Status, Ingression.Call.Status {

            enum class Values(
                val success: Boolean,
                val emptyRoad: Boolean,
                val absentToRank: Boolean,
                val ambiguous: Boolean,
                val alreadyOnRoad: Boolean
            ) : Status {

                Success,

                EmptyRoad(
                    emptyRoad = true,
                    absentToRank = true,
                    ambiguous = false,
                    alreadyOnRoad = false
                ),

                Ambiguous(
                    emptyRoad = false,
                    absentToRank = true,
                    ambiguous = true,
                    alreadyOnRoad = true
                ),

                AlreadyOnRoad(
                    emptyRoad = false,
                    absentToRank = false,
                    ambiguous = false,
                    alreadyOnRoad = true
                );

                constructor() : this(
                    true,
                    false,
                    false,
                    false,
                    false
                )

                constructor(
                    emptyRoad: Boolean,
                    absentToRank: Boolean,
                    ambiguous: Boolean,
                    alreadyOnRoad: Boolean
                ) : this(
                    false,
                    emptyRoad,
                    absentToRank,
                    ambiguous,
                    alreadyOnRoad
                )

                override fun wasSuccessful() = success

                override fun isRoadEmpty() = emptyRoad

                override fun isToRankAbsent() = absentToRank

                override fun isAmbiguous() = ambiguous

                override fun isAlreadyOnRoad() = alreadyOnRoad
            }
        }

        companion object {

            fun success(result: Result): Call = Impl(result, Status.Values.Success)

            val EMPTY_ROAD: Call = Impl(Status.Values.EmptyRoad)

            val AMBIGUOUS: Call = Impl(Status.Values.Ambiguous)

            val ALREADY_ON_ROAD: Call = Impl(Status.Values.AlreadyOnRoad)
        }

        private class Impl(
            override val result: Result?,
            override val status: Status
        ) : Call {

            constructor(status: Status) : this(null, status)

            class Api(override val handle: Call) : Call.Api

            override val proxy = Api(this)
        }
    }
}
