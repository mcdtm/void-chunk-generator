package me.kvd.vcg.versions.v26;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import me.kvd.vcg.api.settings.PluginSettings;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;

/**
 * Supplies a single biome for every position in a void world.
 */
public final class VoidBiomeProvider extends BiomeProvider {

  private final Biome biome;
  private final Logger logger;

  /**
   * Creates a new provider.
   *
   * @param settings the plugin settings, must not be null
   * @param logger   the logger to use, must not be null
   * @throws NullPointerException if any argument is null
   */
  public VoidBiomeProvider(
    final PluginSettings settings,
    final Logger logger
  ) {
    Objects.requireNonNull(settings, "settings");
    this.logger = Objects.requireNonNull(logger, "logger");
    this.biome = resolveBiome(settings.biomeName());
  }

  @Override
  public Biome getBiome(
    final WorldInfo worldInfo,
    final int x,
    final int y,
    final int z
  ) {
    return this.biome;
  }

  @Override
  public List<Biome> getBiomes(WorldInfo worldInfo) {
    return List.of(this.biome);
  }

  private Biome resolveBiome(final String name) {
    try {
      return Biome.valueOf(name.toUpperCase());
    } catch (final IllegalArgumentException e) {
      this.logger.warning(() -> "Unknown biome '" + name
        + "', falling back to PLAINS");
      return Biome.PLAINS;
    }
  }
}