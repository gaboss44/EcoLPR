package com.github.gaboss44.ecolpr.core.transition.dto

import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.core.model.rank.Rank
import com.github.gaboss44.ecolpr.core.model.road.Road

interface TransitionDto : Transition {

    override val road: Road

    val proxy: Api

    interface Api : Transition {

        val handle: TransitionDto

        override val player get() = this.handle.player

        override val road get() = this.handle.road.proxy

        override val source get() = this.handle.source

        override val mode get() = this.handle.mode
    }

    interface Attempt : TransitionDto, Transition.Attempt {

        override val proxy: Api

        interface Api : TransitionDto.Api, Transition.Attempt {

            override val handle: Attempt
        }
    }

    interface Result : TransitionDto, Transition.Result {

        override val attempt: Attempt?

        override val proxy: Api

        interface Api : TransitionDto.Api, Transition.Result {

            override val handle: Result

            override val status get() = this.handle.status

            override val attempt get() = this.handle.attempt?.proxy
        }
    }

    interface Call : Transition.Call {

        override val result: Result?

        override val status: Status

        interface Status : Transition.Call.Status

        val proxy: Api

        interface Api : Transition.Call {

            val handle: Call

            override val result get() = this.handle.result?.proxy

            override val status get() = this.handle.status
        }
    }

    interface FromRank : TransitionDto, Transition.FromRank {

        override val fromRank: Rank

        override val proxy: Api

        interface Api : TransitionDto.Api, Transition.FromRank {

            override val handle: FromRank

            override val fromRank get() = this.handle.fromRank.proxy
        }

        interface Attempt : FromRank, TransitionDto.Attempt, Transition.FromRank.Attempt {

            override val proxy: Api

            interface Api : FromRank.Api, TransitionDto.Attempt.Api, Transition.FromRank.Attempt {

                override val handle: Attempt
            }
        }

        interface Result : FromRank, TransitionDto.Result, Transition.FromRank.Result {

            override val attempt: Attempt?

            override val proxy: Api

            interface Api: FromRank.Api, TransitionDto.Result.Api, Transition.FromRank.Result {

                override val handle: Result

                override val attempt get() = this.handle.attempt?.proxy
            }
        }

        interface Call : TransitionDto.Call, Transition.FromRank.Call {

            override val result: Result?

            override val status: Status

            interface Status : TransitionDto.Call.Status, Transition.FromRank.Call.Status

            override val proxy: Api

            interface Api : TransitionDto.Call.Api, Transition.FromRank.Call {

                override val handle: Call

                override val result get() = this.handle.result?.proxy

                override val status get() = this.handle.status
            }
        }
    }

    interface ToRank : TransitionDto, Transition.ToRank {

        override val toRank: Rank

        override val proxy: Api

        interface Api : TransitionDto.Api, Transition.ToRank {

            override val handle: ToRank

            override val toRank get() = this.handle.toRank.proxy
        }

        interface Attempt : ToRank, TransitionDto.Attempt, Transition.ToRank.Attempt {

            override val proxy: Api

            interface Api : ToRank.Api, TransitionDto.Attempt.Api, Transition.ToRank.Attempt {

                override val handle: Attempt
            }
        }

        interface Result : ToRank, TransitionDto.Result, Transition.ToRank.Result {

            override val attempt: Attempt?

            override val proxy: Api

            interface Api: ToRank.Api, TransitionDto.Result.Api, Transition.ToRank.Result {

                override val handle: Result

                override val attempt get() = this.handle.attempt?.proxy
            }
        }

        interface Call : TransitionDto.Call, Transition.ToRank.Call {

            override val result: Result?

            override val status: Status

            interface Status : TransitionDto.Call.Status, Transition.ToRank.Call.Status

            override val proxy: Api

            interface Api : TransitionDto.Call.Api, Transition.ToRank.Call {

                override val handle: Call

                override val result get() = this.handle.result?.proxy

                override val status get() = this.handle.status
            }
        }
    }
}
