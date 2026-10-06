package me.kvdpxne.vcg.internal;

import java.util.logging.Logger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;

/**
 * Cleans up per-world overrides on unload.
 * Guards against consumers who forget to call
 * {@code unregisterWorldConfig} - the registry would otherwise grow unbounded.
 */
public final class WorldLifecycleListener implements Listener {

  private final ConfigResolver resolver;
  private final Logger logger;

  public WorldLifecycleListener(final ConfigResolver resolver, final Logger logger) {
    if (null == resolver) {
      throw new NullPointerException("resolver must not be null");
    }
    if (null == logger) {
      throw new NullPointerException("logger must not be null");
    }
    this.resolver = resolver;
    this.logger = logger;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onWorldUnload(final WorldUnloadEvent event) {
    if (null == event || null == event.getWorld()) {
      return;
    }
    final var worldName = event.getWorld().getName();
    this.resolver.removeOverride(worldName)
      .ifPresent(_ -> this.logger.info(
        "Cleaned up override for unloaded world: " + worldName));
  }
}