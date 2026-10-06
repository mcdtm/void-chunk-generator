package me.kvdpxne.vcg.api;

import java.util.Optional;

import org.bukkit.block.Biome;

public record VoidWorldConfig(
  Biome biome,
  boolean allowPrecipitation,
  boolean allowPassiveMobs,
  boolean allowHostileMobs,
  boolean allowNeutralMobs,
  boolean enableDayNightCycle,
  Optional<PlatformConfig> platform,
  Optional<SpawnOffset> spawnLocation
) {

  public VoidWorldConfig {
    platform = platform == null ? Optional.empty() : platform;
    spawnLocation = spawnLocation == null ? Optional.empty() : spawnLocation;
  }

  public static final VoidWorldConfig DEFAULT = new VoidWorldConfig(
    Biome.OCEAN, false, false, false, false, false,
    Optional.empty(), Optional.empty());


  public static final class Builder {

    private Biome biome = Biome.OCEAN;
    private boolean allowPrecipitation = false;
    private boolean allowPassiveMobs = false;
    private boolean allowHostileMobs = false;
    private boolean allowNeutralMobs = false;
    private boolean enableDayNightCycle = false;
    private PlatformConfig platform = null;
    private SpawnOffset spawnLocation = null;

    public Builder biome(final Biome biome) { this.biome = biome; return this; }
    public Builder precipitation(final boolean v) { this.allowPrecipitation = v; return this; }
    public Builder passiveMobs(final boolean v) { this.allowPassiveMobs = v; return this; }
    public Builder hostileMobs(final boolean v) { this.allowHostileMobs = v; return this; }
    public Builder neutralMobs(final boolean v) { this.allowNeutralMobs = v; return this; }
    public Builder dayNightCycle(final boolean v) { this.enableDayNightCycle = v; return this; }
    public Builder platform(final PlatformConfig v) { this.platform = v; return this; }
    public Builder spawnLocation(final SpawnOffset v) { this.spawnLocation = v; return this; }

    public VoidWorldConfig build() {
      return new VoidWorldConfig(
        this.biome,
        this.allowPrecipitation,
        this.allowPassiveMobs,
        this.allowHostileMobs,
        this.allowNeutralMobs,
        this.enableDayNightCycle,
        Optional.ofNullable(this.platform),
        Optional.ofNullable(this.spawnLocation));
    }
  }
}