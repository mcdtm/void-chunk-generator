package me.kvdpxne.vcg.internal;

import java.util.Optional;
import java.util.Set;

import me.kvdpxne.vcg.api.VoidChunkGenApi;
import me.kvdpxne.vcg.api.VoidWorldConfig;
import org.bukkit.World;

public final class VoidChunkGenApiImpl implements VoidChunkGenApi {

  private final ConfigResolver resolver;
  private final WorldSettingsApplier applier;

  public VoidChunkGenApiImpl(final ConfigResolver resolver, final WorldSettingsApplier applier) {
    this.resolver = resolver;
    this.applier = applier;
  }

  @Override
  public void registerWorldConfig(final String worldName, final VoidWorldConfig config) {
    if (worldName == null || worldName.isBlank()) {
      throw new IllegalArgumentException("worldName cannot be null/blank");
    }
    if (config == null) {
      throw new IllegalArgumentException("config cannot be null");
    }
    this.resolver.registerOverride(worldName, config);
  }

  @Override
  public boolean unregisterWorldConfig(final String worldName) {
    return worldName != null && this.resolver.removeOverride(worldName).isPresent();
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

  @Override
  public void applyWorldSettings(final World world) {
    this.applier.apply(world, this.resolver.resolve(world.getName()));
  }
}