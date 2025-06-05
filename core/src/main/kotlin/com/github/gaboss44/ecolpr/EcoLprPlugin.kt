package com.github.gaboss44.ecolpr

import com.github.gaboss44.ecolpr.libreforge.condition.ConditionHoldsRank
import com.github.gaboss44.ecolpr.rank.RankResolver
import com.github.gaboss44.ecolpr.rank.RankTransitioner
import com.github.gaboss44.ecolpr.rank.RoadResolver
import com.github.gaboss44.ecolpr.rank.Ranks
import com.github.gaboss44.ecolpr.rank.Roads
import com.github.gaboss44.ecolpr.util.queryOptions
import com.willfp.libreforge.loader.LibreforgePlugin
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
import com.github.gaboss44.ecolpr.wrapper.LuckPermsRepository
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.loader.configs.ConfigCategory
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

    lateinit var transitioner: RankTransitioner
        private set

    lateinit var defaultQueryOptions: QueryOptions
        private set

    private var debug: Boolean = false
    val shouldDebug: Boolean get() = debug

    private var selectMostSpecificRoad = true
    val shouldSelectMostSpecificRoad: Boolean get() = selectMostSpecificRoad

    override fun loadConfigCategories(): List<ConfigCategory> {
        return listOf(Ranks, Roads)
    }

    override fun handleLoad() {
        val luckperms = getLuckPerms() ?: throw IllegalStateException("LuckPerms is not available...")

        repository = LuckPermsRepository(this, luckperms)
    }

    override fun handleEnable() {
        rankResolver = RankResolver(this)
        roadResolver = RoadResolver(this)
        transitioner = RankTransitioner(this)

        api = EcoLprImpl(this)
        server.servicesManager.register(EcoLpr::class.java, api, this, ServicePriority.Normal)

        Conditions.register(ConditionHoldsRank)
    }

    override fun handleReload() {
        defaultQueryOptions = this.configYml.getSubsectionOrNull("default-query-options")?.queryOptions ?: repository.staticQueryOptions
        debug = this.configYml.getBool("debug")
        selectMostSpecificRoad = this.configYml.getBoolOrNull("select-most-specific-road") ?: true
    }

    private fun getLuckPerms(): LuckPerms? =
        Bukkit.getServicesManager().getRegistration(LuckPerms::class.java)?.provider
}