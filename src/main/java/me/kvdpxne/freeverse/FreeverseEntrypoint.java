package me.kvdpxne.freeverse;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public final class FreeverseEntrypoint
  extends JavaPlugin {

  @Override
  public ChunkGenerator getDefaultWorldGenerator(
    final String worldName,
    final String id
  ) {
    return new VoidChunkGenerator();
  }
}
