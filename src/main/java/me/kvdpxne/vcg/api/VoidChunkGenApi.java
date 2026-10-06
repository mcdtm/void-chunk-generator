package me.kvdpxne.vcg.api;

import java.util.Optional;
import java.util.Set;

/**
 * Public API of VoidChunkGenerator.
 * The plugin operates standalone as well - without any consumer of this API,
 * every void world receives the same config from settings.properties.
 */
public interface VoidChunkGenApi {

  /**
   * Incremented on every breaking change.
   */
  int API_VERSION = 1;

  /**
   * Registers a per-world override.
   * Must be called before the world is created.
   *
   * @throws NullPointerException     if {@code worldName} or {@code config} is null
   * @throws IllegalArgumentException if {@code worldName} is blank
   */
  void registerWorldConfig(String worldName, VoidWorldConfig config);

  /**
   * Removes a per-world override if present.
   */
  boolean unregisterWorldConfig(String worldName);

  /**
   * Returns the per-world override if present.
   */
  Optional<VoidWorldConfig> findWorldConfig(String worldName);

  /**
   * Returns the per-world override or the default from settings.properties.
   */
  VoidWorldConfig resolveConfig(String worldName);

  /**
   * Immutable snapshot of all overridden world names.
   */
  Set<String> getOverriddenWorlds();
}