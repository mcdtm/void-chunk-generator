package me.kvdpxne.vcg.internal.validation;

import me.kvdpxne.vcg.api.PlatformConfig;
import me.kvdpxne.vcg.api.SpawnOffset;
import me.kvdpxne.vcg.api.VoidWorldConfig;

import java.util.Objects;

/**
 * Walidacja konfiguracji. Stateless – bezpieczny do wywolania z dowolnego watku.
 */
public final class ConfigValidator {

  private static final int MIN_PLATFORM_Y = 1;
  private static final int MAX_PLATFORM_Y = 254;
  private static final int MAX_PLATFORM_RADIUS = 8;

  private ConfigValidator() {
    // utility class – nie instancjonowac
  }

  public static void validateWorldName(final String worldName) {
    Objects.requireNonNull(worldName, "worldName cannot be null");
    if (worldName.isBlank()) {
      throw new ValidationException("worldName cannot be blank");
    }
  }

  public static void validateConfig(final VoidWorldConfig config) {
    Objects.requireNonNull(config, "config cannot be null");
    Objects.requireNonNull(config.biome(), "config.biome cannot be null");
    Objects.requireNonNull(config.platform(), "config.platform cannot be null");
    Objects.requireNonNull(config.spawnLocation(), "config.spawnLocation cannot be null");

    config.platform().ifPresent(ConfigValidator::validatePlatform);
    config.spawnLocation().ifPresent(ConfigValidator::validateSpawnOffset);
  }

  private static void validatePlatform(final PlatformConfig platform) {
    if (platform.y() < MIN_PLATFORM_Y || platform.y() > MAX_PLATFORM_Y) {
      throw new ValidationException(
        "platform.y must be in [" + MIN_PLATFORM_Y + ", " + MAX_PLATFORM_Y
          + "], got: " + platform.y());
    }
    if (platform.chunkRadius() < 0 || platform.chunkRadius() > MAX_PLATFORM_RADIUS) {
      throw new ValidationException(
        "platform.chunkRadius must be in [0, " + MAX_PLATFORM_RADIUS
          + "], got: " + platform.chunkRadius());
    }
    Objects.requireNonNull(platform.mainBlock(), "platform.mainBlock cannot be null");
    Objects.requireNonNull(platform.borderBlock(), "platform.borderBlock cannot be null");
    Objects.requireNonNull(platform.pattern(), "platform.pattern cannot be null");
  }

  private static void validateSpawnOffset(final SpawnOffset offset) {
    if (offset.y() < 0.0 || offset.y() > 255.0) {
      throw new ValidationException(
        "spawnLocation.y must be in [0, 255], got: " + offset.y());
    }
  }
}