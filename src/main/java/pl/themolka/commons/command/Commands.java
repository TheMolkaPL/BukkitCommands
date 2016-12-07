package pl.themolka.commons.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.CommandSender;

public abstract class Commands {
    private final Map<String, Command> commandMap = new HashMap<>();

    public Command getCommand(String command) {
        return this.commandMap.get(command.toLowerCase());
    }

    public Set<String> getCommandNames() {
        return this.commandMap.keySet();
    }

    public List<Command> getCommands() {
        List<Command> commands = new ArrayList<>();

        for (Command command : this.commandMap.values()) {
            if (!commands.contains(command)) {
                commands.add(command);
            }
        }

        return commands;
    }

    public abstract void handleCommand(CommandSender sender, CommandContext context);

    public void handleCommand(CommandSender sender, Command command, String label, String[] args) {
        this.handleCommand(sender, command, label, args, new CommandContextParser());
    }

    public void handleCommand(CommandSender sender, Command command, String label, String[] args, CommandContext.IContextParser parser) {
        this.handleCommand(sender, parser.parse(command, label, args));
    }

    public abstract List<String> handleCompleter(CommandSender sender, CommandContext context);

    public List<String> handleCompleter(CommandSender sender, Command command, String label, String[] args) {
        return this.handleCompleter(sender, command, label, args, new CommandContextParser());
    }

    public List<String> handleCompleter(CommandSender sender, Command command, String label, String[] args, CommandContext.IContextParser parser) {
        return this.handleCompleter(sender, parser.parse(command, label, args));
    }

    public void registerCommand(Command command) {
        for (String name : command.getName()) {
            this.commandMap.put(name, command);
        }
    }

    public void registerCommandClass(Class clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            method.setAccessible(true);

            Annotation annotation = method.getDeclaredAnnotation(CommandInfo.class);
            if (annotation != null) {
                this.registerCommandMethod(method, null, (CommandInfo) annotation);
            }
        }
    }

    public void registerCommandClasses(Class... classes) {
        for (Class clazz : classes) {
            this.registerCommandClass(clazz);
        }
    }

    public void registerCommandObject(Object object) {
        for (Method method : object.getClass().getDeclaredMethods()) {
            method.setAccessible(true);

            Annotation annotation = method.getDeclaredAnnotation(CommandInfo.class);
            if (annotation != null) {
                this.registerCommandMethod(method, object, (CommandInfo) annotation);
            }
        }
    }

    public void registerCommandObjects(Object... objects) {
        for (Object object : objects) {
            this.registerCommandObject(object);
        }
    }

    public void registerCommandMethod(Method method, Object object, CommandInfo info) {
        Method completer = null;
        if (!info.completer().isEmpty()) {
            try {
                completer = object.getClass().getDeclaredMethod(info.completer(), CommandSender.class, CommandContext.class);
                completer.setAccessible(true);
            } catch (NoSuchMethodException ignored) {
            }
        }

        this.registerCommand(new Command(
                info.name(),
                info.description(),
                info.min(),
                info.usage(),
                info.userOnly(),
                info.flags(), info.permission(),
                method,
                object,
                completer
        ));
    }
}
