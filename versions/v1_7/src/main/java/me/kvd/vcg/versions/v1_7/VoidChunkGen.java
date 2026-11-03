package me.kvd.vcg.versions.v1_7;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Logger;
import me.kvd.vcg.api.VoidGenerator;
import me.kvd.vcg.api.events.VoidWorldSpawnLocationComputedEvent;
import me.kvd.vcg.api.settings.PluginSettings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

/**
 * Chunk generator that produces empty chunks with an optional platform.
 */
public final class VoidChunkGen extends ChunkGenerator implements VoidGenerator {

  private final PluginSettings settings;
  private final PlatformGen platformGen;
  private final Logger logger;
  private final Biome biome;

  /**
   * Creates a new generator.
   *
   * @param settings   the configuration, must not be null
   * @param platformGen the platform generator, must not be null
   * @param logger     the logger to use, must not be null
   * @throws NullPointerException if any argument is null
   */
  public VoidChunkGen(final PluginSettings settings,
                      final PlatformGen platformGen,
                      final Logger logger) {
    this.settings = Objects.requireNonNull(settings, "settings");
    this.platformGen = Objects.requireNonNull(platformGen, "platformGen");
    this.logger = Objects.requireNonNull(logger, "logger");
    this.biome = resolveBiome(settings.biomeName(), logger);
  }

  @Override
  public byte[][] generateBlockSections(final World world, final Random random,
                                        final int chunkX, final int chunkZ,
                                        final BiomeGrid biomes) {
    final byte[][] sections = new byte[16][];
    if (!this.settings.platform().enabled()) {
      return sections;
    }
    final byte[] platform = this.platformGen.generatePlatform(
      chunkX, chunkZ, this.settings.platform());
    if (null == platform) {
      return sections;
    }
    final int sectionIndex = this.settings.platform().y() >> 4;
    if (sectionIndex < 0 || sectionIndex > 15) {
      return sections;
    }
    sections[sectionIndex] = platform;
    return sections;
  }

  @Override
  public List<BlockPopulator> getDefaultPopulators(final World world) {
    return Collections.emptyList();
  }

  @Override
  public Location getFixedSpawnLocation(final World world, final Random random) {
    Objects.requireNonNull(world, "world");
    Objects.requireNonNull(random, "random");
    final var fixed = this.settings.fixedSpawn();
    final Location base = fixed.enabled()
      ? new Location(world, fixed.x(), fixed.y(), fixed.z(), fixed.yaw(), fixed.pitch())
      : new Location(world, 0.5, 64.0, 0.5, 0.0f, 0.0f);
    final var event = new VoidWorldSpawnLocationComputedEvent(world.getName(), base);
    Bukkit.getPluginManager().callEvent(event);
    final var computed = event.getSpawnLocation();
    return null == computed ? new Location(world, 0.5, 64.0, 0.5) : computed;
  }

  private static Biome resolveBiome(final String name, final Logger logger) {
    try {
      return Biome.valueOf(name.toUpperCase(Locale.ROOT));
    } catch (final IllegalArgumentException e) {
      logger.warning(() -> "Unknown biome '" + name
        + "', falling back to PLAINS");
      return Biome.PLAINS;
    }
  }
}