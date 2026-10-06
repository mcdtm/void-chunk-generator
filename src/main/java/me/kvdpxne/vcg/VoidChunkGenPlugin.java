package me.kvdpxne.vcg; // docelowa grupa (przyszlosc)

import me.kvdpxne.vcg.api.VoidChunkGenApi;
import me.kvdpxne.vcg.api.VoidWorldConfig;
import me.kvdpxne.vcg.internal.*;
import me.kvdpxne.vcg.internal.listeners.BedListener;
import me.kvdpxne.vcg.internal.listeners.SpawnListener;
import me.kvdpxne.vcg.internal.listeners.WeatherListener;
import me.kvdpxne.vcg.internal.listeners.WorldLifecycleListener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Klasa glowna pluginu.
 * <p>
 * Tryby dzialania:
 * 1) Standalone - swiaty wskazane w bukkit.yml, config.properties jako
 * wspolna konfiguracja dla wszystkich swiatow void.
 * 2) Z DimensionManager - per-swiatowe nadpisania przez API (w pamieci).
 * <p>
 * Plugin nie zarzadza cyklem zycia swiatow - to rola DimensionManager
 * (lub bukkit.yml w trybie standalone).
 */
public final class VoidChunkGenPlugin extends JavaPlugin {

  private ConfigResolver configResolver;
  private PlatformGen platformGen;
  private WorldSettingsApplier settingsApplier;
  private VoidChunkGenApi api;

  @Override
  public void onLoad() {
    final var logger = this.getLogger();

    final var loader = new PropertiesConfigLoader(logger);
    final var configPath = loader.ensureFile(
      this.getDataFolder().toPath(), "config.properties");
    final VoidWorldConfig defaultConfig = loader.load(configPath);

    this.configResolver = new ConfigResolver(defaultConfig, logger);
    this.platformGen = new DefaultPlatformGen(logger);
    this.settingsApplier = new WorldSettingsApplier(logger);

    this.api = new VoidChunkGenApiImpl(this.configResolver, this.settingsApplier);

    logger.info("onLoad zakonczony (biom domyslny: " + defaultConfig.biome() + ")");
  }

  @Override
  public void onEnable() {
    final var pm = this.getServer().getPluginManager();

    this.getServer().getServicesManager().register(
      VoidChunkGenApi.class, this.api, this, ServicePriority.Normal);

    pm.registerEvents(new SpawnListener(this.configResolver), this);
    pm.registerEvents(new WeatherListener(this.configResolver), this);
    pm.registerEvents(new BedListener(this.configResolver), this);
    pm.registerEvents(new WorldLifecycleListener(this.configResolver, this.getLogger()), this);

    this.getLogger().info("VoidChunkGenerator wlaczony (API v"
      + VoidChunkGenApi.API_VERSION + ").");
  }

  @Override
  public void onDisable() {
    this.getServer().getServicesManager().unregisterAll(this);
    this.getLogger().info("VoidChunkGenerator wylaczony.");
  }

  @Override
  public ChunkGenerator getDefaultWorldGenerator(final String worldName, final String id) {
    final var config = this.configResolver.resolve(worldName);
    this.getLogger().info("Podpinam generator dla: " + worldName
      + " (biom: " + config.biome()
      + ", platforma: " + config.platform().isPresent()
      + ", override: " + this.configResolver.findOverride(worldName).isPresent() + ")");
    return new VoidChunkGen(config, this.platformGen, this.getLogger());
  }

}