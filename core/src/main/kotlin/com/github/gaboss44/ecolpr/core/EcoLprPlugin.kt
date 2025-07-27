package com.github.gaboss44.ecolpr.core

import com.github.gaboss44.ecolpr.api.EcoLpr
import com.github.gaboss44.ecolpr.core.command.OperatorCommandEcoLpr
import com.github.gaboss44.ecolpr.core.command.PlayerCommandRankUp
import com.github.gaboss44.ecolpr.core.libreforge.condition.ConditionPlayerHasRank
import com.github.gaboss44.ecolpr.core.libreforge.effect.EffectAscend
import com.github.gaboss44.ecolpr.core.libreforge.effect.EffectEgress
import com.github.gaboss44.ecolpr.core.libreforge.effect.EffectIngress
import com.github.gaboss44.ecolpr.core.libreforge.effect.EffectMigrate
import com.github.gaboss44.ecolpr.core.libreforge.effect.EffectPrestige
import com.github.gaboss44.ecolpr.core.libreforge.effect.EffectRankup
import com.github.gaboss44.ecolpr.core.libreforge.effect.EffectRecurse
import com.github.gaboss44.ecolpr.core.libreforge.effect.EffectSetCancelled
import com.github.gaboss44.ecolpr.core.libreforge.filter.FilterIsTransitionFromRank
import com.github.gaboss44.ecolpr.core.libreforge.filter.FilterTransitionFromRank
import com.github.gaboss44.ecolpr.core.libreforge.filter.FilterIsCancelled
import com.github.gaboss44.ecolpr.core.libreforge.filter.FilterTransitionSource
import com.github.gaboss44.ecolpr.core.libreforge.filter.FilterTransitionResultStatus
import com.github.gaboss44.ecolpr.core.libreforge.filter.FilterTransitionType
import com.github.gaboss44.ecolpr.core.libreforge.filter.FilterIsTransitionToRank
import com.github.gaboss44.ecolpr.core.libreforge.filter.FilterTransitionMode
import com.github.gaboss44.ecolpr.core.libreforge.filter.FilterTransitionToRank
import com.github.gaboss44.ecolpr.core.libreforge.trigger.TriggerTransitionAttempt
import com.github.gaboss44.ecolpr.core.libreforge.trigger.TriggerTransitionResult
import com.github.gaboss44.ecolpr.core.util.selfInject
import com.github.gaboss44.ecolpr.core.util.selfInjectPrefix
import com.github.gaboss44.ecolpr.core.luckperms.LuckpermsRepository
import com.github.gaboss44.ecolpr.core.model.rank.Ranks
import com.github.gaboss44.ecolpr.core.model.road.Roads
import com.github.gaboss44.ecolpr.core.transition.TransitionManager
import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.loader.LibreforgePlugin
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
import com.willfp.eco.core.config.base.LangYml
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.plugin.ServicePriority

internal lateinit var plugin: EcoLprPlugin
    private set

fun EcoPlugin.asLpr() = this as EcoLprPlugin

class EcoLprPlugin : LibreforgePlugin() {

    init { plugin = this }

    val isDebug get() = configYml.getBoolOrNull("debug") ?: false

    lateinit var api: EcoLpr private set

    lateinit var luckperms: LuckpermsRepository private set

    lateinit var transitionManager: TransitionManager private set

    override fun loadConfigCategories() = listOf(Ranks, Roads)

    override fun handleEnable() {
        val luckperms = getLuckperms()
        if (luckperms == null) {
            Bukkit.getPluginManager().disablePlugin(this)
            throw IllegalStateException("LuckPerms is not available...")
        }
        this.luckperms = luckperms
        this.transitionManager = TransitionManager(this)

        api = EcoLprApiProvider(this)
        server.servicesManager.register(
            EcoLpr::class.java,
            api,
            this,
            ServicePriority.Normal
        )

        Effects.register(EffectSetCancelled)
        Effects.register(EffectRankup(this))
        Effects.register(EffectIngress(this))
        Effects.register(EffectAscend(this))
        Effects.register(EffectPrestige(this))
        Effects.register(EffectEgress(this))
        Effects.register(EffectRecurse(this))
        Effects.register(EffectMigrate(this))

        Conditions.register(ConditionPlayerHasRank)

        Triggers.register(TriggerTransitionAttempt)
        Triggers.register(TriggerTransitionResult)

        Filters.register(FilterIsCancelled)
        Filters.register(FilterTransitionFromRank)
        Filters.register(FilterIsTransitionFromRank)
        Filters.register(FilterTransitionToRank)
        Filters.register(FilterIsTransitionToRank)
        Filters.register(FilterTransitionType)
        Filters.register(FilterTransitionSource)
        Filters.register(FilterTransitionResultStatus)
        Filters.register(FilterTransitionMode)
    }

    val langYml get() = super.langYml as EcoLprLangYml

    override fun createLangYml(): LangYml? {
        try {
            return EcoLprLangYml(this);
        } catch (e: NullPointerException) {
            this.logger.severe("Failed to create LangYml!")
            this.logger.severe("Please make sure that the 'lang.yml' file exists this plugin's folder!")
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return null;
        }
    }

    override fun handleReload() {
        this.langYml.apply {
            clearInjectedPlaceholders()
            selfInjectPrefix()
            selfInject("placeholders")
        }
    }

    override fun loadPluginCommands() = listOf(
        OperatorCommandEcoLpr(this),
        PlayerCommandRankUp(this)
    )

    private fun getLuckperms() = Bukkit
        .getServicesManager()
        .getRegistration(LuckPerms::class.java)
        ?.provider
        ?.let { LuckpermsRepository(it) }

}