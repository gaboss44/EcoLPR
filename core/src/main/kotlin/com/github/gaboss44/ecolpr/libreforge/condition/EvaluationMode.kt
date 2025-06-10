package com.github.gaboss44.ecolpr.libreforge.condition

enum class EvaluationMode(private vararg val names: String) {
    ANY("any", "any_of"),
    ALL("all", "all_of");

    companion object {
        private val byNames = entries.flatMap { mode ->
            mode.names.map { it.lowercase() to mode }
        }.toMap()

        fun of(name: String): EvaluationMode? = byNames[name.lowercase()]
    }
}