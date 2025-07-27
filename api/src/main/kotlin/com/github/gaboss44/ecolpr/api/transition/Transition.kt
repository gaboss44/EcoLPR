package com.github.gaboss44.ecolpr.api.transition

import com.github.gaboss44.ecolpr.api.model.rank.Rank
import com.github.gaboss44.ecolpr.api.model.road.Road
import com.github.gaboss44.ecolpr.api.util.Data
import org.bukkit.entity.Player
import org.jetbrains.annotations.ApiStatus

@ApiStatus.NonExtendable
interface Transition {

    val player: Player

    val road: Road

    val type: Type

    enum class Type(
        val lowerValue: String,
        override val parents: List<Type> = emptyList()
    ) : Data<Type> {

        RANKUP("rankup"),

        INGRESSION("ingression", RANKUP),

        ASCENSION("ascension", RANKUP),

        PRESTIGE("prestige"),

        EGRESSION("egression", PRESTIGE),

        RECURSION("recursion", PRESTIGE),

        MIGRATION("migration", PRESTIGE);

        constructor(
            lowerValue: String,
            vararg parents: Type
        ) : this(
            lowerValue,
            parents.toList()
        )

        companion object {
            private val byLowerValue = entries.associateBy { it.lowerValue.lowercase() }

            @JvmStatic
            fun getByLowerValue(lowerValue: String) = byLowerValue[lowerValue.lowercase()]
        }
    }

    val source: Source

    enum class Source(
        val lowerValue: String,
        override val parents: List<Source> = emptyList()
    ) : Data<Source> {

        COMMAND_SENDER("command_sender"),

        PLAYER("player", COMMAND_SENDER),

        OPERATOR("operator", COMMAND_SENDER),

        ADMIN("admin", PLAYER, OPERATOR),

        CONSOLE("console", OPERATOR),

        SCRIPT("script"),

        API("api"),

        NOT_SPECIFIED("not_specified");

        constructor(
            lowerValue: String,
            vararg parents: Source
        ) : this(
            lowerValue,
            parents.toList()
        )

        companion object {
            private val byLowerValue = entries.associateBy { it.lowerValue.lowercase() }

            @JvmStatic
            fun getByLowerValue(lowerValue: String) = byLowerValue[lowerValue.lowercase()]
        }
    }

    val mode: Mode

    enum class Mode(val lowerValue: String) {

        TEST("test"),

        FORCE("force"),

        NORMAL("normal");

        companion object {
            private val byLowerValue = entries.associateBy { it.lowerValue.lowercase() }

            @JvmStatic
            fun getByLowerValue(lowerValue: String) = byLowerValue[lowerValue.lowercase()]
        }
    }

    interface Attempt : Transition

    interface Result : Transition, com.github.gaboss44.ecolpr.api.util.Result {

        /** If the transition is forced, there's no attempt */
        val attempt: Attempt?

        val status: Status

        override fun wasSuccessful() = status.wasSuccessful()

        enum class Status(
            val lowerValue: String,
            var success: Boolean = false,
            val triggerValue: Double = 0.0,
            override val parents: List<Status> = emptyList()
        ) : com.github.gaboss44.ecolpr.api.util.Result, Data<Status> {

            SUCCESS("success", true, 1.0),

            FAILURE("failure"),

            CANCELLED("cancelled", FAILURE),

            STALE("stale", FAILURE);

            constructor(
                lowerValue: String,
                success: Boolean,
                triggerValue: Double,
                vararg parents: Status
            ) : this(
                lowerValue,
                success,
                triggerValue,
                parents.toList()
            )

            constructor(
                lowerValue: String,
                vararg parents: Status
            ) : this(
                lowerValue,
                false,
                0.0,
                parents.toList()
            )

            override fun wasSuccessful() = success

            companion object {

                private val byLowerValue = entries.associateBy {
                    it.lowerValue.lowercase()
                }

                @JvmStatic
                fun getByLowerValue(
                    lowerValue: String
                ) = byLowerValue[lowerValue.lowercase()]
            }
        }
    }

    interface Call : com.github.gaboss44.ecolpr.api.util.Result {

        val result: Result?

        val status: Status

        override fun wasSuccessful() = this.status.wasSuccessful()

        interface Status : com.github.gaboss44.ecolpr.api.util.Result {

            fun isRoadEmpty() : Boolean

            fun isAmbiguous() : Boolean
        }
    }

    interface FromRank : Transition {

        val fromRank: Rank

        interface Attempt : FromRank, Transition.Attempt

        interface Result : FromRank, Transition.Result {

            override val attempt: Attempt?
        }

        interface Call : Transition.Call {

            override val result: Result?

            override val status: Status

            interface Status : Transition.Call.Status {

                fun isFromRankAbsent() : Boolean
            }
        }
    }

    interface ToRank : Transition {

        val toRank: Rank

        interface Attempt : ToRank, Transition.Attempt

        interface Result : ToRank, Transition.Result {

            override val attempt: Attempt?
        }

        interface Call : Transition.Call {

            override val result: Result?

            override val status: Status

            interface Status : Transition.Call.Status {

                fun isToRankAbsent() : Boolean
            }
        }
    }

    class Options private constructor (val builder: Builder) {

        val mode = this.builder.mode

        fun mode(mode: Mode) = this.builder.mode(mode).build()

        fun silent(boolean: Boolean) = this.builder.silent(boolean).build()

        fun isSilent() = this.builder.isSilent()

        fun effects(boolean: Boolean) = this.builder.effects(boolean).build()

        fun areEffectsEnabled() = this.builder.areEffectsEnabled()

        companion object {

            private val FORCE = newBuilder(Mode.FORCE).build()

            private val TEST = newBuilder(Mode.TEST).build()

            private val NORMAL = newBuilder(Mode.NORMAL).build()

            @JvmStatic
            fun newBuilder(mode: Mode) : Builder = DefaultBuilder(mode)

            @JvmStatic
            fun force() = FORCE

            @JvmStatic
            fun test() = TEST

            @JvmStatic
            fun normal() = NORMAL

            @JvmStatic
            fun mode(mode: Mode) = when(mode) {
                Mode.TEST -> test()
                Mode.FORCE -> force()
                Mode.NORMAL -> normal()
            }
        }

        interface Builder {

            val mode: Mode

            fun mode(mode: Mode) : Builder

            fun silent(boolean: Boolean) : Builder

            fun isSilent() : Boolean

            fun effects(boolean: Boolean) : Builder

            fun areEffectsEnabled() : Boolean

            fun build() : Options
        }

        private data class DefaultBuilder (
            override val mode: Mode,
            private val effects: Boolean = true,
            private val silent: Boolean = false
        ) : Builder {

            override fun mode(mode: Mode) = this.copy(mode)

            override fun silent(boolean: Boolean) = this.copy(silent = boolean)

            override fun isSilent() = silent

            override fun effects(boolean: Boolean) = this.copy(effects = boolean)

            override fun areEffectsEnabled() = effects

            override fun build() = Options(this)
        }

        override fun equals(other: Any?): Boolean {
            if (other === this) return true
            if (other !is Options) return false
            return this.builder == other.builder
        }

        override fun hashCode(): Int {
            var result = builder.hashCode()
            result = 31 * result + mode.hashCode()
            return result
        }
    }
}
