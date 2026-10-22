package me.kvd.vcg.internal.utils;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import me.kvd.vcg.api.events.VoidWorldConfigEvent;
import me.kvd.vcg.api.settings.PluginSettings;
import org.bukkit.Bukkit;

/**
 * Resolves the effective configuration for a world.
 * Priority: per-world override > default from settings.properties.
 *
 * <p>Fires {@link VoidWorldConfigEvent.Registered} before storing an override
 * and {@link VoidWorldConfigEvent.Unregistered} after removal. Both events
 * run at most once per registration/removal - never on the chunk hot path.</p>
 */
public final class ConfigResolver {

  private final ConcurrentHashMap<String, PluginSettings> overrides;
  private volatile PluginSettings defaultConfig;
  private final Logger logger;

  public ConfigResolver(
    final PluginSettings defaultConfig,
    final Logger logger
  ) {
    Objects.requireNonNull(defaultConfig, "defaultConfig");
    Objects.requireNonNull(logger, "logger");
    this.overrides = new ConcurrentHashMap<>();
    this.defaultConfig = defaultConfig;
    this.logger = logger;
  }

  public void setDefault(final PluginSettings config) {
    this.defaultConfig = Objects.requireNonNull(config, "config");
  }

  public PluginSettings getDefault() {
    return this.defaultConfig;
  }

  /**
   * @return {@code true} if the override was stored;
   * {@code false} if a listener cancelled the registration
   */
  public boolean registerOverride(final String worldName, final PluginSettings config) {
    Objects.requireNonNull(worldName, "worldName");
    Objects.requireNonNull(config, "config");

    final var event = new VoidWorldConfigEvent.Registered(worldName, config);
    Bukkit.getPluginManager().callEvent(event);

    if (event.isCancelled()) {
      this.logger.info("Override registration for world '"
        + worldName + "' cancelled by a listener.");
      return false;
    }

    // A listener may have replaced the config - always store the final value.
    this.overrides.put(worldName, event.getConfig());
    this.logger.info("Registered override for world: " + worldName);
    return true;
  }

  public Optional<PluginSettings> removeOverride(final String worldName) {
    if (null == worldName) {
      return Optional.empty();
    }
    final var removed = this.overrides.remove(worldName);
    if (null == removed) {
      return Optional.empty();
    }

    this.logger.info("Removed override for world: " + worldName);
    Bukkit.getPluginManager().callEvent(
      new VoidWorldConfigEvent.Unregistered(worldName, removed));
    return Optional.of(removed);
  }

  public Optional<PluginSettings> findOverride(final String worldName) {
    if (null == worldName) {
      return Optional.empty();
    }
    return Optional.ofNullable(this.overrides.get(worldName));
  }

  public PluginSettings resolve(final String worldName) {
    if (null == worldName) {
      return this.defaultConfig;
    }
    return this.overrides.getOrDefault(worldName, this.defaultConfig);
  }

  public Set<String> overriddenWorlds() {
    return Set.copyOf(this.overrides.keySet());
  }
}