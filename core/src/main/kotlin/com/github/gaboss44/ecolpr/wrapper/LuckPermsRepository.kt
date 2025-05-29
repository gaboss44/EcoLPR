package com.github.gaboss44.ecolpr.wrapper

import com.github.gaboss44.ecolpr.EcoLprPlugin
import net.luckperms.api.LuckPerms
import java.util.UUID

class LuckPermsRepository(
    plugin: EcoLprPlugin,
    luckperms: LuckPerms
) : Wrapper<LuckPerms>(plugin, luckperms) {
    fun getTrack(name: String): TrackWrapper? =
        obj.trackManager.getTrack(name) ?.let { TrackWrapper(plugin, it) }

    fun getGroup(name: String): GroupWrapper? =
        obj.groupManager.getGroup(name) ?.let { GroupWrapper(plugin, it) }

    fun getUser(uniqueId: UUID): UserWrapper? =
        obj.userManager.getUser(uniqueId) ?.let { UserWrapper(plugin, it) }


}