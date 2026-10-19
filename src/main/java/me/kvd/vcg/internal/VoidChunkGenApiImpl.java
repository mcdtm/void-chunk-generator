package me.kvd.vcg.internal;

import me.kvd.vcg.api.VoidChunkGenApi;
import me.kvd.vcg.internal.settings.PluginSettings;

import java.util.Optional;
import java.util.Set;
import me.kvd.vcg.internal.util.ConfigResolver;

/**
 * Internal implementation of {@link VoidChunkGenApi}.
 * Exposed only through the {@code ServicesManager} - consumers
 * never see this class.
 */
public final class VoidChunkGenApiImpl implements VoidChunkGenApi {

  private final me.kvd.vcg.internal.util.ConfigResolver resolver;

  public VoidChunkGenApiImpl(final ConfigResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public void registerWorldConfig(final String worldName, final PluginSettings config) {
    this.resolver.registerOverride(worldName, config);
  }

  @Override
  public boolean unregisterWorldConfig(final String worldName) {
    if (null == worldName) {
      return false;
    }
    return this.resolver.removeOverride(worldName).isPresent();
  }

  @Override
  public Optional<PluginSettings> findWorldConfig(final String worldName) {
    return this.resolver.findOverride(worldName);
  }

  @Override
  public PluginSettings resolveConfig(final String worldName) {
    return this.resolver.resolve(worldName);
  }

  @Override
  public Set<String> getOverriddenWorlds() {
    return this.resolver.overriddenWorlds();
  }
}