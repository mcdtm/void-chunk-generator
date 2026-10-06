package me.kvdpxne.vcg.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.kvdpxne.vcg.api.VoidWorldConfig;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

public final class VoidChunkGen extends ChunkGenerator {

  private static final int CHUNK_SIZE = 16;
  private static final int SECTION_LEN = CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE; // 4096

  /** Log kazdego chunka - wylaczyc produkcyjnie. */
  private static final boolean DEBUG_CHUNKS = true;

  private final VoidWorldConfig config;
  private final PlatformGen platformGenerator;
  private final Logger logger;

  public VoidChunkGen(final VoidWorldConfig config,
                             final PlatformGen platformGenerator,
                             final Logger logger) {
    this.config = config;
    this.platformGenerator = platformGenerator;
    this.logger = logger;
  }

  @Override
  public List<BlockPopulator> getDefaultPopulators(final World world) {
    return new ArrayList<>();
  }

  @Override
  public boolean canSpawn(final World world, final int x, final int z) {
    return true;
  }

  @Override
  public Location getFixedSpawnLocation(final World world, final Random random) {
    return this.config.spawnLocation()
      .map(offset -> offset.toLocation(world))
      .orElse(null);
  }

  // ============================================================
  //  Byte-based sections (klasyczne, IDs 0..255)
  // ============================================================
  @Override
  public byte[][] generateBlockSections(final World world, final Random random,
                                        final int chunkX, final int chunkZ,
                                        final BiomeGrid biomes) {
    try {
      this.applyBiome(biomes);

      final var sectionCount = world.getMaxHeight() / CHUNK_SIZE;
      final var sections = new byte[sectionCount][];
      // kazda sekcja = 4096 bajtow zerowych = powietrze

      this.config.platform().ifPresent(platform ->
        this.platformGenerator.generate(sections, world, chunkX, chunkZ, platform));

      if (DEBUG_CHUNKS) {
        this.logger.info("GEN (byte) chunk " + chunkX + "," + chunkZ
          + " sections=" + sectionCount);
      }

      return sections;
    } catch (final Exception ex) {
      this.logger.log(Level.SEVERE, "Blad (byte) chunka (" + chunkX + "," + chunkZ + ")", ex);
      return new byte[world.getMaxHeight() / CHUNK_SIZE][];
    }
  }

  // ============================================================
  //  Ext byte sections (IDs 0..4095) - nadpisane dla pewnosci,
  //  ze CraftBukkit wybierze wlasnie nasza implementacje.
  // ============================================================
  @Override
  public short[][] generateExtBlockSections(final World world, final Random random,
                                            final int chunkX, final int chunkZ,
                                            final BiomeGrid biomes) {
    try {
      this.applyBiome(biomes);

      final var sectionCount = world.getMaxHeight() / CHUNK_SIZE;
      final var sections = new short[sectionCount][];
      // kazda sekcja = 4096 shortow zerowych = powietrze

      this.config.platform().ifPresent(platform ->
        this.platformGenerator.generateExt(sections, world, chunkX, chunkZ, platform));

      if (DEBUG_CHUNKS) {
        this.logger.info("GEN (ext) chunk " + chunkX + "," + chunkZ
          + " sections=" + sectionCount);
      }

      return sections;
    } catch (final Exception ex) {
      this.logger.log(Level.SEVERE, "Blad (ext) chunka (" + chunkX + "," + chunkZ + ")", ex);
      return new short[world.getMaxHeight() / CHUNK_SIZE][];
    }
  }

  private void applyBiome(final BiomeGrid biomes) {
    final Biome biome = this.config.biome();
    for (var x = 0; x < CHUNK_SIZE; x++) {
      for (var z = 0; z < CHUNK_SIZE; z++) {
        biomes.setBiome(x, z, biome);
      }
    }
  }
}