package me.kvd.vcg.versions.v26;

import java.util.Objects;
import java.util.logging.Logger;
import me.kvd.vcg.api.rules.PlatformBorderPattern;
import me.kvd.vcg.api.rules.PlatformConfig;
import me.kvd.vcg.api.settings.PluginSettings;
import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;

/**
 * Default {@link PlatformGen} for modern versions.
 * <p>
 * <b>Performance contract:</b> called once per chunk. All constant data
 * is pre-resolved in the constructor; chunks outside the platform area
 * and without a marker block return immediately.
 * <p>
 * <b>Alignment note:</b> see v1_7 {@code DefaultPlatformGen}.
 */
public final class DefaultPlatformGen implements PlatformGen {

  /** When true, shift bounds by half a chunk to center on the spawn block. */
  private static final boolean BLOCK_ALIGNED = true;

  private static final int[][] MARKER_OFFSETS = {
    {0, 0}, {-1, 0}, {-1, -1}, {0, -1}
  };

  private final Logger logger;
  private final Material mainMaterial;
  private final Material borderMaterial;
  private final PlatformBorderPattern pattern;

  private final int centerBlockX;
  private final int centerBlockZ;

  private final int minChunkX;
  private final int maxChunkX;
  private final int minChunkZ;
  private final int maxChunkZ;

  private final int minBlockX;
  private final int maxBlockX;
  private final int minBlockZ;
  private final int maxBlockZ;

  /**
   * Creates a new generator.
   *
   * @param settings the plugin settings, must not be null
   * @param logger   the logger to use, must not be null
   * @throws NullPointerException if any argument is null
   */
  public DefaultPlatformGen(final PluginSettings settings, final Logger logger) {
    Objects.requireNonNull(settings, "settings");
    this.logger = Objects.requireNonNull(logger, "logger");
    final var platform = settings.platform();
    this.mainMaterial = resolveMaterial(platform.material(), Material.BEDROCK, logger);
    this.borderMaterial = resolveMaterial(platform.borderMaterial(), Material.STONE, logger);
    this.pattern = platform.borderPattern();

    final var fixed = settings.fixedSpawn();
    final double spawnX = fixed.enabled() ? fixed.x() : 0.5;
    final double spawnZ = fixed.enabled() ? fixed.z() : 0.5;
    this.centerBlockX = (int) Math.floor(spawnX);
    this.centerBlockZ = (int) Math.floor(spawnZ);

    // Pure chunk-aligned bounds: [spawnChunk - radius, spawnChunk + radius]
    final int radius = platform.chunkRadius();
    final int spawnChunkX = this.centerBlockX >> 4;
    final int spawnChunkZ = this.centerBlockZ >> 4;

    // B: radius=0 → 1 chunk; radius>=1 → 2*radius chunks per axis
    this.minChunkX = (radius == 0) ? spawnChunkX : spawnChunkX - radius;
    this.maxChunkX = (radius == 0) ? spawnChunkX : spawnChunkX + radius - 1;
    this.minChunkZ = (radius == 0) ? spawnChunkZ : spawnChunkZ - radius;
    this.maxChunkZ = (radius == 0) ? spawnChunkZ : spawnChunkZ + radius - 1;

    this.minBlockX = this.minChunkX << 4;
    this.maxBlockX = (this.maxChunkX << 4) + 15;
    this.minBlockZ = this.minChunkZ << 4;
    this.maxBlockZ = (this.maxChunkZ << 4) + 15;

    final int chunksX = this.maxChunkX - this.minChunkX + 1;
    final int chunksZ = this.maxChunkZ - this.minChunkZ + 1;
    logger.fine(() -> "Platform: radius=" + radius
      + " chunks X[" + this.minChunkX + ", " + this.maxChunkX + "]"
      + " Z[" + this.minChunkZ + ", " + this.maxChunkZ + "] = "
      + chunksX + "x" + chunksZ + " = " + (chunksX * chunksZ) + " chunks"
      + "; blocks X[" + this.minBlockX + ", " + this.maxBlockX + "]"
      + " Z[" + this.minBlockZ + ", " + this.maxBlockZ + "]"
      + "; spawn block: (" + this.centerBlockX + ", "
      + this.centerBlockZ + ")");
  }

