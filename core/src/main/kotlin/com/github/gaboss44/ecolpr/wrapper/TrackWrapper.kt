package com.github.gaboss44.ecolpr.wrapper

import com.github.gaboss44.ecolpr.EcoLprPlugin
import net.luckperms.api.track.Track

class TrackWrapper(
    plugin: EcoLprPlugin,
    track: Track
) : Wrapper<Track>(plugin, track) {
    val name = track.name
    val groups = track.groups

    /**
     * Checks if the track contains a specific group.
     */
    fun contains(group: GroupWrapper): Boolean {
        return obj.containsGroup(group.obj)
    }

    /**
     * Checks if the track contains a specific group.
     */
    fun contains(group: String): Boolean {
        return obj.containsGroup(group)
    }

    fun getNext(group: GroupWrapper): GroupWrapper? {
        val index = groups.indexOf(group.name)
        return if (index >= 0 && index < groups.size - 1) {
            plugin.repository.getGroup(groups[index + 1])
        } else {
            null
        }
    }

    fun getNext(group: String): GroupWrapper? {
        val index = groups.indexOf(group)
        return if (index >= 0 && index < groups.size - 1) {
            plugin.repository.getGroup(groups[index + 1])
        } else {
            null
        }
    }

    fun getPrevious(group: GroupWrapper): GroupWrapper? {
        val index = groups.indexOf(group.name)
        return if (index > 0) {
            plugin.repository.getGroup(groups[index - 1])
        } else {
            null
        }
    }

    fun getPrevious(group: String): GroupWrapper? {
        val index = groups.indexOf(group)
        return if (index > 0) {
            plugin.repository.getGroup(groups[index - 1])
        } else {
            null
        }
    }
}