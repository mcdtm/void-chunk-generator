package me.kvdpxne.vcg.internal;

import org.bukkit.World;

import me.kvdpxne.vcg.api.PlatformConfig;

/**
 * Strategy for carving a platform into a chunk's section array.
 * Two variants cover the byte and short section formats used by Bukkit 1.7.10.
 */
public interface PlatformGen {

  void generate(byte[][] sections, World world, int chunkX, int chunkZ, PlatformConfig config);

  void generateExt(short[][] sections, World world, int chunkX, int chunkZ, PlatformConfig config);
}