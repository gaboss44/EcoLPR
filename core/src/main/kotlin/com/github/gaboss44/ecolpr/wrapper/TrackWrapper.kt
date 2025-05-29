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
        return groups.contains(group.name)
    }
}