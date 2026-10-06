package me.kvdpxne.vcg.api.event;

import me.kvdpxne.vcg.api.VoidWorldConfig;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class VoidWorldConfigRemovedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String worldName;
    private final VoidWorldConfig previousConfig;

    public VoidWorldConfigRemovedEvent(final String worldName, final VoidWorldConfig previousConfig) {
        this.worldName = worldName;
        this.previousConfig = previousConfig;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public VoidWorldConfig getPreviousConfig() {
        return this.previousConfig;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}