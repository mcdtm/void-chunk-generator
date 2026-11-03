package me.kvd.vcg.api.rules;

/**
 * Immutable configuration for a fixed spawn location.
 * <p>
 * When enabled, every world receives the same spawn point
 * regardless of the world seed.
 */
public record FixedSpawnConfig(
  boolean enabled,
  double x,
  double y,
  double z,
  float yaw,
  float pitch
) {

  /**
   * Compact constructor with validation.
   *
   * @throws IllegalArgumentException if coordinates are outside
   *                                  the Minecraft world bounds
   */
  public FixedSpawnConfig {
    if (x < -30_000_000.0 || x > 30_000_000.0) {
      throw new IllegalArgumentException(
        "x must be in [-30000000, 30000000], got: " + x);
    }
    if (z < -30_000_000.0 || z > 30_000_000.0) {
      throw new IllegalArgumentException(
        "z must be in [-30000000, 30000000], got: " + z);
    }
    if (y < -64.0 || y > 320.0) {
      throw new IllegalArgumentException(
        "y must be in [-64, 320], got: " + y);
    }
  }

  /**
   * Returns a disabled fixed-spawn configuration.
   *
   * @return a disabled configuration, never null
   */
  public static FixedSpawnConfig disabled() {
    return new FixedSpawnConfig(false, 0.5, 64.0, 0.5, 0.0f, 0.0f);
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
   * Builder for {@link FixedSpawnConfig}.
   */
  public static final class Builder {

    private boolean enabled = false;
    private double x = 0.5;
    private double y = 64.0;
    private double z = 0.5;
    private float yaw = 0.0f;
    private float pitch = 0.0f;

    private Builder() {
    }

    public Builder enabled(final boolean enabled) {
      this.enabled = enabled;
      return this;
    }

    public Builder x(final double x) {
      this.x = x;
      return this;
    }

    public Builder y(final double y) {
      this.y = y;
      return this;
    }

    public Builder z(final double z) {
      this.z = z;
      return this;
    }

    public Builder yaw(final float yaw) {
      this.yaw = yaw;
      return this;
    }

    public Builder pitch(final float pitch) {
      this.pitch = pitch;
      return this;
    }

    /**
     * Builds the configuration.
     *
     * @return a new configuration, never null
     */
    public FixedSpawnConfig build() {
      return new FixedSpawnConfig(this.enabled, this.x, this.y, this.z, this.yaw, this.pitch);
    }
  }
}