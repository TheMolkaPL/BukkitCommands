package pl.themolka.commons.command;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicComparator;
import org.bukkit.help.IndexHelpTopic;
import org.bukkit.plugin.Plugin;

public class BukkitCommands extends Commands implements CommandExecutor, TabCompleter {
    private final Plugin plugin;

    private CommandMap bukkitCommandMap;
    private Set<HelpTopic> helpTopics = new TreeSet<>(HelpTopicComparator.helpTopicComparatorInstance());

    public BukkitCommands(Plugin plugin) {
        this.plugin = plugin;

        try {
            this.bukkitCommandMap = this.getBukkitCommandMap();
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }

        this.plugin.getServer().getHelpMap().addTopic(this.createHelpIndex());
    }

    @Override
    public void handleCommand(CommandSender sender, CommandContext context) {
        try {
            if (context.getCommand().isUserOnly() && !(sender instanceof Player)) {
                throw new CommandConsoleException();
            } else if (context.getCommand().hasPermission() && !sender.hasPermission(context.getCommand().getPermission())) {
                throw new CommandPermissionException(context.getCommand().getPermission());
            } else if (context.getCommand().getMin() > context.getParamsLength()) {
                throw new CommandUsageException("Zbyt malo argumentow.");
            } else {
                context.getCommand().handleCommand(sender, context);
            }
        } catch (CommandConsoleException ex) {
            String level = "gry";
            if (ex.isConsoleLevel()) {
                level = "konsoli";
            }

            sender.sendMessage(ChatColor.RED + "Ta komenda moze zostac wykonana tylko z poziomu " + level + ".");
        } catch (CommandPermissionException ex) {
            String permission = ".";
            if (ex.getPermission() != null) {
                permission = " - " + ex.getPermission();
            }

            sender.sendMessage(ChatColor.RED + "Nie posiadasz odpowiednich uprawnien" + permission);
        } catch (CommandUsageException ex) {
            if (ex.getMessage() != null) {
                sender.sendMessage(ChatColor.RED + ex.getMessage());
            }

            sender.sendMessage(context.getCommand().getUsage());
        } catch (CommandException ex) {
            if (ex.getMessage() != null) {
                sender.sendMessage(ex.getMessage());
            } else {
                sender.sendMessage(ChatColor.RED + "Nie udalo sie wykonac komendy, poniewaz popelniono jakis blad.");
            }
        } catch (NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + "Musisz podac liczbe, nie ciag znakow!");
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "Wykryto niespodziewany blad - powiadom o tym administracje!");
            sender.sendMessage(ChatColor.RED + ex.getLocalizedMessage());
        }
    }

    @Override
    public List<String> handleCompleter(CommandSender sender, CommandContext context) {
        try {
            if (context.getCommand().isUserOnly() && !(sender instanceof Player)) {
                throw new CommandConsoleException();
            } else if (context.getCommand().hasPermission() && !sender.hasPermission(context.getCommand().getPermission())) {
                throw new CommandPermissionException(context.getCommand().getPermission());
            } else {
                return context.getCommand().handleCompleter(sender, context);
            }
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void registerCommand(Command command) {
        super.registerCommand(command);

        this.injectCommand(this.getPrefixName(), command);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        this.handleCommand(sender, this.getCommand(label), label, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        return this.handleCompleter(sender, this.getCommand(label), label, args);
    }

    public Set<HelpTopic> getHelpTopics() {
        return this.helpTopics;
    }

    private org.bukkit.command.Command createBukkitCommand(Command command) {
        List<String> aliases = new ArrayList<>();
        for (int i = 1; i < command.getName().length; i++) {
            aliases.add(command.getName()[i]);
        }

        org.bukkit.command.Command performer = new CommandPerformer(command.getCommand());
        performer.setAliases(aliases);
        performer.setDescription(command.getDescription());
        performer.setUsage(command.getUsage());
        return performer;
    }

    private IndexHelpTopic createHelpIndex() {
        return new IndexHelpTopic(
                this.getPluginName(),
                "Wszystkie komendy " + this.getPluginName(),
                null,
                this.getHelpTopics()
        );
    }

    private CommandMap getBukkitCommandMap() throws ReflectiveOperationException {
        Field field = this.plugin.getServer().getClass().getDeclaredField("commandMap");
        field.setAccessible(true);
        return (CommandMap) field.get(this.plugin.getServer());
    }

    private String getPluginName() {
        return this.plugin.getName();
    }

    private String getPrefixName() {
        return this.getPluginName().toLowerCase();
    }

    private void injectCommand(String prefix, Command command) {
        org.bukkit.command.Command performer = this.createBukkitCommand(command);

        this.bukkitCommandMap.register(prefix, performer);
        this.helpTopics.add(new GenericCommandHelpTopic(performer));
    }

    private class CommandPerformer extends org.bukkit.command.Command {
        protected CommandPerformer(String name) {
            super(name);
        }

        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            return BukkitCommands.this.onCommand(sender, this, label, args);
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String label, String[] args) throws IllegalArgumentException {
            return BukkitCommands.this.onTabComplete(sender, this, label, args);
        }
    }
}
