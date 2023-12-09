package me.kvdpxne.freeverse;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

public class VoidChunkGenerator
  extends ChunkGenerator {

//  private static final int[] BEDROCK_POSITIONS = {
//    0x0, 0x0,
//    0xf, 0xf,
//    0xf, 0x0,
//    0x0, 0xf
//  };

  @Override
  public void generateBedrock(
    final WorldInfo worldInfo,
    final Random random,
    final int chunkX,
    final int chunkZ,
    final ChunkGenerator.ChunkData chunkData
  ) {
    if (0 == chunkX && -1 == chunkZ) {
      chunkData.setBlock(0, 63, 15, Material.BEDROCK);
      return;
    }

    if (-1 == chunkX && 0 == chunkZ) {
      chunkData.setBlock(15, 63, 0, Material.BEDROCK);
      return;
    }

    if (0 == chunkX && 0 == chunkZ) {
      chunkData.setBlock(0, 63, 0, Material.BEDROCK);
      return;
    }

    if (-1 == chunkX && -1 == chunkZ) {
      chunkData.setBlock(15, 63, 15, Material.BEDROCK);
    }
  }

  @Override
  public BiomeProvider getDefaultBiomeProvider(
    final WorldInfo worldInfo
  ) {
    return new VoidBiomeProvider();
  }

  @Override
  public Location getFixedSpawnLocation(
    final World world,
    final Random random
  ) {
    return new Location(world, 0d, 64.2d, 0d);
  }
}
