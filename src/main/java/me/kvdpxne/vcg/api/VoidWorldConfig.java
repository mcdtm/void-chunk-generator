package me.kvdpxne.vcg.api;

import java.util.Objects;
import java.util.Optional;

import org.bukkit.block.Biome;

/**
 * Immutable configuration of a single void world.
 * Only contains data relevant to chunk generation.
 */
public record VoidWorldConfig(
  Biome biome,
  Optional<PlatformConfig> platform,
  Optional<SpawnOffset> spawnLocation
) {

  public VoidWorldConfig {
    Objects.requireNonNull(biome, "biome");
    platform = null == platform ? Optional.empty() : platform;
    spawnLocation = null == spawnLocation ? Optional.empty() : spawnLocation;
  }

  public static VoidWorldConfig defaults() {
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

    public Builder platform(final PlatformConfig platform) {
      this.platform = platform;
      return this;
    }

    public Builder spawnLocation(final SpawnOffset spawnLocation) {
      this.spawnLocation = spawnLocation;
      return this;
    }

    public VoidWorldConfig build() {
      return new VoidWorldConfig(
        this.biome,
        Optional.ofNullable(this.platform),
        Optional.ofNullable(this.spawnLocation));
    }
  }
}