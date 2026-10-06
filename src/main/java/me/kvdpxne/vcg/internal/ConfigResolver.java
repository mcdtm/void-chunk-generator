package me.kvdpxne.vcg.internal;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import me.kvdpxne.vcg.api.event.VoidWorldConfigEvent;
import org.bukkit.Bukkit;

import me.kvdpxne.vcg.api.VoidWorldConfig;

/**
 * Resolves the effective configuration for a world.
 * Priority: per-world override > default from settings.properties.
 *
 * <p>Fires {@link VoidWorldConfigEvent.Registered} before storing an override
 * and {@link VoidWorldConfigEvent.Unregistered} after removal. Both events
 * run at most once per registration/removal - never on the chunk hot path.</p>
 */
public final class ConfigResolver {

  private final ConcurrentHashMap<String, VoidWorldConfig> overrides;
  private volatile VoidWorldConfig defaultConfig;
  private final Logger logger;

  public ConfigResolver(
    final VoidWorldConfig defaultConfig,
    final Logger logger
  ) {
    Objects.requireNonNull(defaultConfig, "defaultConfig");
    Objects.requireNonNull(logger, "logger");
    this.overrides = new ConcurrentHashMap<>();
    this.defaultConfig = defaultConfig;
    this.logger = logger;
  }

  public void setDefault(final VoidWorldConfig config) {
    this.defaultConfig = Objects.requireNonNull(config, "config");
  }

  public VoidWorldConfig getDefault() {
    return this.defaultConfig;
  }

  /**
   * @return {@code true} if the override was stored;
   * {@code false} if a listener cancelled the registration
   */
  public boolean registerOverride(final String worldName, final VoidWorldConfig config) {
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

  public Optional<VoidWorldConfig> removeOverride(final String worldName) {
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

  public Optional<VoidWorldConfig> findOverride(final String worldName) {
    if (null == worldName) {
      return Optional.empty();
    }
    return Optional.ofNullable(this.overrides.get(worldName));
  }

  public VoidWorldConfig resolve(final String worldName) {
    if (null == worldName) {
      return this.defaultConfig;
    }
    return this.overrides.getOrDefault(worldName, this.defaultConfig);
  }

  public Set<String> overriddenWorlds() {
    return Set.copyOf(this.overrides.keySet());
  }
}