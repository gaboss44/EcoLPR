package com.github.gaboss44.ecolpr.transition

import com.github.gaboss44.ecolpr.rank.Rank
import com.github.gaboss44.ecolpr.road.Road
import org.bukkit.entity.Player

sealed interface RankTransition {
    val type: Type
    val player: Player
    val source: Source
    val status: Status
    val road: Road
    val from: Rank?
    val to: Rank?

    fun hasFrom(): Boolean = from != null

    fun hasTo(): Boolean = to != null

    interface Data { val lowerValue: String }

    enum class Type(override val lowerValue: String) : Data {
        PROMOTION("promotion"),
        DEMOTION("demotion"),
        REBOOT("reboot"),
        GRADUATION("graduation");

        val isPromotion: Boolean
            get() = this == PROMOTION

        val isDemotion: Boolean
            get() = this == DEMOTION

        val isReboot: Boolean
            get() = this == REBOOT

        val isGraduation: Boolean
            get() = this == GRADUATION
    }

    enum class Source(override val lowerValue: String, private vararg val parents: Source) : Data {
        COMMAND_SENDER("command_sender"),
        PLAYER("player", COMMAND_SENDER),
        OPERATOR("operator"),
        ADMIN("admin", OPERATOR, COMMAND_SENDER),
        CONSOLE("console", OPERATOR),
        SCRIPT("script"),
        API("api");

        val isCommandSender: Boolean
            get() = isOrInheritsFrom(COMMAND_SENDER)

        val isPlayer: Boolean
            get() = isOrInheritsFrom(PLAYER)

        val isOperator: Boolean
            get() = isOrInheritsFrom(OPERATOR)

        val isAdmin: Boolean
            get() = isOrInheritsFrom(ADMIN)

        val isConsole: Boolean
            get() = isOrInheritsFrom(CONSOLE)

        val isScript: Boolean
            get() = isOrInheritsFrom(SCRIPT)

        val isApi: Boolean
            get() = isOrInheritsFrom(API)

        private fun isOrInheritsFrom(other: Source): Boolean {
            if (this == other) return true
            return parents.any { parent ->
                parent == other || parent.isOrInheritsFrom(other)
            }
        }

        companion object {
            private val byLowerValue: Map<String, Source> by lazy {
                entries.associateBy { it.lowerValue }
            }

            fun of(value: String): Source? = byLowerValue[value.lowercase()]
        }

    }

    enum class Status(override val lowerValue: String, val triggerValue: Double = 0.0) : Data {
        SUCCESS("success", 1.0),
        ROAD_GENERIC_CONDITIONS_FAILURE("road_generic_conditions_failure"),
        ROAD_PROMOTION_CONDITIONS_FAILURE("road_promotion_conditions_failure"),
        ROAD_DEMOTION_CONDITIONS_FAILURE("road_demotion_conditions_failure"),
        ROAD_GRADUATION_CONDITIONS_FAILURE("road_graduation_conditions_failure"),
        ROAD_REBOOT_CONDITIONS_FAILURE("road_reboot_conditions_failure"),
        NEXT_ROAD_GENERIC_CONDITIONS_FAILURE("next_road_generic_conditions_failure");

        val isSuccess: Boolean
            get() = this == SUCCESS

        companion object {
            private val byLowerValue: Map<String, Status> by lazy {
                entries.associateBy { it.lowerValue }
            }

            fun of(value: String): Status? = byLowerValue[value.lowercase()]
        }
    }
}