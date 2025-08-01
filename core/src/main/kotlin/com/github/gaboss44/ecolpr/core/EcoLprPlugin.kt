package com.github.gaboss44.ecolpr.core

import com.github.gaboss44.ecolpr.api.EcoLpr
import com.github.gaboss44.ecolpr.core.command.CommandEcoLpr
import com.github.gaboss44.ecolpr.core.command.CommandPrestige
import com.github.gaboss44.ecolpr.core.command.CommandRankup
import com.github.gaboss44.ecolpr.core.libreforge.condition.ConditionHasRank
import com.github.gaboss44.ecolpr.core.libreforge.condition.ConditionOnRoad
import com.github.gaboss44.ecolpr.core.libreforge.condition.ConditionPrestigeLevelAtLeast
import com.github.gaboss44.ecolpr.core.libreforge.condition.ConditionPrestigeLevelAtMost
import com.github.gaboss44.ecolpr.core.libreforge.condition.ConditionPrestigeLevelEquals
import com.github.gaboss44.ecolpr.core.libreforge.condition.ConditionPrestigeLevelGreaterThan
import com.github.gaboss44.ecolpr.core.libreforge.condition.ConditionPrestigeLevelLowerThan
import com.github.gaboss44.ecolpr.core.libreforge.effect.EffectAscend
import com.github.gaboss44.ecolpr.core.libreforge.effect.EffectEgress
import com.github.gaboss44.ecolpr.core.libreforge.effect.EffectIngress
import com.github.gaboss44.ecolpr.core.libreforge.effect.EffectMigrate
import com.github.gaboss44.ecolpr.core.libreforge.effect.EffectPrestige
import com.github.gaboss44.ecolpr.core.libreforge.effect.EffectRankup
import com.github.gaboss44.ecolpr.core.libreforge.effect.EffectRecurse
import com.github.gaboss44.ecolpr.core.libreforge.effect.EffectSetCancelled
import com.github.gaboss44.ecolpr.core.libreforge.filter.FilterTransitionFromRank
import com.github.gaboss44.ecolpr.core.libreforge.filter.FilterIsCancelled
import com.github.gaboss44.ecolpr.core.libreforge.filter.FilterPrestigeLevelAtLeast
import com.github.gaboss44.ecolpr.core.libreforge.filter.FilterPrestigeLevelAtMost
import com.github.gaboss44.ecolpr.core.libreforge.filter.FilterPrestigeLevelEquals
import com.github.gaboss44.ecolpr.core.libreforge.filter.FilterPrestigeLevelGreaterThan
import com.github.gaboss44.ecolpr.core.libreforge.filter.FilterPrestigeLevelLowerThan
import com.github.gaboss44.ecolpr.core.libreforge.filter.FilterPrestigeRoad
import com.github.gaboss44.ecolpr.core.libreforge.filter.FilterTransitionSource
import com.github.gaboss44.ecolpr.core.libreforge.filter.FilterTransitionStatus
import com.github.gaboss44.ecolpr.core.libreforge.filter.FilterTransitionType
import com.github.gaboss44.ecolpr.core.libreforge.filter.FilterTransitionMode
import com.github.gaboss44.ecolpr.core.libreforge.filter.FilterTransitionRoad
import com.github.gaboss44.ecolpr.core.libreforge.filter.FilterTransitionToRank
import com.github.gaboss44.ecolpr.core.libreforge.trigger.TriggerTransitionAttempt
import com.github.gaboss44.ecolpr.core.libreforge.trigger.TriggerTransitionResult
import com.github.gaboss44.ecolpr.core.util.selfInject
import com.github.gaboss44.ecolpr.core.util.selfInjectPrefix
import com.github.gaboss44.ecolpr.core.luckperms.LuckpermsRepository
import com.github.gaboss44.ecolpr.core.model.rank.Ranks
import com.github.gaboss44.ecolpr.core.model.road.Roads
import com.github.gaboss44.ecolpr.core.transition.TransitionManager
import com.github.gaboss44.ecolpr.core.util.ApiRegistrationUtil
import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.loader.LibreforgePlugin
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
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

    lateinit var api: EcoLpr private set

    lateinit var luckperms: LuckpermsRepository private set

    lateinit var transitionManager: TransitionManager private set

    override fun loadConfigCategories() = listOf(Ranks, Roads)

    override fun handleEnable() {
        val luckperms = initLuckperms()
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

        ApiRegistrationUtil.register(api)

        Effects.register(EffectSetCancelled)

        Effects.register(EffectRankup(this))

        Effects.register(EffectIngress(this))
        Effects.register(EffectAscend(this))

        Effects.register(EffectPrestige(this))

        Effects.register(EffectEgress(this))
        Effects.register(EffectRecurse(this))
        Effects.register(EffectMigrate(this))

        Conditions.register(ConditionHasRank)
        Conditions.register(ConditionOnRoad)

        Conditions.register(ConditionPrestigeLevelEquals)
        Conditions.register(ConditionPrestigeLevelAtLeast)
        Conditions.register(ConditionPrestigeLevelAtMost)
        Conditions.register(ConditionPrestigeLevelGreaterThan)
        Conditions.register(ConditionPrestigeLevelLowerThan)

        Triggers.register(TriggerTransitionAttempt)
        Triggers.register(TriggerTransitionResult)

        Filters.register(FilterIsCancelled)

        Filters.register(FilterTransitionFromRank)
        Filters.register(FilterTransitionToRank)

        Filters.register(FilterTransitionType)
        Filters.register(FilterTransitionSource)
        Filters.register(FilterTransitionStatus)
        Filters.register(FilterTransitionMode)

        Filters.register(FilterPrestigeLevelEquals)
        Filters.register(FilterPrestigeLevelAtLeast)
        Filters.register(FilterPrestigeLevelAtMost)
        Filters.register(FilterPrestigeLevelGreaterThan)
        Filters.register(FilterPrestigeLevelLowerThan)

        Filters.register(FilterTransitionRoad)
        Filters.register(FilterPrestigeRoad)
    }

    val langYml get() = super.langYml as EcoLprLangYml

    override fun createLangYml(): EcoLprLangYml? {
        try {
            return EcoLprLangYml(this)
        } catch (e: NullPointerException) {
            this.logger.severe("Failed to create LangYml!")
            this.logger.severe("Please make sure that the 'lang.yml' file exists this plugin's folder!")
            e.printStackTrace()
            Bukkit.getPluginManager().disablePlugin(this)
            return null
        }
    }

    override fun handleReload() {
        super.handleDisable()
        this.langYml.apply {
            clearInjectedPlaceholders()
            selfInjectPrefix()
            selfInject("placeholders")
        }
        EcoLprSettings.setDebug(configYml.getBool("debug"))
    }

    override fun handleDisable() {
        super.handleDisable()
        ApiRegistrationUtil.unregister()
    }

    override fun loadPluginCommands() = listOf(
        CommandEcoLpr(this),
        CommandRankup(this),
        CommandPrestige(this)
    )

    private fun initLuckperms() = Bukkit
        .getServicesManager()
        .getRegistration(LuckPerms::class.java)
        ?.provider
        ?.let { LuckpermsRepository(it) }
}
