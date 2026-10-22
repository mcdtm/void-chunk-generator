package me.kvd.vcg.versions.v1_7;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.kvd.vcg.api.events.VoidWorldSpawnLocationComputedEvent;
import me.kvd.vcg.api.settings.PluginSettings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

/**
 * Generates empty chunks with a fixed biome and an optional platform.
 * All exceptions are caught locally so a single bad chunk cannot take down
 * the whole server tick.
 */
public final class VoidChunkGen extends ChunkGenerator {

  private static final int CHUNK_SIZE = 16;

  private final PluginSettings config;
  private final PlatformGen platformGen;
  private final Logger logger;
  private final Biome biome;

  public VoidChunkGen(final PluginSettings config,
                      final PlatformGen platformGen,
                      final Logger logger) {
    this.config = Objects.requireNonNull(config, "config");
    this.platformGen = Objects.requireNonNull(platformGen, "platformGen");
    this.logger = Objects.requireNonNull(logger, "logger");
    this.biome = Biome.valueOf(this.config.biomeName());
  }

  @Override
  public List<BlockPopulator> getDefaultPopulators(final World world) {
    return Collections.emptyList();
  }

  @Override
  public boolean canSpawn(final World world, final int x, final int z) {
    return true;
  }

  @Override
  public Location getFixedSpawnLocation(final World world, final Random random) {
    if (null == world) {
      return null;
    }
    final var fixedSpawn = this.config.fixedSpawn();
    if (null == fixedSpawn) {
      return null;
    }
    final var initial = fixedSpawn.toLocation(world);
    final var event = new VoidWorldSpawnLocationComputedEvent(world, initial);
    Bukkit.getPluginManager().callEvent(event);
    return event.isCancelled() ? null : event.getSpawnLocation();
  }

  @Override
  public byte[][] generateBlockSections(final World world, final Random random,
                                        final int chunkX, final int chunkZ,
                                        final BiomeGrid biomes) {
    final var sectionCount = this.sectionCount(world);
    try {
      this.applyBiome(biomes);
      final var sections = new byte[sectionCount][];
      final var platform = this.config.platform();
      if (null != platform) {
        this.platformGen.generate(sections, world, chunkX, chunkZ, platform);
      }
      return sections;
    } catch (final RuntimeException ex) {
      this.logger.log(Level.SEVERE, "Byte chunk generation failed at ("
        + chunkX + ", " + chunkZ + ")", ex);
      return new byte[sectionCount][];
    }
  }

  @Override
  public short[][] generateExtBlockSections(final World world, final Random random,
                                            final int chunkX, final int chunkZ,
                                            final BiomeGrid biomes) {
    final var sectionCount = this.sectionCount(world);
    try {
      this.applyBiome(biomes);
      final var sections = new short[sectionCount][];
      final var platform = this.config.platform();
      if (null != platform) {
        this.platformGen.generateExt(sections, world, chunkX, chunkZ, platform);
      }
      return sections;
    } catch (final RuntimeException ex) {
      this.logger.log(Level.SEVERE, "Ext chunk generation failed at ("
        + chunkX + ", " + chunkZ + ")", ex);
      return new short[sectionCount][];
    }
  }

  private int sectionCount(final World world) {
    return null == world ? 16 : world.getMaxHeight() / CHUNK_SIZE;
  }

  private void applyBiome(final BiomeGrid biomes) {
    if (null == biomes) {
      return;
    }
    final var biome = this.biome;
    for (var x = 0; CHUNK_SIZE > x; x++) {
      for (var z = 0; CHUNK_SIZE > z; z++) {
        biomes.setBiome(x, z, biome);
      }
    }
  }
}