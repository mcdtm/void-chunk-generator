package me.kvdpxne.vcg.internal.listeners;

import me.kvdpxne.vcg.api.MobCategory;
import me.kvdpxne.vcg.api.VoidWorldConfig;
import me.kvdpxne.vcg.internal.ConfigResolver;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public final class SpawnListener implements Listener {

    private final ConfigResolver resolver;

    public SpawnListener(final ConfigResolver resolver) {
        this.resolver = resolver;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreatureSpawn(final CreatureSpawnEvent event) {
        final var config = this.resolver.resolve(event.getLocation().getWorld().getName());
        if (!this.isAllowed(event.getEntity(), config)) {
            event.setCancelled(true);
        }
    }

    private boolean isAllowed(final Entity entity, final VoidWorldConfig config) {
        return switch (MobCategory.classify(entity)) {
            case PASSIVE -> config.allowPassiveMobs();
            case HOSTILE -> config.allowHostileMobs();
            case NEUTRAL -> config.allowNeutralMobs();
        };
    }
}