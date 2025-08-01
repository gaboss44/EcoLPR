package com.github.gaboss44.ecolpr.core

import com.willfp.eco.core.config.base.LangYml
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.util.StringUtils
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class EcoLprLangYml(plugin: EcoLprPlugin) : LangYml(plugin) {

    override fun clearInjectedPlaceholders() {
        super.clearInjectedPlaceholders()
        if (EcoLprSettings.isDebug()) plugin.logger.info(
            "Cleared injected placeholders in LangYml"
        )
    }

    fun string(reference: String): String = this
        .get("$KEY_MESSAGES.$reference")
        ?.toString()
        ?: run {
            if (EcoLprSettings.isDebug()) plugin.logger.warning(
                "Could not find message with the reference key '$reference'"
            )
            return ""
        }

    fun strings(reference: String): List<String> {
        val value = this.get("$KEY_MESSAGES.$reference")
        if (value is List<*>) {
            return value.filterIsInstance<String>()
        }
        if (EcoLprSettings.isDebug()) plugin.logger.warning(
            "Could not find messages with the reference key '$reference'"
        )
        return emptyList()
    }

    override fun getMessage(
        reference: String,
        option: StringUtils.FormatOption
    ) = this.getMessage(reference, option, null)

    fun getMessage(
        reference: String,
        replace: (StringBuilder.() -> Unit)? = null
    ) = this.getMessage(
        reference,
        StringUtils.FormatOption.WITH_PLACEHOLDERS,
        replace
    )

    fun getMessage(
        reference: String,
        option: StringUtils.FormatOption,
        replace: (StringBuilder.() -> Unit)? = null
    ): String {
        var string = this.string(reference).also { if (it.isEmpty()) return "" }
        if (option == StringUtils.FormatOption.WITH_PLACEHOLDERS)
            for (injection in placeholderInjections) {
                string = injection.tryTranslateQuickly(
                    string,
                    PlaceholderContext.EMPTY
                )
            }
        val replaced = replace?.let { StringBuilder(string).apply(it).toString() } ?: string
        return StringUtils.format(replaced, option)
    }

    fun getMessage(
        reference: String,
        player: Player,
        replace: (StringBuilder.() -> Unit)? = null
    ) = getMessage(
        reference,
        PlaceholderContext(player),
        replace
    )

    fun getMessage(
        reference: String,
        context: PlaceholderContext,
        replace: (StringBuilder.() -> Unit)? = null
    ): String {
        var string = this.string(reference).also { if (it.isEmpty()) return "" }
        for (injection in placeholderInjections) {
            string = injection.tryTranslateQuickly(string, context)
        }
        val replaced = replace?.let { StringBuilder(string).apply(it).toString() } ?: string
        return StringUtils.format(replaced, context)
    }

    fun getMessages(
        reference: String,
        replace: (StringBuilder.() -> Unit)? = null
    ) = this.getMessages(
        reference,
        StringUtils.FormatOption.WITH_PLACEHOLDERS,
        replace
    )

    fun getMessages(
        reference: String,
        option: StringUtils.FormatOption,
        replace: (StringBuilder.() -> Unit)? = null
    ): List<String> {
        val strings = this.strings(reference).also { if (it.isEmpty()) return emptyList() }
        return strings.map { original ->
            var string = original
            if (option == StringUtils.FormatOption.WITH_PLACEHOLDERS) {
                for (injection in placeholderInjections) {
                    string = injection.tryTranslateQuickly(string, PlaceholderContext.EMPTY)
                }
            }
            val replaced = replace?.let { StringBuilder(string).apply(it).toString() } ?: string
            StringUtils.format(replaced, option)
        }
    }

    fun getMessages(
        reference: String,
        player: Player,
        replace: (StringBuilder.() -> Unit)? = null
    ) = getMessages(
        reference,
        PlaceholderContext(player),
        replace
    )

    fun getMessages(
        reference: String,
        context: PlaceholderContext,
        replace: (StringBuilder.() -> Unit)? = null
    ): List<String> {
        val strings = this.strings(reference).also { if (it.isEmpty()) return emptyList() }
        return strings.map { original ->
            var string = original
            for (injection in placeholderInjections) {
                string = injection.tryTranslateQuickly(string, context)
            }
            val replaced = replace?.let { StringBuilder(string).apply(it).toString() } ?: string
            StringUtils.format(replaced, context)
        }
    }

    fun sendMessage(
        recipient: CommandSender,
        reference: String,
        replace: (StringBuilder.() -> Unit)? = null
    ) = if (recipient is Player) sendMessage(
        recipient,
        reference,
        PlaceholderContext(recipient),
        replace
    ) else sendMessage(
        recipient,
        reference,
        PlaceholderContext.EMPTY,
        replace
    )

    fun sendMessage(
        recipient: CommandSender,
        reference: String,
        player: Player,
        replace: (StringBuilder.() -> Unit)? = null
    ) = sendMessage(
        recipient,
        reference,
        PlaceholderContext(player),
        replace
    )

    fun sendMessage(
        recipient: CommandSender,
        reference: String,
        context: PlaceholderContext,
        replace: (StringBuilder.() -> Unit)? = null
    ): Boolean {
        val message = getMessage(reference, context, replace)
        if (message.isEmpty()) return false
        recipient.sendMessage(message)
        return true
    }

    fun sendMessages(
        recipient: CommandSender,
        reference: String,
        replace: (StringBuilder.() -> Unit)? = null
    ): Boolean = if (recipient is Player) sendMessages(
        recipient,
        reference,
        PlaceholderContext(recipient),
        replace
    ) else sendMessages(
        recipient,
        reference,
        PlaceholderContext.EMPTY,
        replace
    )

    fun sendMessages(
        recipient: CommandSender,
        reference: String,
        player: Player,
        replace: (StringBuilder.() -> Unit)? = null
    ): Boolean = sendMessages(
        recipient,
        reference,
        PlaceholderContext(player),
        replace
    )

    fun sendMessages(
        recipient: CommandSender,
        reference: String,
        context: PlaceholderContext,
        replace: (StringBuilder.() -> Unit)? = null
    ): Boolean {
        val messages = getMessages(reference, context, replace)
        if (messages.isEmpty()) return false
        messages.forEach { if (it.isNotEmpty()) recipient.sendMessage(it) }
        return true
    }
}