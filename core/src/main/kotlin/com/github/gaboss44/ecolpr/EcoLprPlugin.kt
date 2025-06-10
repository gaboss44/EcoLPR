package com.github.gaboss44.ecolpr

import com.github.gaboss44.ecolpr.libreforge.condition.ConditionCheckPermission
import com.github.gaboss44.ecolpr.libreforge.condition.ConditionHeldRanks
import com.github.gaboss44.ecolpr.libreforge.condition.ConditionIsHoldingSomeRank
import com.github.gaboss44.ecolpr.libreforge.condition.ConditionIsSatisfiedBySomeRoad
import com.github.gaboss44.ecolpr.libreforge.condition.ConditionSatisfactoryRoads
import com.github.gaboss44.ecolpr.libreforge.condition.ConditionTravelingRoads
import com.github.gaboss44.ecolpr.libreforge.effect.EffectSetCancelled
import com.github.gaboss44.ecolpr.libreforge.filter.FilterFromNoRank
import com.github.gaboss44.ecolpr.libreforge.filter.FilterFromRank
import com.github.gaboss44.ecolpr.libreforge.filter.FilterIsCancelled
import com.github.gaboss44.ecolpr.libreforge.filter.FilterRankTransitionSource
import com.github.gaboss44.ecolpr.libreforge.filter.FilterRankTransitionStatus
import com.github.gaboss44.ecolpr.libreforge.filter.FilterRankTransitionType
import com.github.gaboss44.ecolpr.libreforge.filter.FilterToNoRank
import com.github.gaboss44.ecolpr.libreforge.filter.FilterToRank
import com.github.gaboss44.ecolpr.libreforge.trigger.TriggerPostRankTransition
import com.github.gaboss44.ecolpr.libreforge.trigger.TriggerRankTransition
import com.github.gaboss44.ecolpr.rank.RankResolver
import com.github.gaboss44.ecolpr.transition.RankTransitionerImpl
import com.github.gaboss44.ecolpr.road.RoadResolver
import com.github.gaboss44.ecolpr.rank.RankCategory
import com.github.gaboss44.ecolpr.road.RoadCategory
import com.github.gaboss44.ecolpr.util.queryOptions
import com.willfp.libreforge.loader.LibreforgePlugin
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
import com.github.gaboss44.ecolpr.wrapper.LuckPermsRepository
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.loader.configs.ConfigCategory
import com.willfp.libreforge.triggers.Triggers
import net.luckperms.api.query.QueryOptions
import org.bukkit.plugin.ServicePriority

internal lateinit var plugin: EcoLprPlugin
    private set

class EcoLprPlugin : LibreforgePlugin() {
    init {
        plugin = this
    }

    lateinit var api: EcoLpr
        private set

    lateinit var repository: LuckPermsRepository
        private set

    lateinit var rankResolver: RankResolver
        private set

    lateinit var roadResolver: RoadResolver
        private set

    lateinit var rankTransitioner: RankTransitionerImpl
        private set

    lateinit var defaultQueryOptions: QueryOptions
        private set

    override fun loadConfigCategories(): List<ConfigCategory> {
        return listOf(RankCategory, RoadCategory)
    }

    override fun handleEnable() {
        val luckperms = getLuckPerms() ?: throw IllegalStateException("LuckPerms is not available...")
        repository = LuckPermsRepository(this, luckperms)

        rankResolver = RankResolver(this)
        roadResolver = RoadResolver(this)
        rankTransitioner = RankTransitionerImpl(this)

        api = EcoLprImpl(this)
        server.servicesManager.register(EcoLpr::class.java, api, this, ServicePriority.Normal)

        Effects.register(EffectSetCancelled)
        Conditions.register(ConditionCheckPermission(this))
        Conditions.register(ConditionHeldRanks)
        Conditions.register(ConditionIsHoldingSomeRank)
        Conditions.register(ConditionSatisfactoryRoads)
        Conditions.register(ConditionIsSatisfiedBySomeRoad)
        Conditions.register(ConditionTravelingRoads)
        Conditions.register(ConditionIsSatisfiedBySomeRoad)
        Triggers.register(TriggerRankTransition)
        Triggers.register(TriggerPostRankTransition)
        Filters.register(FilterIsCancelled)
        Filters.register(FilterFromRank)
        Filters.register(FilterFromNoRank)
        Filters.register(FilterToRank)
        Filters.register(FilterToNoRank)
        Filters.register(FilterRankTransitionType)
        Filters.register(FilterRankTransitionSource)
        Filters.register(FilterRankTransitionStatus)
    }

    override fun handleReload() {
        defaultQueryOptions = this.configYml.getSubsectionOrNull("default-query-options")?.queryOptions ?: repository.staticQueryOptions
    }

    private fun getLuckPerms(): LuckPerms? =
        Bukkit.getServicesManager().getRegistration(LuckPerms::class.java)?.provider
}