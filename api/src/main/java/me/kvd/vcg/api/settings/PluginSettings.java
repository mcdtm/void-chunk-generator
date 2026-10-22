package me.kvd.vcg.api.settings;

import java.util.Objects;
import me.kvd.vcg.api.rules.PlatformConfig;
import me.kvd.vcg.api.rules.FixedSpawnConfig;

/**
 * Immutable configuration of a single void world.
 * Only contains data relevant to chunk generation.
 */
public record PluginSettings(
  String biomeName,
  PlatformConfig platform,
  FixedSpawnConfig fixedSpawn,
  boolean metricsEnabled,
  boolean metricsExtendedEnabled
) {

  public PluginSettings {
    Objects.requireNonNull(biomeName, "biomeName");
  }

  public boolean platformEnabled() {
    return null != this.platform;
  }

  public boolean fixedSpawnEnabled() {
    return null != this.fixedSpawn;
  }
}