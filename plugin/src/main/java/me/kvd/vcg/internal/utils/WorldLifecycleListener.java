package me.kvd.vcg.internal.utils;

import java.util.Objects;
import java.util.logging.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;

/**
 * Clears per-world overrides when a world is unloaded.
 */
public final class WorldLifecycleListener implements Listener {

  private final ConfigResolver resolver;
  private final Logger logger;

  /**
   * Creates a new listener.
   *
   * @param resolver the configuration resolver, must not be null
   * @param logger   the logger to use, must not be null
   * @throws NullPointerException if any argument is null
   */
  public WorldLifecycleListener(final ConfigResolver resolver, final Logger logger) {
    this.resolver = Objects.requireNonNull(resolver, "resolver");
    this.logger = Objects.requireNonNull(logger, "logger");
  }

  /**
   * Clears the override when the world is unloaded.
   *
   * @param event the unload event, must not be null
   */
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onWorldUnload(final WorldUnloadEvent event) {
    final var worldName = event.getWorld().getName();
    if (this.resolver.removeOverride(worldName).isPresent()) {
      this.logger.fine(() -> "Removed override for unloaded world "
        + worldName);
    }
  }
}