package me.kvdpxne.vcg.api;

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
  BorderPattern pattern
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

  public static PlatformConfig defaults() {
    return builder().build();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private int y = 64;
    private int chunkRadius = 1;
    private Material mainBlock = Material.STONE;
    private Material borderBlock = Material.BEDROCK;
    private BorderPattern pattern = BorderPattern.SPAWN_MARKER;

    private Builder() {
    }

    public Builder y(final int y) {
      this.y = y;
      return this;
    }

    public Builder chunkRadius(final int chunkRadius) {
      this.chunkRadius = chunkRadius;
      return this;
    }

    public Builder mainBlock(final Material mainBlock) {
      this.mainBlock = mainBlock;
      return this;
    }

    public Builder borderBlock(final Material borderBlock) {
      this.borderBlock = borderBlock;
      return this;
    }

    public Builder pattern(final BorderPattern pattern) {
      this.pattern = pattern;
      return this;
    }

    public PlatformConfig build() {
      return new PlatformConfig(
        this.y, this.chunkRadius,
        this.mainBlock, this.borderBlock, this.pattern);
    }
  }
}