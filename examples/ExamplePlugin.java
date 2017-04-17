import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import pl.themolka.commons.command.BukkitCommands;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandException;
import pl.themolka.commons.command.CommandInfo;
import pl.themolka.commons.command.Commands;

public class ExamplePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        Commands commands = new BukkitCommands(this);
        commands.registerCommandObject(this);
    }

    @CommandInfo(name = {"broadcast", "bc"}, description = "Oglos wiadomosc na serwerze", min = 1,
            flags = {"ops"}, permission = "bukkitcommands.broadcast", usage = "[-ops] <message...>")
    public void broadcast(CommandSender sender, CommandContext context) {
        boolean ops = context.hasFlag("ops");
        String message = ChatColor.translateAlternateColorCodes('&', context.getParams(0));

        for (Player online : this.getServer().getOnlinePlayers()) {
            if (ops && !online.isOp()) {
                continue;
            }

            online.sendMessage(message);
        }
    }

    @CommandInfo(name = "plugininfo", description = "Informacje o pluginie",
            permission = "bukkitcommands.plugininfo", usage = "[plugin]", completer = "pluginInfoCompleter")
    public void pluginInfo(CommandSender sender, CommandContext context) {
        String name = context.getParam(0, this.getDescription().getName());

        Plugin plugin = this.getServer().getPluginManager().getPlugin(name);
        if (plugin == null) {
            throw new CommandException("Podany plugin nie zostal znaleziony");
        }

        sender.sendMessage("Plugin: " + plugin.getName());
        sender.sendMessage("wersja "  + plugin.getDescription().getVersion());
        sender.sendMessage("by " + StringUtils.join(plugin.getDescription().getAuthors(), ", "));
    }

    public List<String> pluginInfoCompleter(CommandSender sender, CommandContext context) {
        List<String> pluginNames = new ArrayList<>();
        for (Plugin plugin : this.getServer().getPluginManager().getPlugins()) {
            pluginNames.add(plugin.getName());
        }

        return pluginNames;
    }
}

