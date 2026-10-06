package me.kvdpxne.vcg.api;

import org.bukkit.Location;
import org.bukkit.World;

public record SpawnOffset(double x, double y, double z, float yaw, float pitch) {

  public static SpawnOffset of(final double x, final double y, final double z) {
    return new SpawnOffset(x, y, z, 0.0F, 0.0F);
  }

  public Location toLocation(final World world) {
    return new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
  }
}