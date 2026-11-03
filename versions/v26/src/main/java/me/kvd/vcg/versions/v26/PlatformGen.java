package me.kvd.vcg.versions.v26;

import me.kvd.vcg.api.rules.PlatformConfig;
import org.bukkit.generator.ChunkGenerator;

/**
 * Strategy for generating the platform block under the spawn in modern versions.
 */
public interface PlatformGen {

  /**
   * Places the platform blocks into the given {@link ChunkGenerator.ChunkData}.
   *
   * @param chunkData the chunk data to modify, must not be null
   * @param chunkX    the chunk X coordinate
   * @param chunkZ    the chunk Z coordinate
   * @param config    the platform configuration, must not be null
   */
  void generatePlatform(ChunkGenerator.ChunkData chunkData,
                        int chunkX, int chunkZ,
                        PlatformConfig config);
}