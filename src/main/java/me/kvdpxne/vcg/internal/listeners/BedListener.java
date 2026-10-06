package me.kvdpxne.vcg.internal.listeners;

import me.kvdpxne.vcg.internal.ConfigResolver;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public final class BedListener implements Listener {

    private final ConfigResolver resolver;

    public BedListener(final ConfigResolver resolver) {
        this.resolver = resolver;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBedEnter(final PlayerBedEnterEvent event) {
        final var config = this.resolver.resolve(event.getPlayer().getWorld().getName());
        if (!config.enableDayNightCycle()) {
            event.setCancelled(true);
        }
    }
}