package me.kvd.vcg.api.settings;

import java.util.Objects;
import me.kvd.vcg.api.rules.FixedSpawnConfig;
import me.kvd.vcg.api.rules.PlatformConfig;

/**
 * Immutable top-level configuration of VoidChunkGenerator.
 */
public record PluginSettings(
  String biomeName,
  PlatformConfig platform,
  FixedSpawnConfig fixedSpawn,
  boolean metricsEnabled
) {

  /**
   * Compact constructor with validation.
   *
   * @throws NullPointerException     if {@code biomeName}, {@code fixedSpawn}
   *                                  or {@code platform} is null
   * @throws IllegalArgumentException if {@code biomeName} is blank
   */
  public PluginSettings {
    Objects.requireNonNull(biomeName, "biomeName");
    Objects.requireNonNull(fixedSpawn, "fixedSpawn");
    Objects.requireNonNull(platform, "platform");
    if (biomeName.isBlank()) {
      throw new IllegalArgumentException("biomeName must not be blank");
    }
  }

  /**
   * Returns a builder pre-populated with the defaults.
   *
   * @return a new builder, never null
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder for {@link PluginSettings}.
   */
  public static final class Builder {

    private String biomeName = "PLAINS";
    private PlatformConfig platform = PlatformConfig.disabled();
    private FixedSpawnConfig fixedSpawn = FixedSpawnConfig.disabled();
    private boolean metrics = true;

    private Builder() {
    }

    public Builder biomeName(final String biomeName) {
      this.biomeName = biomeName;
      return this;
    }

    public Builder platform(final PlatformConfig platform) {
      this.platform = platform;
      return this;
    }

    public Builder fixedSpawn(final FixedSpawnConfig fixedSpawn) {
      this.fixedSpawn = fixedSpawn;
      return this;
    }

    public Builder metrics(final boolean metrics) {
      this.metrics = metrics;
      return this;
    }

    /**
     * Builds the configuration.
     *
     * @return a new configuration, never null
     */
    public PluginSettings build() {
      return new PluginSettings(
        this.biomeName,
        this.platform,
        this.fixedSpawn,
        this.metrics
      );
    }
  }
}