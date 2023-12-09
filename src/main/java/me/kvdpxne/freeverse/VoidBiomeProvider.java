package me.kvdpxne.freeverse;

import java.util.Collections;
import java.util.List;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;

public class VoidBiomeProvider
  extends BiomeProvider {

  @Override
  public Biome getBiome(
    final WorldInfo worldInfo,
    final int x,
    final int y,
    final int z
  ) {
    return Biome.THE_VOID;
  }

  @Override
  public List<Biome> getBiomes(
    final WorldInfo worldInfo
  ) {
    return Collections.singletonList(Biome.THE_VOID);
  }
}
