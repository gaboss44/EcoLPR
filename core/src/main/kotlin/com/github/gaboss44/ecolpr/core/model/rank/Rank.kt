package com.github.gaboss44.ecolpr.core.model.rank

import com.github.gaboss44.ecolpr.core.model.road.Road
import com.github.gaboss44.ecolpr.core.EcoLprPlugin
import com.github.gaboss44.ecolpr.core.exception.InvalidConfigException
import com.github.gaboss44.ecolpr.core.model.AbstractModel
import com.github.gaboss44.ecolpr.core.util.EcoPlaceholderUtil
import com.github.gaboss44.ecolpr.core.util.hasGroupNode
import com.willfp.eco.core.config.interfaces.Config
import net.luckperms.api.context.ContextSet
import org.bukkit.entity.Player

class Rank(
    plugin: EcoLprPlugin,
    id: String,
    config: Config
) : AbstractModel(
    plugin = plugin,
    id = id,
    config = config,
    category = "rank"
), com.github.gaboss44.ecolpr.api.model.rank.Rank {

    val proxy = ApiRank(this)

    override val group = config.getStringOrNull("group")?. also {
        if (Ranks.byGroup.containsKey(it)) throw InvalidConfigException(
            "Rank by group $it is already registered"
        )
    } ?: throw InvalidConfigException("A luckperms group is required")

    override val name = id

    override fun isGroupActive() = plugin.luckperms.isGroupLoaded(group)

    override val displayName = config.getString("display-name")

    override val description = config.getString("description")

    override fun isHeldBy(player: Player, contextSet: ContextSet?) =
        plugin.luckperms.getGroup(group)
            ?.let { plugin.luckperms.getUser(player).hasGroupNode(it, contextSet) }
            ?: false

    fun isHeldBy(player: Player, road: Road): Boolean {
        return this.isHeldBy(player, road.contextSet)
    }

    override fun onRegister() {
        Ranks.byGroup.put(group, this)
        initPlaceholders()
    }

    override fun onRemove() {
        Ranks.byGroup.remove(group)
        EcoPlaceholderUtil.unregister(plugin, placeholders)
    }
}
