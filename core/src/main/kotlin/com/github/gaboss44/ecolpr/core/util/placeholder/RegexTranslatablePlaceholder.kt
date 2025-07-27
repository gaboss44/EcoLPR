package com.github.gaboss44.ecolpr.core.util.placeholder

import com.willfp.eco.core.placeholder.Placeholder
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import java.util.regex.Matcher

interface RegexTranslatablePlaceholder : Placeholder {
    override fun tryTranslateQuickly(
        text: String,
        context: PlaceholderContext
    ): String {
        val matcher = pattern.matcher(text)
        val buffer = StringBuffer()

        while (matcher.find()) {
            val args = matcher.group()
            val replacement = getValue(args, context) ?: ""
            matcher.appendReplacement(
                buffer,
                Matcher.quoteReplacement(
                    replacement
                )
            )
        }

        matcher.appendTail(buffer)
        return buffer.toString()
    }
}