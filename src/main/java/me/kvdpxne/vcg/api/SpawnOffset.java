package me.kvdpxne.vcg.api;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Fixed spawn offset, detached from any {@link World} reference.
 * Kept {@code World}-free so it can be persisted and passed around safely.
 */
public record SpawnOffset(
  double x,
  double y,
  double z,
  float yaw,
  float pitch
) {

  public SpawnOffset {
    if (0.0 > y || 255.0 < y) {
      throw new IllegalArgumentException("y must be in [0, 255], got: " + y);
    }
  }

  public static SpawnOffset of(final double x, final double y, final double z) {
    return new SpawnOffset(x, y, z, 0.0F, 0.0F);
  }

  public Location toLocation(final World world) {
    return new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
  }
}