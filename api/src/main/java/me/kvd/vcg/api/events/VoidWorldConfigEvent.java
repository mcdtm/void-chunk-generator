package me.kvd.vcg.api.events;

import java.util.Objects;
import me.kvd.vcg.api.settings.PluginSettings;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when the plugin resolves the configuration for a world.
 * <p>
 * Listeners may mutate the configuration before the generator reads it.
 */
public final class VoidWorldConfigEvent extends Event {

  private static final HandlerList HANDLERS = new HandlerList();

  private final String worldName;
  private PluginSettings config;

  /**
   * Creates a new event instance.
   *
   * @param worldName the name of the world, must not be null
   * @param config    the resolved configuration, must not be null
   * @throws NullPointerException if any argument is null
   */
  public VoidWorldConfigEvent(final String worldName, final PluginSettings config) {
    this.worldName = Objects.requireNonNull(worldName, "worldName");
    this.config = Objects.requireNonNull(config, "config");
  }

  /**
   * Returns the name of the world.
   *
   * @return the world name, never null
   */
  public String getWorldName() {
    return this.worldName;
  }

  /**
   * Returns the resolved configuration.
   *
   * @return the configuration, never null
   */
  public PluginSettings getConfig() {
    return this.config;
  }

  /**
   * Overrides the configuration used by the generator.
   *
   * @param config the new configuration, must not be null
   * @throws NullPointerException if {@code config} is null
   */
  public void setConfig(final PluginSettings config) {
    this.config = Objects.requireNonNull(config, "config");
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  /**
   * Returns the static handler list for this event type.
   *
   * @return the handler list, never null
   */
  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}