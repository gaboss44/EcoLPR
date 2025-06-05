package com.github.gaboss44.ecolpr.rank

import org.jetbrains.annotations.ApiStatus

/**
 * Utility interface for rank transition data.
 */
@ApiStatus.NonExtendable
interface RankTransition {
    val road: Road
    val from: Rank?
    val to: Rank?
    val direction: Direction
    val flow: Flow
    val source: Source
    val status: Status

    interface Data {
        val lowerValue: String
    }

    enum class Direction(override val lowerValue: String) : Data {
        UP("up"),
        DOWN("down"),
        LOOP("loop");

        val isUp: Boolean
            get() = this == UP

        val isDown: Boolean
            get() = this == DOWN

        val isLoop: Boolean
            get() = this == LOOP
    }

    enum class Flow(override val lowerValue: String) : Data {
        IN("in"),
        OUT("out"),
        WITHIN("within");

        val isIn: Boolean
            get() = this == IN

        val isOut: Boolean
            get() = this == OUT

        val isWithin: Boolean
            get() = this == WITHIN
    }

    enum class Source(override val lowerValue: String) : Data {
        NATURAL("natural"),
        ADMIN("admin"),
        CONSOLE("console"),
        PLUGIN("plugin"),
        SCRIPT("script");

        val isNatural: Boolean
            get() = this == NATURAL

        val isAdmin: Boolean
            get() = this == ADMIN

        val isConsole: Boolean
            get() = this == CONSOLE

        val isPlugin: Boolean
            get() = this == PLUGIN

        val isScript: Boolean
            get() = this == SCRIPT
    }

    enum class Status(override val lowerValue: String, val triggerValue: Double = 0.0) : Data {
        SUCCESS("success", 1.0),
        NO_TRACK("no_track"),
        UNSATISFIED_CONTEXT("unsatisfied_context"),
        CONDITION("condition");

        val isSuccess: Boolean
            get() = this == SUCCESS

        val isNoTrack: Boolean
            get() = this == NO_TRACK

        val isUnsatisfiedContext: Boolean
            get() = this == UNSATISFIED_CONTEXT

        val isCondition: Boolean
            get() = this == CONDITION

        val isFailure: Boolean
            get() = this != SUCCESS
    }
}