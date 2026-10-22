package me.kvd.vcg.internal.utils.metrics;

import java.util.Objects;
import java.util.logging.Logger;
import me.kvd.vcg.api.settings.PluginSettings;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.java.JavaPlugin;

public final class MetricsBootstrap {

  /**
   * Sentinel used until a real id is assigned at <a href="https://bstats.org/">bStats</a>.
   * Never publish with this value; the plugin logs a warning and skips
   * metrics registration entirely.
   */
  public static final int UNCONFIGURED_PLUGIN_ID = -1;

  public static final int PLUGIN_ID = UNCONFIGURED_PLUGIN_ID;

  private final JavaPlugin plugin;
  private final PluginSettings settings;
  private final int pluginId;
  private final Logger logger;

  public MetricsBootstrap(
    final JavaPlugin plugin,
    final PluginSettings settings,
    final int pluginId
  ) {
    this.plugin = Objects.requireNonNull(plugin, "plugin");
    this.settings = Objects.requireNonNull(settings, "settings");
    this.pluginId = pluginId;
    this.logger = this.plugin.getLogger();
  }

  public MetricsBootstrap(final JavaPlugin plugin, final PluginSettings settings) {
    this(plugin, settings, PLUGIN_ID);
  }

  public void start() {
    if (!this.settings.metricsEnabled()) {
      this.logger.config(() -> "bStats metrics are disabled in the configuration.");
      return;
    }
    if (UNCONFIGURED_PLUGIN_ID >= this.pluginId) {
      this.logger.warning("bStats plugin id is not configured; metrics will not be sent. "
        + "Set a real id in MetricsBootstrap before publishing.");
      return;
    }
    final var metrics = new Metrics(this.plugin, this.pluginId);
    if (this.settings.metricsExtendedEnabled()) {
      this.registerCharts(metrics);
    }
    this.logger.fine(() -> "bStats metrics enabled (id " + this.pluginId
      + "). Set bstats-enabled=false to opt out.");
  }

  private void registerCharts(final Metrics metrics) {
    this.addChart(metrics, "biome", this.settings.biomeName());
    this.addChart(metrics, "platform_enabled", this.settings.platformEnabled());
    this.addChart(metrics, "spawn_enabled", this.settings.fixedSpawnEnabled());
  }

  private void addChart(final Metrics metrics,
                        final String idName,
                        final Object result
  ) {
    metrics.addCustomChart(
      new SimplePie(idName, () -> String.valueOf(result)));
  }
}
