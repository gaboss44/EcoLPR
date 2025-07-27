package com.github.gaboss44.ecolpr.core.transition.dto.generic

import com.github.gaboss44.ecolpr.api.transition.generic.Rankup
import com.github.gaboss44.ecolpr.core.transition.dto.TransitionDto

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
        override val status: Status
        override val proxy: Api

        interface Api : TransitionDto.ToRank.Call.Api, Rankup.Call {
            override val handle: Call
            override val result get() = handle.result?.proxy
            override val status get() = handle.status
        }

        interface Status : TransitionDto.ToRank.Call.Status, Rankup.Call.Status {

            enum class Values(
                val success: Boolean,
                val emptyRoad: Boolean,
                val absentToRank: Boolean,
                val ambiguous: Boolean
            ) : Status {

                Success,

                EmptyRoad(
                    emptyRoad = true,
                    absentToRank = true,
                    ambiguous = false
                ),

                Ambiguous(
                    emptyRoad = false,
                    absentToRank = true,
                    ambiguous = true
                );

                constructor() : this(
                    success = true,
                    emptyRoad = false,
                    absentToRank = false,
                    ambiguous = false
                )

                constructor(
                    emptyRoad: Boolean,
                    absentToRank: Boolean,
                    ambiguous: Boolean
                ) : this(
                    success = false,
                    emptyRoad = emptyRoad,
                    absentToRank = absentToRank,
                    ambiguous = ambiguous
                )

                override fun wasSuccessful() = success

                override fun isRoadEmpty() = emptyRoad

                override fun isToRankAbsent() = absentToRank

                override fun isAmbiguous() = ambiguous
            }
        }

        companion object {

            fun success(result: Result): Call = Impl(result, Status.Values.Success)

            val EMPTY_ROAD: Call = Impl(Status.Values.EmptyRoad)

            val AMBIGUOUS: Call = Impl(Status.Values.Ambiguous)
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

            override val status: Status

            interface Status : RankupDto.Call.Status, TransitionDto.FromRank.Call.Status, Rankup.FromRank.Call.Status

            override val proxy: Api

            interface Api : RankupDto.Call.Api, TransitionDto.FromRank.Call.Api, Rankup.FromRank.Call {

                override val handle: Call

                override val result get() = this.handle.result?.proxy

                override val status get() = this.handle.status
            }
        }
    }
}
