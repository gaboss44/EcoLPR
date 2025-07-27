package com.github.gaboss44.ecolpr.core

import com.willfp.eco.core.config.base.LangYml
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.util.StringUtils
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class EcoLprLangYml(
    plugin: EcoLprPlugin
) : LangYml(plugin) {

    override fun clearInjectedPlaceholders() {
        super.clearInjectedPlaceholders()
        if (plugin.isDebug) plugin.logger.info(
            "Cleared injected placeholders in LangYml"
        )
    }

    fun string(reference: String): String = this
        .get("$KEY_MESSAGES.$reference")
        ?.toString()
        ?: run {
            if (plugin.isDebug) plugin.logger.warning(
                "Could not find message with the reference key '$reference'"
            )
            return ""
        }

    fun strings(reference: String): List<String> {
        val value = this.get("$KEY_MESSAGES.$reference")
        if (value is List<*>) {
            return value.filterIsInstance<String>()
        }
        if (plugin.isDebug) plugin.logger.warning(
            "Could not find messages with the reference key '$reference'"
        )
        return emptyList()
    }

    override fun getMessage(
        reference: String,
        option: StringUtils.FormatOption
    ) = this.getMessage(reference, option) { it }

    fun getMessage(
        reference: String,
        apply: (String) -> String
    ) = this.getMessage(
        reference,
        StringUtils.FormatOption.WITH_PLACEHOLDERS,
        apply
    )

    fun getMessage(
        reference: String,
        option: StringUtils.FormatOption,
        apply: (String) -> String
    ): String {
        var string = this.string(reference).also { if (it.isEmpty()) return "" }

        if (option == StringUtils.FormatOption.WITH_PLACEHOLDERS)
            for (injection in placeholderInjections) {
                string = injection.tryTranslateQuickly(
                    string,
                    PlaceholderContext.EMPTY
                )
            }

        return StringUtils.format(apply(string), option)
    }

    fun getMessage(
        reference: String,
        player: Player
    ) = getMessage(
        reference,
        player
    ) { it }

    fun getMessage(
        reference: String,
        context: PlaceholderContext
    ) = getMessage(reference, context) { it }

    fun getMessage(
        reference: String,
        player: Player,
        apply: (String) -> String
    ) = getMessage(
        reference,
        PlaceholderContext(player),
        apply
    )

    fun getMessage(
        reference: String,
        context: PlaceholderContext,
        apply: (String) -> String
    ): String {
        var string = this.string(reference).also { if (it.isEmpty()) return "" }

        for (injection in placeholderInjections) {
            string = injection.tryTranslateQuickly(string, context)
        }

        return StringUtils.format(apply(string), context)
    }

    fun getMessages(
        reference: String,
        option: StringUtils.FormatOption
    ) = this.getMessages(reference, option) { it }

    fun getMessages(
        reference: String,
        apply: (String) -> String
    ) = this.getMessages(
        reference,
        StringUtils.FormatOption.WITH_PLACEHOLDERS,
        apply
    )

    fun getMessages(
        reference: String,
        option: StringUtils.FormatOption,
        apply: (String) -> String
    ): List<String> {
        val strings = this.strings(reference).also { if (it.isEmpty()) return emptyList() }

        return strings.map { original ->
            var str = original
            if (option == StringUtils.FormatOption.WITH_PLACEHOLDERS) {
                for (injection in placeholderInjections) {
                    str = injection.tryTranslateQuickly(str, PlaceholderContext.EMPTY)
                }
            }
            StringUtils.format(apply(str), option)
        }
    }

    fun getMessages(
        reference: String,
        player: Player
    ) = getMessages(
        reference,
        player
    ) { it }

    fun getMessages(
        reference: String,
        context: PlaceholderContext
    ) = getMessages(reference, context) { it }

    fun getMessages(
        reference: String,
        player: Player,
        apply: (String) -> String
    ) = getMessages(
        reference,
        PlaceholderContext(player),
        apply
    )

    fun getMessages(
        reference: String,
        context: PlaceholderContext,
        apply: (String) -> String
    ): List<String> {
        val strings = this.strings(reference).also { if (it.isEmpty()) return emptyList() }

        return strings.map { original ->
            var str = original
            for (injection in placeholderInjections) {
                str = injection.tryTranslateQuickly(str, context)
            }
            StringUtils.format(apply(str), context)
        }
    }

    fun sendMessage(
        recipient: CommandSender,
        reference: String,
        apply: (String) -> String = { it }
    ) = if (recipient is Player) sendMessage(
        recipient,
        reference,
        PlaceholderContext(recipient),
        apply
    ) else sendMessage(
        recipient,
        reference,
        PlaceholderContext.EMPTY,
        apply
    )

    fun sendMessage(
        recipient: CommandSender,
        reference: String,
        player: Player,
        apply: (String) -> String = { it }
    ) = sendMessage(
        recipient,
        reference,
        PlaceholderContext(player),
        apply
    )

    fun sendMessage(
        recipient: CommandSender,
        reference: String,
        context: PlaceholderContext,
        apply: (String) -> String = { it }
    ): Boolean {
        val message = getMessage(
            reference,
            context,
            apply
        ).also { if (it.isEmpty()) return false }
        recipient.sendMessage(message)
        return true
    }

    fun sendMessages(
        recipient: CommandSender,
        reference: String,
        apply: (String) -> String = { it }
    ): Boolean = if (recipient is Player) sendMessages(
        recipient,
        reference,
        PlaceholderContext(recipient),
        apply
    ) else sendMessages(
        recipient,
        reference,
        PlaceholderContext.EMPTY,
        apply
    )

    fun sendMessages(
        recipient: CommandSender,
        reference: String,
        player: Player,
        apply: (String) -> String = { it }
    ): Boolean = sendMessages(
        recipient,
        reference,
        PlaceholderContext(player),
        apply
    )

    fun sendMessages(
        recipient: CommandSender,
        reference: String,
        context: PlaceholderContext,
        apply: (String) -> String = { it }
    ): Boolean {
        val messages = getMessages(reference, context, apply)
        if (messages.isEmpty()) return false
        messages.forEach { recipient.sendMessage(it) }
        return true
    }
}