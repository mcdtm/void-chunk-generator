package me.kvdpxne.vcg.api.event;

import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when the generator computes a fixed spawn location for a world.
 * Cancelling makes the generator return {@code null} (let Bukkit decide).
 * Listeners may also replace the location by calling
 * {@link #setSpawnLocation(Location)}.
 *
 * <p>Called rarely (world creation, respawn, spawn setup), never during
 * chunk generation.</p>
 */
public final class VoidWorldSpawnLocationComputedEvent extends Event implements Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();

  private final World world;
  private Location spawnLocation;
  private boolean cancelled;

  public VoidWorldSpawnLocationComputedEvent(final World world, final Location spawnLocation) {
    this.world = Objects.requireNonNull(world, "world");
    this.spawnLocation = Objects.requireNonNull(spawnLocation, "spawnLocation");
    this.cancelled = false;
  }

  public World getWorld() {
    return this.world;
  }

  public Location getSpawnLocation() {
    return this.spawnLocation;
  }

  /**
   * Replaces the spawn location. If the provided location has no world
   * attached, the event's world is assigned automatically.
   */
  public void setSpawnLocation(final Location spawnLocation) {
    Objects.requireNonNull(spawnLocation, "spawnLocation");
    if (null == spawnLocation.getWorld()) {
      spawnLocation.setWorld(this.world);
    }
    this.spawnLocation = spawnLocation;
  }

  @Override
  public boolean isCancelled() {
    return this.cancelled;
  }

  @Override
  public void setCancelled(final boolean cancel) {
    this.cancelled = cancel;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}