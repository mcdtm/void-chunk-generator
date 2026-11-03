package me.kvd.vcg.api.rules;

import java.util.Objects;

/**
 * Immutable configuration for the platform placed under the spawn.
 * <p>
 * The platform covers a square of chunks centered on the spawn chunk:
 * {@code (2 * chunkRadius + 1)^2} chunks in total.
 * <ul>
 *   <li>{@code chunkRadius = 0} → 1 chunk (16×16 blocks)</li>
 *   <li>{@code chunkRadius = 1} → 3×3 chunks (48×48 blocks)</li>
 *   <li>{@code chunkRadius = 2} → 5×5 chunks (80×80 blocks)</li>
 * </ul>
 * <p>
 * <b>Border patterns:</b>
 * <ul>
 *   <li>{@link PlatformBorderPattern#NONE} — no border, all main material</li>
 *   <li>{@link PlatformBorderPattern#FULL} — entire platform is border material</li>
 *   <li>{@link PlatformBorderPattern#RING} — border along outer edge of the area</li>
 *   <li>{@link PlatformBorderPattern#SPAWN_MARKER} — 4 border blocks at offsets
 *       {@code (0,0)}, {@code (-1,0)}, {@code (-1,-1)}, {@code (0,-1)}
 *       from the spawn block. The marker is drawn even when the platform
 *       area is smaller than 2×2 chunks, so all 4 blocks stay visible.</li>
 * </ul>
 */
public record PlatformConfig(
  boolean enabled,
  int y,
  String material,
  String borderMaterial,
  PlatformBorderPattern borderPattern,
  int chunkRadius
) {

  /**
   * Compact constructor with validation.
   *
   * @throws NullPointerException     if any reference argument is null
   * @throws IllegalArgumentException if {@code y} or {@code chunkRadius}
   *                                  is out of range
   */
  public PlatformConfig {
    Objects.requireNonNull(material, "material");
    Objects.requireNonNull(borderMaterial, "borderMaterial");
    Objects.requireNonNull(borderPattern, "borderPattern");
    if (y < 0 || y > 255) {
      throw new IllegalArgumentException(
        "y must be in [0, 255], got: " + y);
    }
    if (chunkRadius < 0 || chunkRadius > 16) {
      throw new IllegalArgumentException(
        "chunkRadius must be in [0, 16], got: " + chunkRadius);
    }
  }

  /**
   * Returns a disabled platform configuration.
   *
   * @return a disabled configuration, never null
   */
  public static PlatformConfig disabled() {
    return new PlatformConfig(
      false, 64, "STONE", "BEDROCK",
      PlatformBorderPattern.NONE, 0);
  }

  /**
   * Returns a builder pre-populated with the defaults.
   *
   * @return a new builder, never null
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder for {@link PlatformConfig}.
   */
  public static final class Builder {

    private boolean enabled = false;
    private int y = 64;
    private String material = "STONE";
    private String borderMaterial = "BEDROCK";
    private PlatformBorderPattern borderPattern = PlatformBorderPattern.NONE;
    private int chunkRadius = 0;

    private Builder() {
    }

    public Builder enabled(final boolean enabled) {
      this.enabled = enabled;
      return this;
    }

    public Builder y(final int y) {
      this.y = y;
      return this;
    }

    public Builder mainBlock(final String material) {
      this.material = material;
      return this;
    }

    public Builder borderBlock(final String borderMaterial) {
      this.borderMaterial = borderMaterial;
      return this;
    }

    public Builder pattern(final PlatformBorderPattern borderPattern) {
      this.borderPattern = borderPattern;
      return this;
    }

    public Builder chunkRadius(final int chunkRadius) {
      this.chunkRadius = chunkRadius;
      return this;
    }

    /**
     * Builds the configuration.
     *
     * @return a new configuration, never null
     */
    public PlatformConfig build() {
      return new PlatformConfig(
        this.enabled,
        this.y,
        this.material,
        this.borderMaterial,
        this.borderPattern,
        this.chunkRadius);
    }
  }
}