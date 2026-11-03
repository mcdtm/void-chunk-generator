package me.kvd.vcg.api;

import java.util.Optional;
import java.util.Set;
import me.kvd.vcg.api.settings.PluginSettings;

/**
 * Public API of VoidChunkGenerator.
 * <p>
 * The plugin operates standalone as well - without any consumer of this API,
 * every void world receives the same config from {@code settings.properties}.
 */
public interface VoidChunkGeneratorApi {

  /**
   * Incremented on every breaking change.
   */
  String API_VERSION = ApiVersion.VERSION;

  /**
   * Registers a per-world override.
   * Must be called before the world is created.
   *
   * @param worldName the name of the world, must not be null or blank
   * @param config    the per-world configuration, must not be null
   * @throws NullPointerException     if {@code worldName} or {@code config} is null
   * @throws IllegalArgumentException if {@code worldName} is blank
   */
  void registerWorldConfig(String worldName, PluginSettings config);

  /**
   * Removes a per-world override if present.
   *
   * @param worldName the name of the world, must not be null
   * @return {@code true} if an override was removed, {@code false} otherwise
   * @throws NullPointerException if {@code worldName} is null
   */
  boolean unregisterWorldConfig(String worldName);

  /**
   * Returns the per-world override if present.
   *
   * @param worldName the name of the world, must not be null
   * @return an {@link Optional} containing the override, or empty if none exists
   * @throws NullPointerException if {@code worldName} is null
   */
  Optional<PluginSettings> findWorldConfig(String worldName);

  /**
   * Returns the per-world override or the default from settings.properties.
   *
   * @param worldName the name of the world, must not be null
   * @return the resolved configuration, never null
   * @throws NullPointerException if {@code worldName} is null
   */
  PluginSettings resolveConfig(String worldName);

  /**
   * Immutable snapshot of all overridden world names.
   *
   * @return an immutable set of world names, never null
   */
  Set<String> getOverriddenWorlds();
}