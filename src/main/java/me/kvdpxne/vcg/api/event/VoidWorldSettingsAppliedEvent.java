package me.kvdpxne.vcg.api.event;

import me.kvdpxne.vcg.api.VoidWorldConfig;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class VoidWorldSettingsAppliedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final World world;
    private final VoidWorldConfig config;

    public VoidWorldSettingsAppliedEvent(final World world, final VoidWorldConfig config) {
        this.world = world;
        this.config = config;
    }

    public World getWorld() {
        return this.world;
    }

    public VoidWorldConfig getConfig() {
        return this.config;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}