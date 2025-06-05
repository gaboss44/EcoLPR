package com.github.gaboss44.ecolpr.event

import org.bukkit.command.CommandSender
import org.jetbrains.annotations.ApiStatus

/**
 * Interface that defines the contract for events that [CommandSender]s can trigger.
 *
 * @property sender The command sender who triggered the event. Can be null if the event was not triggered by a player or console.
 */
@ApiStatus.NonExtendable
interface CommandTriggeredEvent {
    val sender: CommandSender?
}