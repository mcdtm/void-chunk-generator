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
 * Resolves the configuration for a given world.
 * <p>
 * Holds per-world overrides in a {@link ConcurrentHashMap}.
 * Reads are lock-free and return immutable snapshots.
 */
public final class ConfigResolver {

  private final PluginSettings defaultConfig;
  private final ConcurrentHashMap<String, PluginSettings> overrides;
  private final Logger logger;

  /**
   * Creates a new resolver.
   *
   * @param defaultConfig the fallback configuration, must not be null
   * @param logger        the logger to use, must not be null
   * @throws NullPointerException if any argument is null
   */
  public ConfigResolver(final PluginSettings defaultConfig, final Logger logger) {
    this.defaultConfig = Objects.requireNonNull(defaultConfig, "defaultConfig");
    this.logger = Objects.requireNonNull(logger, "logger");
    this.overrides = new ConcurrentHashMap<>();
  }

  /**
   * Registers (or replaces) the per-world override.
   *
   * @param worldName the name of the world, must not be null
   * @param config    the configuration, must not be null
   * @throws NullPointerException if any argument is null
   */
  public void registerOverride(final String worldName, final PluginSettings config) {
    Objects.requireNonNull(worldName, "worldName");
    Objects.requireNonNull(config, "config");
    this.overrides.put(worldName, config);
    this.logger.fine(() -> "Registered override for world " + worldName);
  }

  /**
   * Removes the per-world override if present.
   *
   * @param worldName the name of the world, must not be null
   * @return an {@link Optional} containing the removed configuration,
   *         or empty if no override existed
   * @throws NullPointerException if {@code worldName} is null
   */
  public Optional<PluginSettings> removeOverride(final String worldName) {
    Objects.requireNonNull(worldName, "worldName");
    return Optional.ofNullable(this.overrides.remove(worldName));
  }

  /**
   * Returns the per-world override if present.
   *
   * @param worldName the name of the world, must not be null
   * @return an {@link Optional} containing the override, or empty if none exists
   * @throws NullPointerException if {@code worldName} is null
   */
  public Optional<PluginSettings> findOverride(final String worldName) {
    Objects.requireNonNull(worldName, "worldName");
    return Optional.ofNullable(this.overrides.get(worldName));
  }

  /**
   * Resolves the configuration for the given world, firing
   * {@link VoidWorldConfigEvent} so listeners may mutate it.
   *
   * @param worldName the name of the world, must not be null
   * @return the resolved configuration, never null
   * @throws NullPointerException if {@code worldName} is null
   */
  public PluginSettings resolve(final String worldName) {
    Objects.requireNonNull(worldName, "worldName");
    final var base = this.overrides.getOrDefault(worldName, this.defaultConfig);
    final var event = new VoidWorldConfigEvent(worldName, base);
    SafeExecution.run(this.logger,
      "Fire VoidWorldConfigEvent for world " + worldName,
      () -> Bukkit.getPluginManager().callEvent(event));
    return event.getConfig();
  }

  /**
   * Returns an immutable snapshot of all overridden world names.
   *
   * @return an immutable set of world names, never null
   */
  public Set<String> overriddenWorlds() {
    return Set.copyOf(this.overrides.keySet());
  }
}