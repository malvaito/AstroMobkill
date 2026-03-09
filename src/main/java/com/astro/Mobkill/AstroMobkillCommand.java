package com.astro.Mobkill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AstroMobkillCommand implements CommandExecutor, TabCompleter {

    private final AstroMobkill plugin;
    private static final MiniMessage MM = MiniMessage.miniMessage();

    public AstroMobkillCommand(AstroMobkill plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        if (!sender.hasPermission("astromobkill.admin")) {
            sender.sendMessage(MM.deserialize("<red>No tienes permiso para usar este comando."));
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.getConfigManager().reload();
            sender.sendMessage(MM.deserialize(
                    "<green><bold>[AstroMobkill]</bold></green> <white>Configuración recargada correctamente.</white>"));
            return true;
        }

        sender.sendMessage(MM.deserialize(
                "<gold><bold>[AstroMobkill]</bold></gold> <white>Uso: /<command> reload</white>"
                        .replace("<command>", label)));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String label,
                                      @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission("astromobkill.admin")) {
            return List.of("reload");
        }
        return List.of();
    }
}
