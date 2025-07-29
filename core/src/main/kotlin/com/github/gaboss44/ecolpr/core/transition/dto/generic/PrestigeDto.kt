package com.github.gaboss44.ecolpr.core.transition.dto.generic

import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.core.model.road.Road
import com.github.gaboss44.ecolpr.api.transition.generic.Prestige
import com.github.gaboss44.ecolpr.core.model.rank.Rank
import com.github.gaboss44.ecolpr.core.transition.dto.TransitionDto
import org.bukkit.entity.Player

interface PrestigeDto : TransitionDto.FromRank, Prestige {

    override val proxy: Api

    interface Api : TransitionDto.FromRank.Api, Prestige {

        override val handle: PrestigeDto

        override val prestigeLevel get() = this.handle.prestigeLevel
    }

    interface Attempt : PrestigeDto, TransitionDto.FromRank.Attempt, Prestige.Attempt {

        override val proxy: Api

        interface Api : PrestigeDto.Api, TransitionDto.FromRank.Attempt.Api, Prestige.Attempt {

            override val handle: Attempt
        }
    }

    interface Result : PrestigeDto, TransitionDto.FromRank.Result, Prestige.Result {

        override val attempt: Attempt?

        override val proxy: Api

        interface Api : PrestigeDto.Api, TransitionDto.FromRank.Result.Api, Prestige.Result {

            override val handle: Result

            override val attempt get() = handle.attempt?.proxy
        }
    }

    interface Call : TransitionDto.FromRank.Call, Prestige.Call {

        override val result: Result?

        override val proxy: Api

        interface Api : TransitionDto.FromRank.Call.Api, Prestige.Call {

            override val handle: Call

            override val result get() = this.handle.result?.proxy

            override val prestigeType get() = this.handle.prestigeType
        }

        companion object {
            fun notSpecified(player: Player, road: Road): Call = NotSpecified(player, road)
        }

        private class NotSpecified(
            override val player: Player,
            override val road: Road
        ) : Call {
            override val result = null
            override val fromRank = null
            override val prestigeType = null
            override val status = Transition.Call.Status.PRESTIGE_TYPE_NOT_SPECIFIED
            override val proxy: Api = Api(this)

            class Api(override val handle: NotSpecified) : Call.Api
        }
    }

    interface ToRank : PrestigeDto, TransitionDto.ToRank, Prestige.ToRank {

        override val proxy: Api

        interface Api : PrestigeDto.Api, TransitionDto.ToRank.Api, Prestige.ToRank {

            override val handle: ToRank

            override val toRank get() = this.handle.toRank.proxy
        }

        interface Attempt : ToRank, PrestigeDto.Attempt, TransitionDto.ToRank.Attempt, Prestige.ToRank.Attempt {

            override val proxy: Api

            interface Api : ToRank.Api, PrestigeDto.Attempt.Api, TransitionDto.ToRank.Attempt.Api, Prestige.ToRank.Attempt {

                override val handle: Attempt
            }
        }

        interface Result : ToRank, PrestigeDto.Result, TransitionDto.ToRank.Result, Prestige.ToRank.Result {

            override val attempt: Attempt?

            override val proxy: Api

            interface Api: ToRank.Api, PrestigeDto.Result.Api, TransitionDto.ToRank.Result.Api, Prestige.ToRank.Result {

                override val handle: Result

                override val attempt get() = this.handle.attempt?.proxy
            }
        }

        interface Call : PrestigeDto.Call, TransitionDto.ToRank.Call, Prestige.ToRank.Call {

            override val result: Result?

            override val proxy: Api

            interface Api : PrestigeDto.Call.Api, TransitionDto.ToRank.Call.Api, Prestige.ToRank.Call {

                override val handle: Call

                override val result get() = this.handle.result?.proxy
            }
        }
    }

    interface WithTarget : ToRank, Prestige.WithTarget {

        override val prestigeRoad: Road

        override val proxy: Api

        interface Api : ToRank.Api, Prestige.WithTarget {

            override val handle: WithTarget

            override val prestigeRoad get() = this.handle.prestigeRoad.proxy
        }

        interface Attempt : WithTarget, ToRank.Attempt, Prestige.WithTarget.Attempt {

            override val proxy: Api

            interface Api : WithTarget.Api, ToRank.Attempt.Api, Prestige.WithTarget.Attempt {

                override val handle: Attempt
            }
        }

        interface Result : WithTarget, ToRank.Result, Prestige.WithTarget.Result {

            override val toRank: Rank

            override val attempt: Attempt?

            override val proxy: Api

            interface Api: WithTarget.Api, ToRank.Result.Api, Prestige.WithTarget.Result {

                override val handle: Result

                override val attempt get() = this.handle.attempt?.proxy
            }
        }

        interface Call : ToRank.Call, Prestige.WithTarget.Call {

            override val result: Result?

            override val prestigeRoad: Road?

            override val proxy: Api

            interface Api : ToRank.Call.Api, Prestige.WithTarget.Call {

                override val handle: Call

                override val result get() = this.handle.result?.proxy

                override val prestigeRoad get() = this.handle.prestigeRoad?.proxy
            }
        }
    }
}
