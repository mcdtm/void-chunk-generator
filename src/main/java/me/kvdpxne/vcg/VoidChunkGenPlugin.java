package me.kvdpxne.vcg;

import java.util.Objects;
import me.kvdpxne.vcg.api.VoidChunkGenApi;
import me.kvdpxne.vcg.internal.*;
import org.bukkit.Bukkit;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Entry point.
 *
 * <p>Standalone mode: worlds declared in {@code bukkit.yml} with
 * {@code generator: VoidChunkGenerator} receive the shared config from
 * {@code settings.properties}.</p>
 *
 * <p>Managed mode: a consumer (e.g. DimensionManager) registers per-world
 * overrides through {@link VoidChunkGenApi}.</p>
 */
public final class VoidChunkGenPlugin extends JavaPlugin {

  private ConfigResolver resolver;
  private PlatformGen platformGenerator;
  private VoidChunkGenApi api;

  @Override
  public void onLoad() {
    final var logger = this.getLogger();

    final var loader = new PropertiesConfigLoader(logger);
    final var configPath = loader.ensureFile(this.getDataFolder().toPath());
    final var defaultConfig = loader.load(configPath);

    this.resolver = new ConfigResolver(defaultConfig, logger);
    this.platformGenerator = new DefaultPlatformGen(logger);
    this.api = new VoidChunkGenApiImpl(this.resolver);

    logger.info("onLoad completed (default biome: " + defaultConfig.biome() + ").");
  }

  @Override
  public void onEnable() {
    this.getServer().getServicesManager().register(
      VoidChunkGenApi.class, this.api, this, ServicePriority.Normal);

    this.getServer().getPluginManager().registerEvents(
      new WorldLifecycleListener(this.resolver, this.getLogger()), this);

    this.getLogger().info("VoidChunkGenerator enabled (API v"
      + VoidChunkGenApi.API_VERSION + ").");
  }

  @Override
  public void onDisable() {
    this.getServer().getServicesManager().unregisterAll(this);
    this.getLogger().info("VoidChunkGenerator disabled.");
  }

  @Override
  public ChunkGenerator getDefaultWorldGenerator(final String worldName, final String id) {
    Objects.requireNonNull(worldName, "worldName");
    final var config = this.resolver.resolve(worldName);
    this.getLogger().info("Attaching generator for world: " + worldName
      + " (biome: " + config.biome()
      + ", platform: " + config.platform().isPresent()
      + ", override: " + this.resolver.findOverride(worldName).isPresent() + ").");
    return new VoidChunkGen(config, this.platformGenerator, this.getLogger());
  }

  /**
   * Reloads {@code settings.properties} and updates the default config.
   * Per-world overrides stay untouched.
   */
  public void reloadDefaultConfig() {
    final var loader = new PropertiesConfigLoader(this.getLogger());
    final var path = loader.ensureFile(this.getDataFolder().toPath());
    final var newDefault = loader.load(path);
    this.resolver.setDefault(newDefault);
    this.getLogger().info("Default configuration reloaded (biome: "
      + newDefault.biome() + ").");
  }
}