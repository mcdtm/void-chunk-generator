package me.kvdpxne.vcg.internal.listeners;

import me.kvdpxne.vcg.internal.ConfigResolver;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public final class WeatherListener implements Listener {

    private final ConfigResolver resolver;

    public WeatherListener(final ConfigResolver resolver) {
        this.resolver = resolver;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onWeatherChange(final WeatherChangeEvent event) {
        final var config = this.resolver.resolve(event.getWorld().getName());
        if (!config.allowPrecipitation()) {
            event.setCancelled(true);
        }
    }
}