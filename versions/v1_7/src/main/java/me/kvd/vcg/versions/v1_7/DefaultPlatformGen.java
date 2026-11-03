package me.kvd.vcg.versions.v1_7;

import java.util.Objects;
import java.util.logging.Logger;
import me.kvd.vcg.api.rules.PlatformBorderPattern;
import me.kvd.vcg.api.rules.PlatformConfig;
import me.kvd.vcg.api.settings.PluginSettings;
import org.bukkit.Material;

/**
 * Default {@link PlatformGen} for 1.7.x.
 * <p>
 * <b>Performance contract:</b> called once per chunk. All bounds, materials
 * and the spawn center are pre-resolved in the constructor. Chunks outside
 * the platform area and without a marker block receive {@code null} without
 * allocation.
 * <p>
 * <b>Alignment note:</b> when the spawn block sits at the NW corner of its
 * chunk (e.g. {@code fixedSpawn.x = 0.5}), the chunk-aligned bounds
 * {@code [chunkRadius * 16]} make the platform appear to extend further
 * on the SE side. Set {@link #BLOCK_ALIGNED} to {@code true} to shift
 * bounds by half a chunk so the platform is visually centered on the spawn
 * block instead of the spawn chunk.
 */
public final class DefaultPlatformGen implements PlatformGen {

  /** When true, shift bounds by half a chunk to center on the spawn block. */
  private static final boolean BLOCK_ALIGNED = true;

  private static final byte DEFAULT_MAIN_ID = 1;      // STONE
  private static final byte DEFAULT_BORDER_ID = 7;    // BEDROCK
  private static final int SECTION_VOLUME = 16 * 16 * 16;

  private static final int[][] MARKER_OFFSETS = {
    {0, 0}, {-1, 0}, {-1, -1}, {0, -1}
  };

  private final Logger logger;
  private final byte mainId;
  private final byte borderId;
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
   * Creates a new generator with all constant data pre-resolved.
   *
   * @param settings the plugin settings, must not be null
   * @param logger   the logger to use, must not be null
   * @throws NullPointerException if any argument is null
   */
  public DefaultPlatformGen(final PluginSettings settings, final Logger logger) {
    Objects.requireNonNull(settings, "settings");
    this.logger = Objects.requireNonNull(logger, "logger");
    final var platform = settings.platform();
    this.mainId = resolveBlockId(platform.material(), DEFAULT_MAIN_ID, logger);
    this.borderId = resolveBlockId(platform.borderMaterial(), this.mainId, logger);
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
  public byte[] generatePlatform(final int chunkX, final int chunkZ,
                                 final PlatformConfig config) {
    if (!config.enabled()) {
      return null;
    }
    final int y = config.y();
    if (y < 0 || y > 255) {
      this.logger.warning(() -> "Invalid platform Y: " + y);
      return null;
    }
    final boolean inArea = chunkX >= this.minChunkX && chunkX <= this.maxChunkX
      && chunkZ >= this.minChunkZ && chunkZ <= this.maxChunkZ;
    final boolean drawsMarker = PlatformBorderPattern.SPAWN_MARKER == this.pattern
      && chunkContainsMarker(chunkX, chunkZ);
    if (!inArea && !drawsMarker) {
      return null;
    }
    final int localY = y & 0x0F;
    final int chunkMinBlockX = chunkX << 4;
    final int chunkMinBlockZ = chunkZ << 4;
    final byte[] blocks = new byte[SECTION_VOLUME];

    if (inArea) {
      final int iterMinX = Math.max(this.minBlockX, chunkMinBlockX);
      final int iterMaxX = Math.min(this.maxBlockX, chunkMinBlockX + 15);
      final int iterMinZ = Math.max(this.minBlockZ, chunkMinBlockZ);
      final int iterMaxZ = Math.min(this.maxBlockZ, chunkMinBlockZ + 15);
      switch (this.pattern) {
        case NONE, SPAWN_MARKER -> fillRect(blocks, localY, iterMinX,
          iterMaxX, iterMinZ, iterMaxZ, chunkMinBlockX,
          chunkMinBlockZ, this.mainId);
        case FULL -> fillRect(blocks, localY, iterMinX, iterMaxX,
          iterMinZ, iterMaxZ, chunkMinBlockX, chunkMinBlockZ,
          this.borderId);
        case RING -> {
          fillRect(blocks, localY, iterMinX, iterMaxX, iterMinZ,
            iterMaxZ, chunkMinBlockX, chunkMinBlockZ, this.mainId);
          for (int wx = iterMinX; wx <= iterMaxX; wx++) {
            if (wx != this.minBlockX && wx != this.maxBlockX) {
              continue;
            }
            final int lx = wx - chunkMinBlockX;
            for (int wz = iterMinZ; wz <= iterMaxZ; wz++) {
              blocks[(localY << 8) | ((wz - chunkMinBlockZ) << 4) | lx]
                = this.borderId;
            }
          }
          for (int wz = iterMinZ; wz <= iterMaxZ; wz++) {
            if (wz != this.minBlockZ && wz != this.maxBlockZ) {
              continue;
            }
            final int lz = wz - chunkMinBlockZ;
            for (int wx = iterMinX; wx <= iterMaxX; wx++) {
              blocks[(localY << 8) | (lz << 4) | (wx - chunkMinBlockX)]
                = this.borderId;
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
          blocks[(localY << 8) | ((wz & 0x0F) << 4) | (wx & 0x0F)]
            = this.borderId;
        }
      }
    }
    return blocks;
  }

  private static void fillRect(final byte[] blocks, final int localY,
                               final int iterMinX, final int iterMaxX,
                               final int iterMinZ, final int iterMaxZ,
                               final int chunkMinBlockX, final int chunkMinBlockZ,
                               final byte blockId) {
    for (int wx = iterMinX; wx <= iterMaxX; wx++) {
      final int lx = wx - chunkMinBlockX;
      final int baseIdx = (localY << 8) | lx;
      for (int wz = iterMinZ; wz <= iterMaxZ; wz++) {
        blocks[baseIdx | ((wz - chunkMinBlockZ) << 4)] = blockId;
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

  private static byte resolveBlockId(final String name, final byte fallback,
                                     final Logger logger) {
    if (name == null || name.isBlank()) {
      return fallback;
    }
    final Material material = Material.matchMaterial(name.trim());
    if (null == material) {
      logger.warning(() -> "Unknown material: " + name
        + ", falling back to ID " + fallback);
      return fallback;
    }
    final int id = material.getId();
    if (id < 0 || id > 255) {
      logger.warning(() -> "Material ID out of byte range: "
        + material + " (" + id + "), falling back to " + fallback);
      return fallback;
    }
    return (byte) id;
  }
}