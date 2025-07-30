package com.github.gaboss44.ecolpr.api.transition

import com.github.gaboss44.ecolpr.api.model.rank.Rank
import com.github.gaboss44.ecolpr.api.model.road.Road
import com.github.gaboss44.ecolpr.api.util.Data
import com.willfp.eco.core.config.interfaces.Config
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

        FROM_RANK("from_rank"),

        FROM_NO_RANK("from_no_rank"),

        TO_RANK("to_rank"),

        TO_NO_RANK("to_no_rank"),

        RANKUP("rankup", TO_RANK),

        RANKUP_FROM_RANK("rankup_from_rank", RANKUP, FROM_RANK),

        RANKUP_FROM_NO_RANK("rankup_from_no_rank", RANKUP, FROM_NO_RANK),

        PRESTIGE("prestige"),

        PRESTIGE_TO_RANK("prestige_to_rank", PRESTIGE, TO_RANK),

        PRESTIGE_TO_NO_RANK("prestige_to_no_rank", PRESTIGE, TO_NO_RANK),

        PRESTIGE_WITH_TARGET("prestige_with_target", PRESTIGE_TO_RANK),

        PRESTIGE_WITHOUT_TARGET("prestige_without_target", PRESTIGE),

        INGRESSION("ingression", RANKUP_FROM_NO_RANK),

        ASCENSION("ascension", RANKUP_FROM_RANK),

        EGRESSION("egression", PRESTIGE_TO_NO_RANK, PRESTIGE_WITHOUT_TARGET),

        RECURSION("recursion", PRESTIGE_TO_RANK, PRESTIGE_WITHOUT_TARGET),

        MIGRATION("migration", PRESTIGE_WITH_TARGET);

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
            operator fun get(lowerValue: String?) = lowerValue?.let { byLowerValue[it.lowercase()] }
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
            operator fun get(lowerValue: String?) = lowerValue?.let { byLowerValue[it.lowercase()] }
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
            operator fun get(lowerValue: String?) = lowerValue?.let { byLowerValue[it.lowercase()] }
        }
    }

    interface Attempt : Transition

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

            private val byLowerValue = entries.associateBy { it.lowerValue.lowercase() }

            @JvmStatic
            operator fun get(lowerValue: String?) = lowerValue?.let { byLowerValue[it.lowercase()] }
        }
    }

    interface Result : Transition, com.github.gaboss44.ecolpr.api.util.Result {

        /** If the transition is forced, there's no attempt */
        val attempt: Attempt?

        val status: Status

        override fun wasSuccessful() = status.wasSuccessful()
    }

    interface Call : com.github.gaboss44.ecolpr.api.util.Result {

        val player: Player

        val road: Road

        val source: Source

        val result: Result?

        val status: Status

        override fun wasSuccessful() = this.status.wasSuccessful()

        enum class Status(
            val lowerValue: String,
            private val success: Boolean,
            override val parents: List<Status> = emptyList()
        ) : com.github.gaboss44.ecolpr.api.util.Result, Data<Status> {

            SUCCESS("success", true),

            FAILURE("failure"),

            EMPTY_ROAD("empty_road", FAILURE),

            AMBIGUOUS_RANK("ambiguous_rank", FAILURE),

            FROM_RANK_FAILURE("from_rank_failure", FAILURE),

            NOT_ON_ROAD("not_on_road", FROM_RANK_FAILURE),

            LAST_RANK("last_rank", FROM_RANK_FAILURE),

            NOT_LAST_RANK("not_last_rank", FROM_RANK_FAILURE),

            TO_RANK_FAILURE("to_rank_failure", FAILURE),

            ALREADY_ON_ROAD("already_on_road", TO_RANK_FAILURE),

            INACTIVE_RANK("inactive_rank", TO_RANK_FAILURE),

            PRESTIGE_FAILURE("prestige_failure", FAILURE),

            PRESTIGE_TYPE_NOT_SPECIFIED("prestige_type_not_specified", PRESTIGE_FAILURE),

            PRESTIGE_WITH_TARGET_FAILURE("prestige_with_target_failure", PRESTIGE_FAILURE, TO_RANK_FAILURE),

            NO_PRESTIGE_ROAD("no_prestige_road", PRESTIGE_WITH_TARGET_FAILURE),

            EMPTY_PRESTIGE_ROAD("empty_prestige_road", PRESTIGE_WITH_TARGET_FAILURE),

            AMBIGUOUS_PRESTIGE_RANK("ambiguous_prestige_rank", PRESTIGE_WITH_TARGET_FAILURE),

            ALREADY_ON_PRESTIGE_ROAD("already_on_prestige_road", PRESTIGE_WITH_TARGET_FAILURE),

            INACTIVE_PRESTIGE_RANK("inactive_prestige_rank", PRESTIGE_WITH_TARGET_FAILURE);

            constructor(
                lowerValue: String,
                vararg parents: Status
            ) : this(
                lowerValue,
                false,
                parents.toList()
            )

            override fun wasSuccessful() = success

            companion object {

                private val byLowerValue = entries.associateBy { it.lowerValue.lowercase() }

                @JvmStatic
                operator fun get(lowerValue: String?) = lowerValue?.let { byLowerValue[it.lowercase()] }
            }
        }
    }

    interface FromRank : Transition {

        val fromRank: Rank

        interface Attempt : FromRank, Transition.Attempt

        interface Result : FromRank, Transition.Result {

            override val attempt: Attempt?
        }

        interface Call : Transition.Call {

            val fromRank: Rank?

            override val result: Result?
        }
    }

    interface ToRank : Transition {

        val toRank: Rank

        interface Attempt : ToRank, Transition.Attempt

        interface Result : ToRank, Transition.Result {

            override val attempt: Attempt?
        }

        interface Call : Transition.Call {

            val toRank: Rank?

            override val result: Result?
        }
    }

    class Options private constructor(
        val mode: Mode,
        private val events: Boolean,
        private val effects: Boolean,
        val prestigeTarget: String? = null
    ) {

        fun areEventsEnabled() = events

        fun areEffectsEnabled() = effects

        fun toBuilder(): Builder = Builder(mode).apply {
            setEvents(events)
            setEffects(effects)
            setPrestigeTarget(prestigeTarget)
        }

        override fun toString(): String {
            return "Options(" +
                    "mode=$mode, " +
                    "events=$events, " +
                    "effects=$effects, " +
                    "prestigeTarget=$prestigeTarget)"
        }

        companion object {
            private val FORCE = Builder(Mode.FORCE).build()
            private val TEST = Builder(Mode.TEST).build()
            private val NORMAL = Builder(Mode.NORMAL).build()

            @JvmStatic
            fun force(): Options = FORCE

            @JvmStatic
            fun test(): Options = TEST

            @JvmStatic
            fun normal(): Options = NORMAL

            @JvmStatic
            fun mode(mode: Mode): Options = when (mode) {
                Mode.TEST -> test()
                Mode.FORCE -> force()
                Mode.NORMAL -> normal()
            }

            @JvmStatic
            fun iterateArgs(args: Iterable<String>): Options {
                val builder = Builder(Mode.NORMAL)
                for (arg in args) {
                    val str = arg.lowercase()
                    when {
                        str == "--test" -> builder.setMode(Mode.TEST)
                        str == "--force" -> builder.setMode(Mode.FORCE)
                        str == "--no-events" -> builder.setEvents(false)
                        str == "--no-effects" -> builder.setEffects(false)
                        str.startsWith("--prestige-target=") -> {
                            val target = str.substringAfter("--prestige-target=", "")
                            if (target.isNotBlank()) builder.setPrestigeTarget(target)
                        }
                    }
                }
                return builder.build()
            }

            @JvmStatic
            fun consumeArgs(args: MutableList<String>): Options {
                val builder = Builder(Mode.NORMAL)

                val iterator = args.iterator()
                while (iterator.hasNext()) {
                    val arg = iterator.next()
                    val str = arg.lowercase()

                    when {
                        str == "--test" -> {
                            builder.setMode(Mode.TEST)
                            iterator.remove()
                        }

                        str == "--force" -> {
                            builder.setMode(Mode.FORCE)
                            iterator.remove()
                        }

                        str == "--no-events" -> {
                            builder.setEvents(false)
                            iterator.remove()
                        }

                        str == "--no-effects" -> {
                            builder.setEffects(false)
                            iterator.remove()
                        }

                        str.startsWith("--prestige-target=") -> {
                            val target = arg.substringAfter("--prestige-target=", "")
                            if (target.isNotBlank()) {
                                builder.setPrestigeTarget(target)
                                iterator.remove()
                            }
                        }
                    }
                }

                return builder.build()
            }

            @JvmStatic
            fun parseArgs(config: Config): Options {
                val builder = Builder(Mode.NORMAL)

                Mode[config.getStringOrNull("mode")]?.let { builder.setMode(it) }

                config.getBoolOrNull("events")?.let { builder.setEvents(it) }

                config.getBoolOrNull("effects")?.let { builder.setEffects(it) }

                config.getStringOrNull("prestige-target")
                    ?.takeIf { it.isNotBlank() }
                    ?.let { builder.setPrestigeTarget(it) }

                return builder.build()
            }
        }

        class Builder(
            private var mode: Mode,
            private var events: Boolean = true,
            private var effects: Boolean = true,
            private var prestigeTarget: String? = null
        ) {

            fun getMode() = mode

            fun setMode(mode: Mode) = apply { this.mode = mode }

            fun setEvents(boolean: Boolean) = apply { this.events = boolean }

            fun setEffects(boolean: Boolean) = apply { this.effects = boolean }

            fun setPrestigeTarget(target: String?) = apply { this.prestigeTarget = target }

            fun areEventsEnabled() = events

            fun areEffectsEnabled() = effects

            fun getPrestigeTarget() = prestigeTarget

            fun build() = Options(mode, events, effects, prestigeTarget)
        }
    }
}
