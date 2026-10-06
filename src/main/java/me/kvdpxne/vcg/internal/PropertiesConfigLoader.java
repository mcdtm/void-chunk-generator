package me.kvdpxne.vcg.internal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.Logger;

import me.kvdpxne.vcg.api.*;
import org.bukkit.Material;
import org.bukkit.block.Biome;

/**
 * Odpowiedzialnosc: wczytanie config.properties i zbudowanie VoidWorldConfig.
 * Niepoprawne wartosci sa logowane i zastepowane domyslnymi.
 */
public final class PropertiesConfigLoader {

  private final Logger logger;

  public PropertiesConfigLoader(final Logger logger) {
    this.logger = logger;
  }

  public Path ensureFile(final Path dataFolder, final String resourceName) {
    try {
      if (!Files.exists(dataFolder)) {
        Files.createDirectories(dataFolder);
      }
      final var target = dataFolder.resolve(resourceName);
      if (Files.exists(target)) {
        return target;
      }
      try (final InputStream in = this.getClass().getClassLoader()
        .getResourceAsStream(resourceName)) {
        if (in == null) {
          this.logger.warning("Brak " + resourceName + " w JAR - uzywam domyslnych wartosci.");
          return target;
        }
        Files.copy(in, target);
      }
      return target;
    } catch (final IOException ex) {
      this.logger.severe("Nie udalo sie utworzyc " + resourceName + ": " + ex.getMessage());
      return dataFolder.resolve(resourceName);
    }
  }

  public VoidWorldConfig load(final Path path) {
    final var props = new Properties();
    if (Files.exists(path)) {
      try (final InputStream in = Files.newInputStream(path)) {
        props.load(in);
      } catch (final IOException ex) {
        this.logger.severe("Blad wczytywania " + path.getFileName() + ": " + ex.getMessage());
        return VoidWorldConfig.DEFAULT;
      }
    } else {
      this.logger.warning("Plik " + path.getFileName() + " nie istnieje - uzywam domyslnych wartosci.");
      return VoidWorldConfig.DEFAULT;
    }

    final var builder = new VoidWorldConfig.Builder()
      .biome(this.biome(props, "biome", Biome.OCEAN))
      .precipitation(this.bool(props, "allowPrecipitation", false))
      .passiveMobs(this.bool(props, "allowPassiveMobs", false))
      .hostileMobs(this.bool(props, "allowHostileMobs", false))
      .neutralMobs(this.bool(props, "allowNeutralMobs", false))
      .dayNightCycle(this.bool(props, "enableDayNightCycle", false));

    if (this.bool(props, "platformEnabled", true)) {
      builder.platform(new PlatformConfig(
        this.intVal(props, "platformY", 64),
        this.intVal(props, "platformChunkRadius", 2),
        this.material(props, "platformMainBlock", Material.STONE),
        this.material(props, "platformBorderBlock", Material.BEDROCK),
        this.borderPattern(props, "platformPattern", BorderPattern.SPAWN_MARKER)
      ));
    }

    if (this.bool(props, "spawnEnabled", true)) {
      builder.spawnLocation(new SpawnOffset(
        this.doubleVal(props, "spawnX", 0.0),
        this.doubleVal(props, "spawnY", 65.0),
        this.doubleVal(props, "spawnZ", 0.0),
        (float) this.doubleVal(props, "spawnYaw", 0.0),
        (float) this.doubleVal(props, "spawnPitch", 0.0)
      ));
    }

    return builder.build();
  }

  private Biome biome(final Properties p, final String key, final Biome fallback) {
    final var raw = p.getProperty(key);
    if (raw == null) return fallback;
    try {
      return Biome.valueOf(raw.trim().toUpperCase());
    } catch (final IllegalArgumentException ex) {
      this.logger.warning("Nieznany biom '" + raw + "' - uzywam " + fallback);
      return fallback;
    }
  }

  private Material material(final Properties p, final String key, final Material fallback) {
    final var raw = p.getProperty(key);
    if (raw == null) return fallback;
    try {
      return Material.valueOf(raw.trim().toUpperCase());
    } catch (final IllegalArgumentException ex) {
      this.logger.warning("Nieznany material '" + raw + "' - uzywam " + fallback);
      return fallback;
    }
  }

  private BorderPattern borderPattern(final Properties p, final String key, final BorderPattern fallback) {
    final var raw = p.getProperty(key);
    if (raw == null) return fallback;
    try {
      return BorderPattern.valueOf(raw.trim().toUpperCase());
    } catch (final IllegalArgumentException ex) {
      this.logger.warning("Nieznany pattern '" + raw + "' - uzywam " + fallback);
      return fallback;
    }
  }

  private boolean bool(final Properties p, final String key, final boolean fallback) {
    final var raw = p.getProperty(key);
    return raw == null ? fallback : Boolean.parseBoolean(raw.trim());
  }

  private int intVal(final Properties p, final String key, final int fallback) {
    final var raw = p.getProperty(key);
    if (raw == null) return fallback;
    try {
      return Integer.parseInt(raw.trim());
    } catch (final NumberFormatException ex) {
      this.logger.warning("Nieprawidlowa liczba w '" + key + "' = " + raw + " - uzywam " + fallback);
      return fallback;
    }
  }

  private double doubleVal(final Properties p, final String key, final double fallback) {
    final var raw = p.getProperty(key);
    if (raw == null) return fallback;
    try {
      return Double.parseDouble(raw.trim());
    } catch (final NumberFormatException ex) {
      this.logger.warning("Nieprawidlowa liczba w '" + key + "' = " + raw + " - uzywam " + fallback);
      return fallback;
    }
  }
}