package com.github.gaboss44.ecolpr.core.model.road

import com.github.gaboss44.ecolpr.api.model.road.PrestigeType
import com.github.gaboss44.ecolpr.core.model.rank.Rank
import com.github.gaboss44.ecolpr.core.EcoLprPlugin
import com.github.gaboss44.ecolpr.core.exception.InvalidConfigException
import com.github.gaboss44.ecolpr.core.model.AbstractModel
import com.github.gaboss44.ecolpr.core.model.rank.Ranks
import com.github.gaboss44.ecolpr.core.util.EcoPlaceholderUtil
import com.github.gaboss44.ecolpr.core.util.getInheritedGroups
import com.github.gaboss44.ecolpr.core.util.nextOrNull
import com.github.gaboss44.ecolpr.core.util.parseContextSet
import com.github.gaboss44.ecolpr.core.util.placeholder.ContextualPlaceholder
import com.github.gaboss44.ecolpr.core.util.previousOrNull
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.util.toNumeral
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.executors.impl.NormalExecutorFactory
import org.bukkit.entity.Player

class Road(
    plugin: EcoLprPlugin,
    id: String,
    config: Config
) : AbstractModel(
    plugin = plugin,
    id = id,
    config = config,
    category = "road"
), com.github.gaboss44.ecolpr.api.model.road.Road {

    override fun initPlaceholders() {
        super.initPlaceholders()
        placeholders.apply {
            add(
                ContextualPlaceholder(
                    plugin,
                    prefix + "_prestige_level"
                ) { _, context ->
                    val player = context.player ?: return@ContextualPlaceholder null
                    getPrestigeLevel(player).toString()
                }.register()
            )
            add(
                ContextualPlaceholder(
                    plugin,
                    prefix + "_prestige_level_numeral"
                ) { _, context ->
                    val player = context.player ?: return@ContextualPlaceholder null
                    getPrestigeLevel(player).toNumeral()
                }.register()
            )
            add(
                ContextualPlaceholder(
                    plugin,
                    prefix + "_current_rank"
                ) { _, context ->
                    val player = context.player ?: return@ContextualPlaceholder null
                    getCurrentRank(player)?.id ?: ""
                }.register()
            )
            add(
                ContextualPlaceholder(
                    plugin,
                    prefix + "_current_rank_display_name"
                ) { _, context ->
                    val player = context.player ?: return@ContextualPlaceholder null
                    getCurrentRank(player)?.getFormattedDisplayName(player) ?: ""
                }.register()
            )
            add(
                ContextualPlaceholder(
                    plugin,
                    prefix + "_current_rank_description"
                ) { _, context ->
                    val player = context.player ?: return@ContextualPlaceholder null
                    getCurrentRank(player)?.getFormattedDescription(player) ?: ""
                }.register()
            )
            add(
                ContextualPlaceholder(
                    plugin,
                    prefix + "_prev_rank"
                ) { _, context ->
                    val player = context.player ?: return@ContextualPlaceholder null
                    getPrevRank(player)?.id ?: ""
                }.register()
            )
            add(
                ContextualPlaceholder(
                    plugin,
                    prefix + "_prev_rank_display_name"
                ) { _, context ->
                    val player = context.player ?: return@ContextualPlaceholder null
                    getPrevRank(player)?.getFormattedDisplayName(player) ?: ""
                }.register()
            )
            add(
                ContextualPlaceholder(
                    plugin,
                    prefix + "_prev_rank_description"
                ) { _, context ->
                    val player = context.player ?: return@ContextualPlaceholder null
                    getPrevRank(player)?.getFormattedDescription(player) ?: ""
                }.register()
            )
            add(
                ContextualPlaceholder(
                    plugin,
                    prefix + "_next_rank"
                ) { _, context ->
                    val player = context.player ?: return@ContextualPlaceholder null
                    getNextRank(player)?.id ?: ""
                }.register()
            )
            add(
                ContextualPlaceholder(
                    plugin,
                    prefix + "_next_rank_display_name"
                ) { _, context ->
                    val player = context.player ?: return@ContextualPlaceholder null
                    getNextRank(player)?.getFormattedDisplayName(player) ?: ""
                }.register()
            )
            add(
                ContextualPlaceholder(
                    plugin,
                    prefix + "_next_rank_description"
                ) { _, context ->
                    val player = context.player ?: return@ContextualPlaceholder null
                    getNextRank(player)?.getFormattedDescription(player) ?: ""
                }.register()
            )
        }
    }

    val proxy = ApiRoad(this)

    val prestigeKey = PersistentDataKey(
        plugin.createNamespacedKey(prefix + "_prestige_level"),
        PersistentDataKeyType.INT, 0
    )

    val transitionAttemptEffects = Effects.compileChain(
        config.getSubsections("transition-attempt-effects"),
        NormalExecutorFactory.create(),
        ViolationContext(plugin,"Road $id transition attempt effects")
    )

    val transitionResultEffects = Effects.compileChain(
        config.getSubsections("transition-result-effects"),
        NormalExecutorFactory.create(),
        ViolationContext(plugin,"Road $id transition result effects")
    )

    override val name = id

    override val ranks: List<Rank> = config.getStrings("ranks").map {
        Ranks.getByID(it) ?: throw InvalidConfigException(
            "Rank $it in 'ranks' section is not loaded"
        )
    }

    override fun getRanks(player: Player) = plugin.luckperms
        .getUser(player)
        .getInheritedGroups(contextSet)
        .mapNotNull { Ranks.getByGroup(it) }
        .filter(ranks::contains)

    fun getCurrentRank(player: Player): Rank? {
        val ranks = this.getRanks(player)
        if (ranks.size > 1) return null
        if (ranks.isEmpty()) return null
        return ranks[0]
    }
    
    fun getPrevRank(player: Player): Rank? {
        val ranks = this.getRanks(player)
        if (ranks.size > 1) return null
        if (ranks.isEmpty()) return null
        return this.ranks.previousOrNull(ranks[0])
    }
    
    fun getNextRank(player: Player): Rank? {
        val ranks = this.getRanks(player)
        if (ranks.size > 1) return null
        if (ranks.isEmpty()) return null
        return this.ranks.nextOrNull(ranks[0])
    }

    override fun getPrestigeLevel(player: Player) = player.profile.read(prestigeKey)

    fun setPrestigeLevel(player: Player, level: Int) = player.profile.write(prestigeKey, level)

    override val prestigeType = config.getStringOrNull("prestige-type")?.let { PrestigeType[it] }

    override val displayName = config.getString("display-name")

    override val description = config.getString("description")

    override val isHidden = config.getBoolOrNull("hidden") ?: false

    override val hideBypassPermission = config.getStringOrNull("hide-bypass-permission")

    override val prestigeTarget = config.getStringOrNull("prestige-target")

    val prestigeRoad get() = Roads[prestigeTarget]

    override val contextSet = config.getSubsectionOrNull("context-set")
        ?. let { parseContextSet(it) }
        ?: plugin.luckperms.staticQueryOptions.context()

    override fun onRegister() {
        initPlaceholders()
    }

    override fun onRemove() {
        EcoPlaceholderUtil.unregister(plugin, placeholders)
    }
}
