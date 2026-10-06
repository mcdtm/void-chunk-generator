package me.kvdpxne.vcg.internal;

import me.kvdpxne.vcg.api.VoidWorldConfig;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Odpowiedzialnosc: rozwiazywanie konfiguracji dla swiata.
 * Priorytet: per-swiatowe nadpisanie (API) > domyslna (config.properties).
 */
public final class ConfigResolver {

  private final ConcurrentHashMap<String, VoidWorldConfig> overrides;
  private volatile VoidWorldConfig defaultConfig;
  private final Logger logger;

  public ConfigResolver(final VoidWorldConfig defaultConfig, final Logger logger) {
    this.overrides = new ConcurrentHashMap<>();
    this.defaultConfig = defaultConfig;
    this.logger = logger;
  }

  public void setDefault(final VoidWorldConfig config) {
    this.defaultConfig = config;
  }

  public VoidWorldConfig defaultConfig() {
    return this.defaultConfig;
  }

  public void registerOverride(final String worldName, final VoidWorldConfig config) {
    this.overrides.put(worldName, config);
    this.logger.info("Zarejestrowano nadpisanie konfiguracji dla: " + worldName);
  }

  public Optional<VoidWorldConfig> removeOverride(final String worldName) {
    return Optional.ofNullable(this.overrides.remove(worldName));
  }

  public Optional<VoidWorldConfig> findOverride(final String worldName) {
    return Optional.ofNullable(this.overrides.get(worldName));
  }

  public VoidWorldConfig resolve(final String worldName) {
    return this.overrides.getOrDefault(worldName, this.defaultConfig);
  }

  public Set<String> overriddenWorlds() {
    return Set.copyOf(this.overrides.keySet());
  }
}