package me.kvdpxne.vcg.internal;

import me.kvdpxne.vcg.api.PlatformConfig;
import org.bukkit.World;

public interface PlatformGen {

  /** Dla byte-based sections (ID 0..255). */
  void generate(byte[][] sections, World world, int chunkX, int chunkZ, PlatformConfig config);

  /** Dla short-based sections (ID 0..4095). */
  void generateExt(short[][] sections, World world, int chunkX, int chunkZ, PlatformConfig config);
}