  @Override
  public void generatePlatform(final ChunkGenerator.ChunkData chunkData,
                               final int chunkX, final int chunkZ,
                               final PlatformConfig config) {
    Objects.requireNonNull(chunkData, "chunkData");
    if (!config.enabled()) {
      return;
    }
    final int y = config.y();
    final int minY = chunkData.getMinHeight();
    final int maxY = chunkData.getMaxHeight();
    if (y < minY || y >= maxY) {
      this.logger.warning(() -> "Platform Y " + y
        + " is outside world bounds [" + minY + ", " + maxY + ")");
      return;
    }
    final boolean inArea = chunkX >= this.minChunkX && chunkX <= this.maxChunkX
      && chunkZ >= this.minChunkZ && chunkZ <= this.maxChunkZ;
    final boolean drawsMarker = PlatformBorderPattern.SPAWN_MARKER == this.pattern
      && chunkContainsMarker(chunkX, chunkZ);
    if (!inArea && !drawsMarker) {
      return;
    }
    final int chunkMinBlockX = chunkX << 4;
    final int chunkMinBlockZ = chunkZ << 4;

    if (inArea) {
      final int iterMinX = Math.max(this.minBlockX, chunkMinBlockX);
      final int iterMaxX = Math.min(this.maxBlockX, chunkMinBlockX + 15);
      final int iterMinZ = Math.max(this.minBlockZ, chunkMinBlockZ);
      final int iterMaxZ = Math.min(this.maxBlockZ, chunkMinBlockZ + 15);
      switch (this.pattern) {
        case NONE, SPAWN_MARKER -> fillRect(chunkData, y, iterMinX,
          iterMaxX, iterMinZ, iterMaxZ, chunkMinBlockX,
          chunkMinBlockZ, this.mainMaterial);
        case FULL -> fillRect(chunkData, y, iterMinX, iterMaxX,
          iterMinZ, iterMaxZ, chunkMinBlockX, chunkMinBlockZ,
          this.borderMaterial);
        case RING -> {
          fillRect(chunkData, y, iterMinX, iterMaxX, iterMinZ,
            iterMaxZ, chunkMinBlockX, chunkMinBlockZ,
            this.mainMaterial);
          for (int wx = iterMinX; wx <= iterMaxX; wx++) {
            if (wx != this.minBlockX && wx != this.maxBlockX) {
              continue;
            }
            for (int wz = iterMinZ; wz <= iterMaxZ; wz++) {
              chunkData.setBlock(wx - chunkMinBlockX, y,
                wz - chunkMinBlockZ, this.borderMaterial);
            }
          }
          for (int wz = iterMinZ; wz <= iterMaxZ; wz++) {
            if (wz != this.minBlockZ && wz != this.maxBlockZ) {
              continue;
            }
            for (int wx = iterMinX; wx <= iterMaxX; wx++) {
              chunkData.setBlock(wx - chunkMinBlockX, y,
                wz - chunkMinBlockZ, this.borderMaterial);
            }
          }
        }
      }
    }

    if (PlatformBorderPattern.SPAWN_MARKER == this.pattern) {
      for (final int[] offset : MARKER_OFFSETS) {
        final int wx = this.centerBlockX + offset[0];
        final int wz = this.centerBlockZ + offset[1];
        if ((wx >> 4) == chunkX && (wz >> 4) == chunkZ) {
          chunkData.setBlock(wx & 0x0F, y, wz & 0x0F,
            this.borderMaterial);
        }
      }
    }
  }

  private static void fillRect(final ChunkGenerator.ChunkData chunkData,
                               final int y,
                               final int iterMinX, final int iterMaxX,
                               final int iterMinZ, final int iterMaxZ,
                               final int chunkMinBlockX, final int chunkMinBlockZ,
                               final Material material) {
    for (int wx = iterMinX; wx <= iterMaxX; wx++) {
      final int lx = wx - chunkMinBlockX;
      for (int wz = iterMinZ; wz <= iterMaxZ; wz++) {
        chunkData.setBlock(lx, y, wz - chunkMinBlockZ, material);
      }
    }
  }

  private boolean chunkContainsMarker(final int chunkX, final int chunkZ) {
    for (final int[] offset : MARKER_OFFSETS) {
      final int wx = this.centerBlockX + offset[0];
      final int wz = this.centerBlockZ + offset[1];
      if ((wx >> 4) == chunkX && (wz >> 4) == chunkZ) {
        return true;
      }
    }
    return false;
  }

  private static Material resolveMaterial(final String name,
                                          final Material fallback,
                                          final Logger logger) {
    if (name == null || name.isBlank()) {
      return fallback;
    }
    final Material material = Material.matchMaterial(name.trim());
    if (null == material) {
      logger.warning(() -> "Unknown material: " + name
        + ", falling back to " + fallback);
      return fallback;
    }
    return material;
  }
}