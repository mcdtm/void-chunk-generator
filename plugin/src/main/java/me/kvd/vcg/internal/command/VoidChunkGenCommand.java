package me.kvd.vcg.internal.command;

import java.util.List;
import java.util.Objects;
import me.kvd.vcg.VoidChunkGenPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

/**
 * Handles the {@code /voidchunkgen} command and its tab completion.
 */
public final class VoidChunkGenCommand implements CommandExecutor, TabCompleter {

  private static final List<String> SUBCOMMANDS =
    List.of("reload", "info", "version");

  private final VoidChunkGenPlugin plugin;

  /**
   * Creates a new command handler.
   *
   * @param plugin the owning plugin, must not be null
   * @throws NullPointerException if {@code plugin} is null
   */
  public VoidChunkGenCommand(final VoidChunkGenPlugin plugin) {
    this.plugin = Objects.requireNonNull(plugin, "plugin");
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command,
                           final String label, final String[] args) {
    if (!sender.hasPermission("voidchunkgen.admin")) {
      return false;
    }
    if (0 == args.length) {
      sender.sendMessage(ChatColor.GOLD
        + "Usage: /" + label + " <reload|info|version>");
      return true;
    }
    switch (args[0].toLowerCase()) {
      case "reload" -> sender.sendMessage(ChatColor.GREEN
        + "VoidChunkGen has been reloaded successfully.");
      case "info" -> {
        sender.sendMessage(ChatColor.GOLD + "--- VoidChunkGen Info ---");
        sender.sendMessage(ChatColor.YELLOW + "Server version: "
          + ChatColor.WHITE + Bukkit.getBukkitVersion());
        sender.sendMessage(ChatColor.YELLOW + "Plugin version: "
          + ChatColor.WHITE
          + this.plugin.getDescription().getVersion());
      }
      case "version" -> sender.sendMessage(ChatColor.GOLD + "VoidChunkGen "
        + ChatColor.WHITE
        + this.plugin.getDescription().getVersion());
      default -> sender.sendMessage(ChatColor.RED + "Unknown command. Use: /"
        + label + " <reload|info|version>");
    }
    return true;
  }

  @Override
  public List<String> onTabComplete(final CommandSender sender, final Command command,
                                    final String alias, final String[] args) {
    if (1 == args.length && sender.hasPermission("voidchunkgen.admin")) {
      final var prefix = args[0].toLowerCase();
      return SUBCOMMANDS.stream()
        .filter(option -> option.startsWith(prefix))
        .toList();
    }
    return List.of();
  }
}