package me.kvdpxne.vcg.internal.listeners;

import java.util.logging.Logger;

import me.kvdpxne.vcg.internal.ConfigResolver;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;

/**
 * Odpowiedzialnosc: sprzatanie nadpisan konfiguracji przy unload swiata.
 *
 * Zabezpieczenie przed wyciekiem pamieci, gdy konsument API (DimensionManager)
 * zapomni wywolac {@link pl.voidchunkgenerator.api.VoidChunkGeneratorAPI#unregisterWorldConfig(String)}.
 *
 * Plugin dziala w trybie "defensive cleanup" - jesli swiat jest rozladowywany,
 * jego per-swiatowe nadpisanie nie ma juz zastosowania i zostaje usuniete.
 */
public final class WorldLifecycleListener implements Listener {

    private final ConfigResolver resolver;
    private final Logger logger;

    public WorldLifecycleListener(final ConfigResolver resolver, final Logger logger) {
        this.resolver = resolver;
        this.logger = logger;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldUnload(final WorldUnloadEvent event) {
        final var worldName = event.getWorld().getName();
        this.resolver.removeOverride(worldName).ifPresent(previous ->
                this.logger.info("Sprzatnieto nadpisanie dla rozladowanego swiata: " + worldName));
    }
}