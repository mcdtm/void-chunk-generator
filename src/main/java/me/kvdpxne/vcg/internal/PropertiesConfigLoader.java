package me.kvdpxne.vcg.internal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.block.Biome;

import me.kvdpxne.vcg.api.BorderPattern;
import me.kvdpxne.vcg.api.PlatformConfig;
import me.kvdpxne.vcg.api.SpawnOffset;
import me.kvdpxne.vcg.api.VoidWorldConfig;

/**
 * Loads {@code settings.properties} into {@link VoidWorldConfig}.
 * Every parse failure is logged and replaced by a sensible default -
 * the loader never throws for malformed user input.
 */
public final class PropertiesConfigLoader {

  private static final String FILE_NAME = "settings.properties";

  private static final String KEY_BIOME = "biome";

  private static final String KEY_PLATFORM_ENABLED = "platform-enabled";
  private static final String KEY_PLATFORM_Y = "platform-y";
  private static final String KEY_PLATFORM_RADIUS = "platform-chunk-radius";
  private static final String KEY_PLATFORM_MAIN = "platform-main-block";
  private static final String KEY_PLATFORM_BORDER = "platform-border-block";
  private static final String KEY_PLATFORM_PATTERN = "platform-pattern";

  private static final String KEY_SPAWN_ENABLED = "spawn-enabled";
  private static final String KEY_SPAWN_X = "spawn-x";
  private static final String KEY_SPAWN_Y = "spawn-y";
  private static final String KEY_SPAWN_Z = "spawn-z";
  private static final String KEY_SPAWN_YAW = "spawn-yaw";
  private static final String KEY_SPAWN_PITCH = "spawn-pitch";

  private final Logger logger;

  public PropertiesConfigLoader(final Logger logger) {
    if (null == logger) {
      throw new NullPointerException("logger must not be null");
    }
    this.logger = logger;
  }

  /** Copies the bundled {@code settings.properties} into the data folder if missing. */
  public Path ensureFile(final Path dataFolder) {
    if (null == dataFolder) {
      throw new NullPointerException("dataFolder must not be null");
    }
    try {
      if (!Files.exists(dataFolder)) {
        Files.createDirectories(dataFolder);
      }
      final var target = dataFolder.resolve(FILE_NAME);
      if (Files.exists(target)) {
        return target;
      }
      try (final InputStream in = this.getClass().getClassLoader()
        .getResourceAsStream(FILE_NAME)) {
        if (null == in) {
          this.logger.warning(FILE_NAME + " missing from JAR - using in-code defaults.");
          return target;
        }
        Files.copy(in, target);
      }
      return target;
    } catch (final IOException ex) {
      this.logger.log(Level.SEVERE, "Could not create " + FILE_NAME, ex);
      return dataFolder.resolve(FILE_NAME);
    }
  }

  public VoidWorldConfig load(final Path path) {
    if (null == path) {
      throw new NullPointerException("path must not be null");
    }
    if (!Files.exists(path)) {
      this.logger.warning(FILE_NAME + " not found - using in-code defaults.");
      return VoidWorldConfig.defaults();
    }

    final var props = new Properties();
    try (final InputStream in = Files.newInputStream(path)) {
      props.load(in);
    } catch (final IOException ex) {
      this.logger.log(Level.SEVERE, "Could not read " + FILE_NAME, ex);
      return VoidWorldConfig.defaults();
    }

    final var builder = VoidWorldConfig.builder()
      .biome(this.biome(props, KEY_BIOME, Biome.OCEAN));

    if (this.bool(props, KEY_PLATFORM_ENABLED, true)) {
      builder.platform(this.platform(props));
    }
    if (this.bool(props, KEY_SPAWN_ENABLED, true)) {
      builder.spawnLocation(this.spawn(props));
    }

    return builder.build();
  }

  // ------------------------------------------------------------------
  //  Parsers - every method logs on failure and returns a fallback.
  // ------------------------------------------------------------------

  private PlatformConfig platform(final Properties props) {
    return PlatformConfig.builder()
      .y(this.intVal(props, KEY_PLATFORM_Y, 64))
      .chunkRadius(this.intVal(props, KEY_PLATFORM_RADIUS, 1))
      .mainBlock(this.material(props, KEY_PLATFORM_MAIN, Material.STONE))
      .borderBlock(this.material(props, KEY_PLATFORM_BORDER, Material.BEDROCK))
      .pattern(this.borderPattern(props, KEY_PLATFORM_PATTERN, BorderPattern.SPAWN_MARKER))
      .build();
  }

  private SpawnOffset spawn(final Properties props) {
    return new SpawnOffset(
      this.doubleVal(props, KEY_SPAWN_X, 0.5),
      this.doubleVal(props, KEY_SPAWN_Y, 65.0),
      this.doubleVal(props, KEY_SPAWN_Z, 0.5),
      (float) this.doubleVal(props, KEY_SPAWN_YAW, 0.0),
      (float) this.doubleVal(props, KEY_SPAWN_PITCH, 0.0));
  }

  private Biome biome(final Properties props, final String key, final Biome fallback) {
    final var raw = props.getProperty(key);
    if (null == raw) {
      return fallback;
    }
    try {
      return Biome.valueOf(raw.trim().toUpperCase());
    } catch (final IllegalArgumentException ex) {
      this.logger.warning("Unknown biome '" + raw + "' - using " + fallback);
      return fallback;
    }
  }

  private Material material(final Properties props, final String key, final Material fallback) {
    final var raw = props.getProperty(key);
    if (null == raw) {
      return fallback;
    }
    try {
      return Material.valueOf(raw.trim().toUpperCase());
    } catch (final IllegalArgumentException ex) {
      this.logger.warning("Unknown material '" + raw + "' - using " + fallback);
      return fallback;
    }
  }

  private BorderPattern borderPattern(final Properties props, final String key,
                                      final BorderPattern fallback) {
    final var raw = props.getProperty(key);
    if (null == raw) {
      return fallback;
    }
    try {
      return BorderPattern.valueOf(raw.trim().toUpperCase());
    } catch (final IllegalArgumentException ex) {
      this.logger.warning("Unknown pattern '" + raw + "' - using " + fallback);
      return fallback;
    }
  }

  private boolean bool(final Properties props, final String key, final boolean fallback) {
    final var raw = props.getProperty(key);
    return null == raw ? fallback : Boolean.parseBoolean(raw.trim());
  }

  private int intVal(final Properties props, final String key, final int fallback) {
    final var raw = props.getProperty(key);
    if (null == raw) {
      return fallback;
    }
    try {
      return Integer.parseInt(raw.trim());
    } catch (final NumberFormatException ex) {
      this.logger.warning("Invalid int for '" + key + "' = " + raw + " - using " + fallback);
      return fallback;
    }
  }

  private double doubleVal(final Properties props, final String key, final double fallback) {
    final var raw = props.getProperty(key);
    if (null == raw) {
      return fallback;
    }
    try {
      return Double.parseDouble(raw.trim());
    } catch (final NumberFormatException ex) {
      this.logger.warning("Invalid double for '" + key + "' = " + raw + " - using " + fallback);
      return fallback;
    }
  }
}