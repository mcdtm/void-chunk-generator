package me.kvd.vcg.internal;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import me.kvd.vcg.api.VoidChunkGeneratorApi;
import me.kvd.vcg.api.settings.PluginSettings;
import me.kvd.vcg.internal.utils.ConfigResolver;

/**
 * Internal implementation of {@link VoidChunkGeneratorApi}.
 * <p>
 * Exposed only through the {@code ServicesManager} - consumers never see this class.
 */
public final class VoidChunkGeneratorApiImpl implements VoidChunkGeneratorApi {

  private final ConfigResolver resolver;

  /**
   * Creates a new API implementation.
   *
   * @param resolver the configuration resolver, must not be null
   * @throws NullPointerException if {@code resolver} is null
   */
  public VoidChunkGeneratorApiImpl(final ConfigResolver resolver) {
    this.resolver = Objects.requireNonNull(resolver, "resolver");
  }

  @Override
  public void registerWorldConfig(final String worldName, final PluginSettings config) {
    Objects.requireNonNull(worldName, "worldName");
    Objects.requireNonNull(config, "config");
    if (worldName.isBlank()) {
      throw new IllegalArgumentException("worldName must not be blank");
    }
    this.resolver.registerOverride(worldName, config);
  }

  @Override
  public boolean unregisterWorldConfig(final String worldName) {
    Objects.requireNonNull(worldName, "worldName");
    return this.resolver.removeOverride(worldName).isPresent();
  }

  @Override
  public Optional<PluginSettings> findWorldConfig(final String worldName) {
    Objects.requireNonNull(worldName, "worldName");
    return this.resolver.findOverride(worldName);
  }

  @Override
  public PluginSettings resolveConfig(final String worldName) {
    Objects.requireNonNull(worldName, "worldName");
    return this.resolver.resolve(worldName);
  }

  @Override
  public Set<String> getOverriddenWorlds() {
    return this.resolver.overriddenWorlds();
  }
}