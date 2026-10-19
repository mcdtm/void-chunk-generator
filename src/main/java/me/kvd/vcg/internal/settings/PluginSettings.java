package me.kvd.vcg.internal.settings;

import java.util.Objects;
import java.util.Optional;

import me.kvd.vcg.api.PlatformConfig;
import me.kvd.vcg.api.SpawnOffset;
import org.bukkit.block.Biome;

/**
 * Immutable configuration of a single void world.
 * Only contains data relevant to chunk generation.
 */
public record PluginSettings(
  Biome biome,
  Optional<PlatformConfig> platform,
  Optional<SpawnOffset> spawnLocation,
  boolean metricsEnabled,
  boolean metricsExtendedEnabled
) {

  public PluginSettings {
    Objects.requireNonNull(biome, "biome");
    platform = null == platform ? Optional.empty() : platform;
    spawnLocation = null == spawnLocation ? Optional.empty() : spawnLocation;
  }

  public static PluginSettings defaults() {
    return builder().build();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private Biome biome = Biome.OCEAN;
    private PlatformConfig platform = null;
    private SpawnOffset spawnLocation = null;

    private Builder() {
    }

    public Builder biome(final Biome biome) {
      this.biome = biome;
      return this;
    }

    public Builder platform(final boolean expression, final PlatformConfig platform) {
      this.platform = platform;
      return this;
    }

    public Builder spawnOffset(final boolean expression, final SpawnOffset spawnLocation) {
      this.spawnLocation = spawnLocation;
      return this;
    }

    public PluginSettings build() {
      return new PluginSettings(
        this.biome,
        Optional.ofNullable(this.platform),
        Optional.ofNullable(this.spawnLocation),
        true,
        true
      );
    }
  }
}