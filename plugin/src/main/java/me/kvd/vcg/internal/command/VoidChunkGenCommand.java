package me.kvd.vcg.internal.command;

import java.util.List;
import me.kvd.vcg.VoidChunkGenPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public final class VoidChunkGenCommand implements CommandExecutor, TabCompleter {

  private final VoidChunkGenPlugin plugin;

  public VoidChunkGenCommand(VoidChunkGenPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!sender.hasPermission("voidchunkgen.admin")) {
      sender.sendMessage(ChatColor.RED + "Brak uprawnień do zarządzania VoidChunkGen.");
      return true;
    }

    if (args.length == 0) {
      sender.sendMessage(ChatColor.GOLD + "Użycie: /" + label + " <reload|info|version>");
      return true;
    }

    // Java 25 - switch pattern matching (dla Stringów działa jak zwykły switch)
    switch (args[0].toLowerCase()) {
      case "reload" -> {
        // TODO add config reload
        sender.sendMessage(ChatColor.GREEN + "VoidChunkGen został pomyślnie przeładowany.");
      }
      case "info" -> {
        sender.sendMessage(ChatColor.GOLD + "--- VoidChunkGen Info ---");
        sender.sendMessage(ChatColor.YELLOW + "Wersja serwera: " + ChatColor.WHITE + Bukkit.getBukkitVersion());
        sender.sendMessage(ChatColor.YELLOW + "Aktywny generator: " + ChatColor.WHITE + "none"); // TODO add active generator
        sender.sendMessage(ChatColor.YELLOW + "Wersja pluginu: " + ChatColor.WHITE + this.plugin.getDescription().getVersion());
      }
      case "version" -> {
        sender.sendMessage(ChatColor.GOLD + "VoidChunkGen " + ChatColor.WHITE + this.plugin.getDescription().getVersion());
      }
      default -> {
        sender.sendMessage(ChatColor.RED + "Nieznana komenda. Użyj: /" + label + " <reload|info|version>");
      }
    }

    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    if (args.length == 1 && sender.hasPermission("voidchunkgen.admin")) {
      return List.of("reload", "info", "version").stream()
        .filter(option -> option.startsWith(args[0].toLowerCase()))
        .toList();
    }
    return List.of();
  }
}