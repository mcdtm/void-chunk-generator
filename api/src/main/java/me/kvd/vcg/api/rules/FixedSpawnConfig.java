package me.kvd.vcg.api.rules;

import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Fixed fixedSpawn offset, detached from any {@link World} reference.
 * Kept {@code World}-free so it can be persisted and passed around safely.
 */
public record FixedSpawnConfig(
  double x,
  double y,
  double z,
  float yaw,
  float pitch
) {

  public FixedSpawnConfig {
    if (0.0 > y || 255.0 < y) {
      throw new IllegalArgumentException("y must be in [0, 255], got: " + y);
    }
  }

  public Location toLocation(final World world) {
    Objects.requireNonNull(world, "world");
    return new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
  }
}