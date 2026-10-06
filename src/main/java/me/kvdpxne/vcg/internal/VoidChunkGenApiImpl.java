package me.kvdpxne.vcg.internal;

import me.kvdpxne.vcg.api.VoidChunkGenApi;
import me.kvdpxne.vcg.api.VoidWorldConfig;
import me.kvdpxne.vcg.internal.ConfigResolver;

import java.util.Optional;
import java.util.Set;

/**
 * Internal implementation of {@link VoidChunkGenApi}.
 * Exposed only through the {@code ServicesManager} - consumers
 * never see this class.
 */
public final class VoidChunkGenApiImpl implements VoidChunkGenApi {

  private final ConfigResolver resolver;

  public VoidChunkGenApiImpl(final ConfigResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public void registerWorldConfig(final String worldName, final VoidWorldConfig config) {
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
  public Optional<VoidWorldConfig> findWorldConfig(final String worldName) {
    return this.resolver.findOverride(worldName);
  }

  @Override
  public VoidWorldConfig resolveConfig(final String worldName) {
    return this.resolver.resolve(worldName);
  }

  @Override
  public Set<String> getOverriddenWorlds() {
    return this.resolver.overriddenWorlds();
  }
}