package me.kvd.vcg.versions.v26;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Logger;
import me.kvd.vcg.api.VoidGenerator;
import me.kvd.vcg.api.rules.FixedSpawnConfig;
import me.kvd.vcg.api.settings.PluginSettings;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

/**
 * Void chunk generator for modern Minecraft versions (1.18+ / 26.x).
 * <p>
 * Disables all vanilla generation stages and optionally places
 * a platform under the spawn.
 * <p>
 * <b>Important for 26.3+:</b> the parameterless {@code shouldGenerate*} methods
 * are deprecated and have no effect when the overloads taking
 * {@link WorldInfo}, {@link Random}, {@code int}, {@code int} are present.
 * This class therefore overrides <b>both</b> variants to guarantee a void world
 * regardless of the server implementation.
 */
public final class VoidChunkGen extends ChunkGenerator implements VoidGenerator {

  private final PluginSettings settings;
  private final PlatformGen platformGen;
  private final VoidBiomeProvider biomeProvider;

  /**
   * Creates a new generator.
   *
   * @param settings    the configuration, must not be null
   * @param platformGen the platform generator, must not be null
   * @param logger      the logger to use, must not be null
   * @throws NullPointerException if any argument is null
   */
  public VoidChunkGen(final PluginSettings settings,
                      final PlatformGen platformGen,
                      final Logger logger) {
    this.settings = Objects.requireNonNull(settings, "settings");
    this.platformGen = Objects.requireNonNull(platformGen, "platformGen");
    this.biomeProvider = new VoidBiomeProvider(settings, logger);
  }

  // ------------------------------------------------------------------
  // Generation stages (left empty on purpose)
  // ------------------------------------------------------------------

  @Override
  public void generateNoise(final WorldInfo worldInfo, final Random random,
                            final int chunkX, final int chunkZ,
                            final ChunkData chunkData) {
    // Deliberately empty – no vanilla noise.
  }

  @Override
  public void generateSurface(final WorldInfo worldInfo, final Random random,
                              final int chunkX, final int chunkZ,
                              final ChunkData chunkData) {
    // Vanilla surface is disabled by shouldGenerateSurface returning false.
    // We only add the platform here.
    if (this.settings.platform().enabled()) {
      this.platformGen.generatePlatform(
        chunkData, chunkX, chunkZ, this.settings.platform());
    }
  }

  @Override
  public void generateBedrock(final WorldInfo worldInfo, final Random random,
                              final int chunkX, final int chunkZ,
                              final ChunkData chunkData) {
    // Deliberately empty – no bedrock layer.
  }

  @Override
  public void generateCaves(final WorldInfo worldInfo, final Random random,
                            final int chunkX, final int chunkZ,
                            final ChunkData chunkData) {
    // Deliberately empty – no caves.
  }

  // ------------------------------------------------------------------
  // Stage control – BOTH overloads are overridden to be safe on 26.3+
  // ------------------------------------------------------------------

  // --- Noise --------------------------------------------------------
  @Override
  @Deprecated
  public boolean shouldGenerateNoise() {
    return false;
  }

  @Override
  public boolean shouldGenerateNoise(final WorldInfo worldInfo,
                                     final Random random,
                                     final int chunkX,
                                     final int chunkZ) {
    return false;
  }

  // --- Surface ------------------------------------------------------
  @Override
  @Deprecated
  public boolean shouldGenerateSurface() {
    return false;
  }

  @Override
  public boolean shouldGenerateSurface(final WorldInfo worldInfo,
                                       final Random random,
                                       final int chunkX,
                                       final int chunkZ) {
    // Returning false disables Vanilla surface generation.
    // generateSurface(...) is still called and adds the platform.
    return false;
  }

  // --- Bedrock ------------------------------------------------------
  @Override
  @Deprecated
  public boolean shouldGenerateBedrock() {
    return false;
  }

  // --- Caves --------------------------------------------------------
  @Override
  @Deprecated
  public boolean shouldGenerateCaves() {
    return false;
  }

  @Override
  public boolean shouldGenerateCaves(final WorldInfo worldInfo,
                                     final Random random,
                                     final int chunkX,
                                     final int chunkZ) {
    return false;
  }

  // --- Structures ---------------------------------------------------
  @Override
  public boolean shouldGenerateStructures() {
    return false;
  }

  @Override
  public boolean shouldGenerateStructures(final WorldInfo worldInfo,
                                          final Random random,
                                          final int chunkX,
                                          final int chunkZ) {
    return false;
  }

  // --- Mobs ---------------------------------------------------------
  @Override
  public boolean shouldGenerateMobs() {
    return false;
  }

  @Override
  public boolean shouldGenerateMobs(final WorldInfo worldInfo,
                                    final Random random,
                                    final int chunkX,
                                    final int chunkZ) {
    return false;
  }

  // ------------------------------------------------------------------
  // Biome provider / populator / spawn
  // ------------------------------------------------------------------

  @Override
  public BiomeProvider getDefaultBiomeProvider(final WorldInfo worldInfo) {
    return this.biomeProvider;
  }

  @Override
  public List<BlockPopulator> getDefaultPopulators(final World world) {
    Objects.requireNonNull(world, "world");
    return Collections.emptyList();
  }

  @Override
  public Location getFixedSpawnLocation(final World world, final Random random) {
    Objects.requireNonNull(world, "world");
    Objects.requireNonNull(random, "random");
    final FixedSpawnConfig fixed = this.settings.fixedSpawn();
    if (fixed.enabled()) {
      return new Location(world, fixed.x(), fixed.y(), fixed.z(),
        fixed.yaw(), fixed.pitch());
    }
    return new Location(world, 0.5, 65.0, 0.5, 0.0f, 0.0f);
  }
}