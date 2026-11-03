package me.kvd.vcg.internal.settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.kvd.configra.core.ConfigFile;
import me.kvd.configra.format.ini.IniFormat;
import me.kvd.configra.options.WriteOptions;
import me.kvd.vcg.api.rules.FixedSpawnConfig;
import me.kvd.vcg.api.rules.PlatformBorderPattern;
import me.kvd.vcg.api.rules.PlatformConfig;
import me.kvd.vcg.api.settings.PluginSettings;

public final class SettingsLoader {

  private static final String FILE_NAME = "settings.ini";

  private final Logger logger;

  public SettingsLoader(final Logger logger) {
    this.logger = logger;
  }

  /**
   * Zwraca konfigurację bez blokowania głównego wątku.
   * <ul>
   *     <li>Jeśli {@code settings.ini} istnieje – jest wczytywany <b>synchronicznie</b>
   *         i zwracana jest konfiguracja z pliku.</li>
   *     <li>Jeśli {@code settings.ini} nie istnieje – zwracane są natychmiast
   *         {@link SettingsDefaults#defaultSettings()}, a plik jest tworzony w tle
   *         na wirtualnym wątku, aby był gotowy przy następnym starcie.</li>
   * </ul>
   */
  public PluginSettings loadOrCreate(final Path dataFolder) {
    final var path = dataFolder.resolve(FILE_NAME);
    if (Files.exists(path)) {
      return this.loadSync(path);
    }
    this.createDefaultFileAsync(dataFolder);
    return PluginSettings.builder().build();
  }

  /**
   * Synchronicznie wczytuje istniejący plik INI i mapuje go na {@link PluginSettings}.
   */
  public PluginSettings loadSync(final Path path) {
    try {
      final ConfigFile config = ConfigFile.load(path, IniFormat.INSTANCE);
      return this.map(config);
    } catch (final Exception ex) {
      this.logger.log(Level.SEVERE, "Błąd podczas wczytywania " + FILE_NAME, ex);
      return PluginSettings.builder().build();
    }
  }

  /**
   * Synchronicznie wczytuje istniejący plik INI i mapuje go na {@link PluginSettings}.
   */
  public PluginSettings loadAsync(final Path path) {
    try {
      final ConfigFile config = ConfigFile.load(path, IniFormat.INSTANCE);
      return this.map(config);
    } catch (final Exception ex) {
      this.logger.log(Level.SEVERE, "Błąd podczas wczytywania " + FILE_NAME, ex);
      return PluginSettings.builder().build();
    }
  }

  /**
   * Tworzy plik domyślny w tle na wirtualnym wątku.
   * Zapis jest atomowy (tmp + ATOMIC_MOVE), więc nawet jeśli plugin zostanie
   * wyłączony w trakcie, nie pozostawi częściowo zapisanego pliku.
   */
  private void createDefaultFileAsync(final Path dataFolder) {
    Thread.ofVirtual()
      .name("vcg-config-init")
      .start(() -> {
        final Path target = dataFolder.resolve(FILE_NAME);
        final Path tmp = dataFolder.resolve(FILE_NAME + ".tmp");
        try {
          Files.createDirectories(dataFolder);
          SettingsDefaults.defaultSettingsFile().save(tmp, WriteOptions.ini());
          Files.move(tmp, target, StandardCopyOption.ATOMIC_MOVE);
          this.logger.info(FILE_NAME + " utworzony asynchronicznie.");
        } catch (final IOException ex) {
          this.logger.log(Level.SEVERE, "Nie można utworzyć " + FILE_NAME, ex);
          try {
            Files.deleteIfExists(tmp);
          } catch (final IOException ignored) {
            // best-effort cleanup
          }
        }
      });
  }

  /**
   * Mapowanie wczytanego {@link ConfigFile} na {@link PluginSettings}.
   */
  private PluginSettings map(final ConfigFile config) {
    return PluginSettings.builder()
      .biomeName(config.getOrElse(SettingsKeys.BIOME, "OCEAN"))
      .platform(
        PlatformConfig.builder()
          .enabled(config.getOrElse(SettingsKeys.PLATFORM_ENABLED, true))
          .y(config.getOrElse(SettingsKeys.PLATFORM_Y, 64))
          .chunkRadius(config.getOrElse(SettingsKeys.PLATFORM_CHUNK_RADIUS, 1))
          .mainBlock(config.getOrElse(SettingsKeys.PLATFORM_MAIN_BLOCK, "BEDROCK"))
          .borderBlock(config.getOrElse(SettingsKeys.PLATFORM_BORDER_BLOCK, "STONE"))
          .pattern(config.getOrElse(SettingsKeys.PLATFORM_PATTERN, PlatformBorderPattern.NONE))
          .build()
      )
      .fixedSpawn(
        FixedSpawnConfig.builder()
          .enabled(config.getOrElse(SettingsKeys.SPAWN_ENABLED, true))
          .x(config.getOrElse(SettingsKeys.SPAWN_X, 0.0D))
          .y(config.getOrElse(SettingsKeys.SPAWN_Y, 65.0D))
          .z(config.getOrElse(SettingsKeys.SPAWN_Z, 0.0D))
          .yaw(config.getOrElse(SettingsKeys.SPAWN_YAW, 0.0F))
          .pitch(config.getOrElse(SettingsKeys.SPAWN_PITCH, 0.0F))
          .build()
      )
      .metrics(config.getOrElse(SettingsKeys.METRICS_ENABLED, true))
      .build();
  }
}