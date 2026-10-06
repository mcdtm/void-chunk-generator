package me.kvdpxne.vcg.internal;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.World;

import me.kvdpxne.vcg.api.PlatformConfig;

/**
 * Default platform layout.
 * Handles every {@link me.kvdpxne.vcg.api.BorderPattern} and works with both
 * byte-based and short-based section arrays.
 * Never throws - any internal failure is logged and the affected section is
 * left empty so the world keeps generating.
 */
public final class DefaultPlatformGen implements PlatformGen {

  private static final int CHUNK_SIZE = 16;
  private static final int SECTION_VOLUME = CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE;

  private final Logger logger;

  public DefaultPlatformGen(final Logger logger) {
    if (null == logger) {
      throw new NullPointerException("logger must not be null");
    }
    this.logger = logger;
  }

  @Override
  public void generate(final byte[][] sections, final World world,
                       final int chunkX, final int chunkZ,
                       final PlatformConfig config) {
    if (null == sections || null == config) {
      return;
    }
    try {
      this.fillByteSections(sections, chunkX, chunkZ, config);
    } catch (final RuntimeException ex) {
      this.logger.log(Level.SEVERE,
        "Byte platform failed at chunk (" + chunkX + ", " + chunkZ + ")", ex);
    }
  }

  @Override
  public void generateExt(final short[][] sections, final World world,
                          final int chunkX, final int chunkZ,
                          final PlatformConfig config) {
    if (null == sections || null == config) {
      return;
    }
    try {
      this.fillShortSections(sections, chunkX, chunkZ, config);
    } catch (final RuntimeException ex) {
      this.logger.log(Level.SEVERE,
        "Ext platform failed at chunk (" + chunkX + ", " + chunkZ + ")", ex);
    }
  }

  // ------------------------------------------------------------------
  //  Internals
  // ------------------------------------------------------------------

  private void fillByteSections(final byte[][] sections, final int chunkX, final int chunkZ,
                                final PlatformConfig config) {
    if (!this.inRange(chunkX, chunkZ, config.chunkRadius())) {
      return;
    }
    final var sectionIndex = config.y() / CHUNK_SIZE;
    final var yInSection = config.y() % CHUNK_SIZE;

    sections[sectionIndex] = new byte[SECTION_VOLUME];

    final var minX = chunkX * CHUNK_SIZE;
    final var minZ = chunkZ * CHUNK_SIZE;
    final var mainId = (byte) config.mainBlock().getId();
    final var borderId = (byte) config.borderBlock().getId();

    for (var localX = 0; CHUNK_SIZE > localX; localX++) {
      for (var localZ = 0; CHUNK_SIZE > localZ; localZ++) {
        final var worldX = minX + localX;
        final var worldZ = minZ + localZ;
        final var index = yInSection * 256 + localZ * 16 + localX;
        sections[sectionIndex][index] =
          this.isBorder(worldX, worldZ, config) ? borderId : mainId;
      }
    }
  }

  private void fillShortSections(final short[][] sections, final int chunkX, final int chunkZ,
                                 final PlatformConfig config) {
    if (!this.inRange(chunkX, chunkZ, config.chunkRadius())) {
      return;
    }
    final var sectionIndex = config.y() / CHUNK_SIZE;
    final var yInSection = config.y() % CHUNK_SIZE;

    sections[sectionIndex] = new short[SECTION_VOLUME];

    final var minX = chunkX * CHUNK_SIZE;
    final var minZ = chunkZ * CHUNK_SIZE;
    final var mainId = (short) config.mainBlock().getId();
    final var borderId = (short) config.borderBlock().getId();

    for (var localX = 0; CHUNK_SIZE > localX; localX++) {
      for (var localZ = 0; CHUNK_SIZE > localZ; localZ++) {
        final var worldX = minX + localX;
        final var worldZ = minZ + localZ;
        final var index = yInSection * 256 + localZ * 16 + localX;
        sections[sectionIndex][index] =
          this.isBorder(worldX, worldZ, config) ? borderId : mainId;
      }
    }
  }

  private boolean inRange(final int chunkX, final int chunkZ, final int radius) {
    return radius > chunkX && -radius <= chunkX
      && radius > chunkZ && -radius <= chunkZ;
  }

  private boolean isBorder(final int x, final int z, final PlatformConfig config) {
    return switch (config.pattern()) {
      case NONE -> false;
      case FULL -> true;
      case SPAWN_MARKER -> (0 == x || -1 == x) && (0 == z || -1 == z);
      case RING -> {
        final var bound = config.chunkRadius() * CHUNK_SIZE;
        yield -bound == x || bound - 1 == x
          || -bound == z || bound - 1 == z;
      }
    };
  }
}