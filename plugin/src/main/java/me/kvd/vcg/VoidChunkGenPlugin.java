package me.kvd.vcg;

import java.util.Objects;
import java.util.logging.Level;
import me.kvd.vcg.api.VoidChunkGeneratorApi;
import me.kvd.vcg.api.settings.PluginSettings;
import me.kvd.vcg.internal.VoidChunkGeneratorApiImpl;
import me.kvd.vcg.internal.command.VoidChunkGenCommand;
import me.kvd.vcg.internal.settings.SettingsLoader;
import me.kvd.vcg.internal.utils.ConfigResolver;
import me.kvd.vcg.internal.utils.WorldLifecycleListener;
import me.kvd.vcg.versions.v1_7.DefaultPlatformGen;
import me.kvd.vcg.versions.v1_7.PlatformGen;
import org.bukkit.Bukkit;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Entry point of VoidChunkGenerator.
 * <p>
 * Standalone mode: worlds declared in {@code bukkit.yml} with
 * {@code generator: VoidChunkGenerator} receive the shared config from
 * {@code settings.properties}.
 * <p>
 * Managed mode: a consumer (e.g. DimensionManager) registers per-world
 * overrides through {@link VoidChunkGeneratorApi}.
 */
public final class VoidChunkGenPlugin extends JavaPlugin {

  private ConfigResolver resolver;
  private PlatformGen platformGenerator;
  private VoidChunkGeneratorApi api;
  private PluginSettings settings;

  @Override
  public void onLoad() {
    final var logger = this.getLogger();
    final var loaded = new SettingsLoader(logger)
      .loadOrCreate(this.getDataFolder().toPath());
    this.settings = loaded;
    this.resolver = new ConfigResolver(loaded, logger);
    this.platformGenerator = new DefaultPlatformGen(this.settings, logger);
    this.api = new VoidChunkGeneratorApiImpl(this.resolver);
    logger.fine("onLoad completed (default biome: " + loaded.biomeName() + ").");
  }

  @Override
  public void onEnable() {
    this.getServer().getServicesManager().register(
      VoidChunkGeneratorApi.class,
      this.api,
      this,
      ServicePriority.Normal);
    this.getServer().getPluginManager().registerEvents(
      new WorldLifecycleListener(this.resolver, this.getLogger()), this);

    final var command = this.getCommand("voidchunkgen");
    if (null != command) {
      final var executor = new VoidChunkGenCommand(this);
      command.setExecutor(executor);
      command.setTabCompleter(executor);
    } else {
      this.getLogger().warning(
        "Command 'voidchunkgen' is not declared in plugin.yml");
    }
    this.getLogger().fine(() -> String.format(
      "Public API (v%s) registered under %s.",
      VoidChunkGeneratorApi.API_VERSION,
      VoidChunkGeneratorApi.class.getName()));
  }

  @Override
  public void onDisable() {
    this.getServer().getServicesManager().unregisterAll(this);
    this.resolver = null;
    this.platformGenerator = null;
    this.api = null;
    this.settings = null;
  }

  @Override
  public ChunkGenerator getDefaultWorldGenerator(final String worldName, final String id) {
    Objects.requireNonNull(worldName, "worldName");
    final var config = this.resolver.resolve(worldName);
    final var generator = this.createGenerator(config);
    this.getLogger().fine(() -> "Attaching generator for world: "
      + worldName + " (biome: " + config.biomeName()
      + ", platform: " + config.platform().enabled()
      + ", override: "
      + this.resolver.findOverride(worldName).isPresent() + ").");
    return generator;
  }

  private ChunkGenerator createGenerator(final PluginSettings config) {
    if (isModernVersion()) {
      return new me.kvd.vcg.versions.v26.VoidChunkGen(
        config,
        new me.kvd.vcg.versions.v26.DefaultPlatformGen(config, this.getLogger()),
        this.getLogger());
    }
    return new me.kvd.vcg.versions.v1_7.VoidChunkGen(
      config,
      new me.kvd.vcg.versions.v1_7.DefaultPlatformGen(this.settings, this.getLogger()),
      this.getLogger());
  }

  private boolean isModernVersion() {
    final String version = Bukkit.getBukkitVersion();
    // Przykład: "1.21-R0.1-SNAPSHOT" lub "26.1.2-R0.1-SNAPSHOT"
    final String[] parts = version.split("[-.]");
    if (parts.length < 2) {
      return true;
    }
    try {
      final int major = Integer.parseInt(parts[0]);
      final int minor = Integer.parseInt(parts[1]);
      return major > 1 || (1 == major && minor >= 18);
    } catch (final NumberFormatException e) {
      this.getLogger().warning(() -> "Could not parse version: " + version
        + ", assuming modern API");
      return true;
    }
  }
}