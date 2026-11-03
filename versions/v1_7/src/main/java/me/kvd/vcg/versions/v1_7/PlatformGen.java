package me.kvd.vcg.versions.v1_7;

import me.kvd.vcg.api.rules.PlatformConfig;

/**
 * Strategy for generating the platform block under the spawn.
 */
public interface PlatformGen {

  /**
   * Generates the block data for the platform at the given chunk.
   *
   * @param chunkX the chunk X coordinate
   * @param chunkZ the chunk Z coordinate
   * @param config the platform configuration, must not be null
   * @return a byte array containing the block data (id + metadata packed),
   *         never null
   */
  byte[] generatePlatform(int chunkX, int chunkZ, PlatformConfig config);
}