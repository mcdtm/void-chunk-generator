package me.kvd.vcg.api.event;

import java.util.Objects;
import me.kvd.vcg.internal.settings.PluginSettings;
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
    private PluginSettings config;
    private boolean cancelled;

    public Registered(final String worldName, final PluginSettings config) {
      this.worldName = Objects.requireNonNull(worldName, "worldName");
      this.config = Objects.requireNonNull(config, "config");
      this.cancelled = false;
    }

    public String getWorldName() {
      return this.worldName;
    }

    public PluginSettings getConfig() {
      return this.config;
    }

    public void setConfig(final PluginSettings config) {
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
    private final PluginSettings previousConfig;

    public Unregistered(final String worldName,
                        final PluginSettings previousConfig) {
      this.worldName = Objects.requireNonNull(worldName, "worldName");
      this.previousConfig = Objects.requireNonNull(previousConfig, "previousConfig");
    }

    public String getWorldName() {
      return this.worldName;
    }

    public PluginSettings getPreviousConfig() {
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

    private final PluginSettings previous;
    private final PluginSettings current;

    public Reloaded(final PluginSettings previous,
                    final PluginSettings current) {
      this.previous = Objects.requireNonNull(previous, "previous");
      this.current = Objects.requireNonNull(current, "current");
    }

    public PluginSettings getPrevious() {
      return this.previous;
    }

    public PluginSettings getCurrent() {
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