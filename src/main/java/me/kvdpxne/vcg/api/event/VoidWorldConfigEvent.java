package me.kvdpxne.vcg.api.event;

import java.util.Objects;
import me.kvdpxne.vcg.api.VoidWorldConfig;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired after {@code reloadDefaultConfig()} succeeds.
 * Per-world overrides are untouched by a reload.
 */
public final class VoidWorldConfigEvent {

  public static final class Registered extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String worldName;
    private VoidWorldConfig config;
    private boolean cancelled;

    public Registered(final String worldName, final VoidWorldConfig config) {
      this.worldName = Objects.requireNonNull(worldName, "worldName");
      this.config = Objects.requireNonNull(config, "config");
      this.cancelled = false;
    }

    public String getWorldName() {
      return this.worldName;
    }

    public VoidWorldConfig getConfig() {
      return this.config;
    }

    public void setConfig(final VoidWorldConfig config) {
      this.config = Objects.requireNonNull(config, "config");
    }

    @Override
    public boolean isCancelled() {
      return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
      this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
      return HANDLERS;
    }

    public static HandlerList getHandlerList() {
      return HANDLERS;
    }
  }

  public static final class Unregistered extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String worldName;
    private final VoidWorldConfig previousConfig;

    public Unregistered(final String worldName,
                        final VoidWorldConfig previousConfig) {
      this.worldName = Objects.requireNonNull(worldName, "worldName");
      this.previousConfig = Objects.requireNonNull(previousConfig, "previousConfig");
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

  public static final class Reloaded extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final VoidWorldConfig previous;
    private final VoidWorldConfig current;

    public Reloaded(final VoidWorldConfig previous,
                    final VoidWorldConfig current) {
      this.previous = Objects.requireNonNull(previous, "previous");
      this.current = Objects.requireNonNull(current, "current");
    }

    public VoidWorldConfig getPrevious() {
      return this.previous;
    }

    public VoidWorldConfig getCurrent() {
      return this.current;
    }

    @Override
    public HandlerList getHandlers() {
      return HANDLERS;
    }

    public static HandlerList getHandlerList() {
      return HANDLERS;
    }
  }
}