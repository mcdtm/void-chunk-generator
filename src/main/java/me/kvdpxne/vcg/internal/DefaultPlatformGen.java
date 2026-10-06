package me.kvdpxne.vcg.internal;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.kvdpxne.vcg.api.PlatformConfig;
import org.bukkit.World;

public final class DefaultPlatformGen implements PlatformGen {

  private static final int CHUNK_SIZE = 16;

  private final Logger logger;

  public DefaultPlatformGen(final Logger logger) {
    this.logger = logger;
  }

  @Override
  public void generate(final byte[][] sections, final World world,
                       final int chunkX, final int chunkZ,
                       final PlatformConfig config) {
    try {
      this.generateByteSafely(sections, chunkX, chunkZ, config);
    } catch (final Exception ex) {
      this.logger.log(Level.SEVERE,
        "Blad platformy (byte) w chunku (" + chunkX + "," + chunkZ + ")", ex);
    }
  }

  @Override
  public void generateExt(final short[][] sections, final World world,
                          final int chunkX, final int chunkZ,
                          final PlatformConfig config) {
    try {
      this.generateShortSafely(sections, chunkX, chunkZ, config);
    } catch (final Exception ex) {
      this.logger.log(Level.SEVERE,
        "Blad platformy (ext) w chunku (" + chunkX + "," + chunkZ + ")", ex);
    }
  }

  private void generateByteSafely(final byte[][] sections, final int chunkX, final int chunkZ,
                                  final PlatformConfig config) {
    if (!this.isInRange(chunkX, chunkZ, config.chunkRadius())) return;

    final var sectionIndex = config.y() / CHUNK_SIZE;
    final var yInSection = config.y() % CHUNK_SIZE;
    sections[sectionIndex] = new byte[CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE];

    final var chunkMinX = chunkX * CHUNK_SIZE;
    final var chunkMinZ = chunkZ * CHUNK_SIZE;
    final var mainId = (byte) config.mainBlock().getId();
    final var borderId = (byte) config.borderBlock().getId();

    for (var localX = 0; localX < CHUNK_SIZE; localX++) {
      for (var localZ = 0; localZ < CHUNK_SIZE; localZ++) {
        final var worldX = chunkMinX + localX;
        final var worldZ = chunkMinZ + localZ;
        final var idx = yInSection * 256 + localZ * 16 + localX;
        sections[sectionIndex][idx] = this.isBorder(worldX, worldZ, config)
          ? borderId : mainId;
      }
    }
  }

  private void generateShortSafely(final short[][] sections, final int chunkX, final int chunkZ,
                                   final PlatformConfig config) {
    if (!this.isInRange(chunkX, chunkZ, config.chunkRadius())) return;

    final var sectionIndex = config.y() / CHUNK_SIZE;
    final var yInSection = config.y() % CHUNK_SIZE;
    sections[sectionIndex] = new short[CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE];

    final var chunkMinX = chunkX * CHUNK_SIZE;
    final var chunkMinZ = chunkZ * CHUNK_SIZE;
    final var mainId = (short) config.mainBlock().getId();
    final var borderId = (short) config.borderBlock().getId();

    for (var localX = 0; localX < CHUNK_SIZE; localX++) {
      for (var localZ = 0; localZ < CHUNK_SIZE; localZ++) {
        final var worldX = chunkMinX + localX;
        final var worldZ = chunkMinZ + localZ;
        final var idx = yInSection * 256 + localZ * 16 + localX;
        sections[sectionIndex][idx] = this.isBorder(worldX, worldZ, config)
          ? borderId : mainId;
      }
    }
  }

  private boolean isInRange(final int chunkX, final int chunkZ, final int radius) {
    return chunkX >= -radius && chunkX < radius
      && chunkZ >= -radius && chunkZ < radius;
  }

  private boolean isBorder(final int x, final int z, final PlatformConfig config) {
    return switch (config.pattern()) {
      case NONE -> false;
      case SPAWN_MARKER -> (x == -1 || x == 0) && (z == -1 || z == 0);
      case FULL -> true;
      case RING -> {
        final var bound = config.chunkRadius() * CHUNK_SIZE;
        yield x == -bound || x == bound - 1
          || z == -bound || z == bound - 1;
      }
    };
  }
}