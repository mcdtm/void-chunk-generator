package me.kvd.vcg;

import java.util.Objects;
import me.kvd.vcg.api.VoidChunkGeneratorApi;
import me.kvd.vcg.api.settings.PluginSettings;
import me.kvd.vcg.internal.VoidChunkGenApiImpl;
import me.kvd.vcg.internal.settings.SettingsLoader;
import me.kvd.vcg.internal.utils.ConfigResolver;
import me.kvd.vcg.internal.utils.WorldLifecycleListener;
import me.kvd.vcg.internal.utils.metrics.MetricsBootstrap;
import me.kvd.vcg.versions.v1_7.DefaultPlatformGen;
import me.kvd.vcg.versions.v1_7.PlatformGen;
import me.kvd.vcg.versions.v1_7.VoidChunkGen;
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
 * overrides through {@link VoidChunkGeneratorApi}.</p>
 */
public final class VoidChunkGenPlugin extends JavaPlugin {

  private ConfigResolver resolver;
  private PlatformGen platformGenerator;
  private VoidChunkGeneratorApi api;

  private PluginSettings settings;

  @Override
  public void onLoad() {
    final var logger = this.getLogger();
    final var config = (new SettingsLoader(logger))
      .loadOrCreate(this.getDataFolder().toPath());

    this.settings = config;

    this.resolver = new ConfigResolver(config, logger);
    this.platformGenerator = new DefaultPlatformGen(logger);
    this.api = new VoidChunkGenApiImpl(this.resolver);

    logger.info("onLoad completed (default biome: " + config.biomeName() + ").");
  }

  @Override
  public void onEnable() {
    this.getServer().getServicesManager().register(
      VoidChunkGeneratorApi.class, this.api, this, ServicePriority.Normal);

    this.getServer().getPluginManager().registerEvents(
      new WorldLifecycleListener(this.resolver, this.getLogger()), this);

    //
    new MetricsBootstrap(this, this.settings).start();

    this.getLogger().fine(() -> String.format(
      "Public API (v%s) registered under %s.",
      VoidChunkGeneratorApi.API_VERSION, VoidChunkGeneratorApi.class.getName()
    ));
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
      + " (biome: " + config.biomeName()
      + ", platform: " + config.platformEnabled()
      + ", override: " + this.resolver.findOverride(worldName).isPresent() + ").");
    return new VoidChunkGen(config, this.platformGenerator, this.getLogger());
  }
}