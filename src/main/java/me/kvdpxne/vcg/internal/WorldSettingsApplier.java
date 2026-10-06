package me.kvdpxne.vcg.internal;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.kvdpxne.vcg.api.VoidWorldConfig;
import org.bukkit.World;

/**
 * Odpowiedzialnosc: zastosowanie pogody, cyklu dnia i flag spawnu na swiecie.
 * Nie zarzadza swiatem - tylko ustawia parametry zwiazane z generowanymi chunkami.
 */
public final class WorldSettingsApplier {

    private static final String GAMERULE_DAYLIGHT = "doDaylightCycle";
    private static final int WEATHER_FOREVER = Integer.MAX_VALUE;
    private static final int WEATHER_DEFAULT = 6000;

    private final Logger logger;

    public WorldSettingsApplier(final Logger logger) {
        this.logger = logger;
    }

    public void apply(final World world, final VoidWorldConfig config) {
        try {
            this.applyWeather(world, config);
            this.applyDayNightCycle(world, config);
            this.applySpawnFlags(world, config);
            this.logger.info("Zastosowano ustawienia swiata: " + world.getName());
        } catch (final Exception ex) {
            this.logger.log(Level.SEVERE,
                    "Blad stosowania ustawien w " + world.getName(), ex);
        }
    }

    private void applyWeather(final World world, final VoidWorldConfig config) {
        if (config.allowPrecipitation()) {
            world.setWeatherDuration(WEATHER_DEFAULT);
            return;
        }
        world.setStorm(false);
        world.setThundering(false);
        world.setWeatherDuration(WEATHER_FOREVER);
    }

    private void applyDayNightCycle(final World world, final VoidWorldConfig config) {
        world.setGameRuleValue(GAMERULE_DAYLIGHT, Boolean.toString(config.enableDayNightCycle()));
    }

    private void applySpawnFlags(final World world, final VoidWorldConfig config) {
        world.setSpawnFlags(config.allowHostileMobs(), config.allowPassiveMobs());
    }
}