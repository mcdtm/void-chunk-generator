package me.kvd.vcg.api.events;

import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired after the plugin computes the spawn location for a void world.
 * <p>
 * Listeners may mutate the spawn location before it is returned to the server.
 */
public final class VoidWorldSpawnLocationComputedEvent extends Event {

  private static final HandlerList HANDLERS = new HandlerList();

  private final String worldName;
  private Location spawnLocation;

  /**
   * Creates a new event instance.
   *
   * @param worldName     the name of the world, must not be null
   * @param spawnLocation the computed spawn location, must not be null
   * @throws NullPointerException if any argument is null
   */
  public VoidWorldSpawnLocationComputedEvent(final String worldName, final Location spawnLocation) {
    this.worldName = Objects.requireNonNull(worldName, "worldName");
    this.spawnLocation = Objects.requireNonNull(spawnLocation, "spawnLocation");
  }

  /**
   * Returns the name of the world.
   *
   * @return the world name, never null
   */
  public String getWorldName() {
    return this.worldName;
  }

  /**
   * Returns the computed spawn location.
   *
   * @return the spawn location, never null
   */
  public Location getSpawnLocation() {
    return this.spawnLocation;
  }

  /**
   * Overrides the spawn location.
   *
   * @param spawnLocation the new spawn location, must not be null
   * @throws NullPointerException if {@code spawnLocation} is null
   */
  public void setSpawnLocation(final Location spawnLocation) {
    this.spawnLocation = Objects.requireNonNull(spawnLocation, "spawnLocation");
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  /**
   * Returns the static handler list for this event type.
   *
   * @return the handler list, never null
   */
  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}