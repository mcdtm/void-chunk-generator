package me.kvdpxne.vcg.internal;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.kvdpxne.vcg.api.event.VoidWorldSpawnLocationComputedEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import me.kvdpxne.vcg.api.VoidWorldConfig;

/**
 * Generates empty chunks with a fixed biome and an optional platform.
 * All exceptions are caught locally so a single bad chunk cannot take down
 * the whole server tick.
 */
public final class VoidChunkGen extends ChunkGenerator {

  private static final int CHUNK_SIZE = 16;

  private final VoidWorldConfig config;
  private final PlatformGen platformGen;
  private final Logger logger;

  public VoidChunkGen(final VoidWorldConfig config,
                      final PlatformGen platformGen,
                      final Logger logger) {
    this.config = Objects.requireNonNull(config, "config");
    this.platformGen = Objects.requireNonNull(platformGen, "platformGen");
    this.logger = Objects.requireNonNull(logger, "logger");
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

    final var offsetOpt = this.config.spawnLocation();
    if (offsetOpt.isEmpty()) {
      return null;
    }

    final var initial = offsetOpt.get().toLocation(world);
    final var event = new VoidWorldSpawnLocationComputedEvent(world, initial);
    Bukkit.getPluginManager().callEvent(event);

    if (event.isCancelled()) {
      return null;
    }

    return event.getSpawnLocation();
  }

  @Override
  public byte[][] generateBlockSections(final World world, final Random random,
                                        final int chunkX, final int chunkZ,
                                        final BiomeGrid biomes) {
    final var sectionCount = this.sectionCount(world);
    try {
      this.applyBiome(biomes);
      final var sections = new byte[sectionCount][];
      this.config.platform().ifPresent(platform ->
        this.platformGen.generate(sections, world, chunkX, chunkZ, platform));
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
      this.config.platform().ifPresent(platform ->
        this.platformGen.generateExt(sections, world, chunkX, chunkZ, platform));
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
    final Biome biome = this.config.biome();
    for (var x = 0; CHUNK_SIZE > x; x++) {
      for (var z = 0; CHUNK_SIZE > z; z++) {
        biomes.setBiome(x, z, biome);
      }
    }
  }
}