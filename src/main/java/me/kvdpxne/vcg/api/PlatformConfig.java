package me.kvdpxne.vcg.api;

import org.bukkit.Material;

public record PlatformConfig(
  int y,
  int chunkRadius,
  Material mainBlock,
  Material borderBlock,
  BorderPattern pattern
) {

  public static final PlatformConfig DEFAULT = new PlatformConfig(
    64, 1, Material.STONE, Material.BEDROCK, BorderPattern.SPAWN_MARKER);
}