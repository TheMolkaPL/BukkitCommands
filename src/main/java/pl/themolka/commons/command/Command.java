package pl.themolka.commons.command;

import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class Command {
    private final String[] name;
    private String description;
    private int min;
    private String usage;
    private boolean userOnly;
    private final String[] flags;
    private String permission;
    private final Method method;
    private final Object classObject;
    private final Method completer;

    public Command(String[] name, String description, int min, String usage, boolean userOnly, String[] flags, String permission, Method method, Object classObject, Method completer) {
        this.name = name;
        this.description = description;
        this.min = min;
        this.usage = usage;
        this.userOnly = userOnly;
        this.flags = flags;
        this.permission = permission;
        this.method = method;
        this.classObject = classObject;
        this.completer = completer;
    }

    public String getCommand() {
        return this.getName()[0];
    }

    public String[] getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public int getMin() {
        return this.min;
    }

    public String getUsage() {
        return ("/" + this.getCommand() + " " + this.usage).trim();
    }

    public String[] getFlags() {
        return this.flags;
    }

    public String getPermission() {
        return this.permission;
    }

    public Method getMethod() {
        return this.method;
    }

    public Object getClassObject() {
        return this.classObject;
    }

    public Method getCompleter() {
        return this.completer;
    }

    public void handleCommand(CommandSender sender, CommandContext context) throws Throwable {
        if (this.getMethod() == null) {
            return;
        }

        try {
            this.getMethod().setAccessible(true);
            this.getMethod().invoke(this.getClassObject(), sender, context);
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }

    public List<String> handleCompleter(CommandSender sender, CommandContext context) throws Throwable {
        if (this.getCompleter() == null) {
            return null;
        }

        try {
            this.getCompleter().setAccessible(true);
            Object result = this.getCompleter().invoke(this.getClassObject(), sender, context);

            if (result instanceof List) {
                return (List<String>) result;
            }
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }

        return null;
    }

    public boolean hasCompleter() {
        return this.completer != null;
    }

    public boolean hasFlag(String flag) {
        for (String f : this.flags) {
            if (f.equals(flag)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPermission() {
        return this.permission != null;
    }

    public boolean isUserOnly() {
        return this.userOnly;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public void setUserOnly(boolean userOnly) {
        this.userOnly = userOnly;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}