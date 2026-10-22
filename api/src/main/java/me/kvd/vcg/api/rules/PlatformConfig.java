package me.kvd.vcg.api.rules;

import org.bukkit.Material;

/**
 * Immutable platform definition. Nested {@link Builder} keeps related
 * construction logic next to the data it builds.
 */
public record PlatformConfig(
  int y,
  int chunkRadius,
  Material mainBlock,
  Material borderBlock,
  PlatformBorderPattern pattern
) {

  private static final int MIN_Y = 1;
  private static final int MAX_Y = 254;
  private static final int MIN_RADIUS = 0;
  private static final int MAX_RADIUS = 8;

  public PlatformConfig {
    if (null == mainBlock) {
      throw new NullPointerException("mainBlock must not be null");
    }
    if (null == borderBlock) {
      throw new NullPointerException("borderBlock must not be null");
    }
    if (null == pattern) {
      throw new NullPointerException("pattern must not be null");
    }
    if (MIN_Y > y || MAX_Y < y) {
      throw new IllegalArgumentException("y must be in ["
        + MIN_Y + ", " + MAX_Y + "], got: " + y);
    }
    if (MIN_RADIUS > chunkRadius || MAX_RADIUS < chunkRadius) {
      throw new IllegalArgumentException("chunkRadius must be in ["
        + MIN_RADIUS + ", " + MAX_RADIUS + "], got: " + chunkRadius);
    }
  }
}