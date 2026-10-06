package me.kvdpxne.vcg.api.event;

import me.kvdpxne.vcg.api.VoidWorldConfig;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class VoidWorldConfigRegisteredEvent extends Event {

  private static final HandlerList HANDLERS = new HandlerList();

  private final String worldName;
  private final VoidWorldConfig config;

  public VoidWorldConfigRegisteredEvent(final String worldName, final VoidWorldConfig config) {
    this.worldName = worldName;
    this.config = config;
  }

  public String getWorldName() {
    return this.worldName;
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