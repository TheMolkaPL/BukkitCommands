package pl.themolka.commons.command;

public class CommandConsoleException extends CommandException {
    private boolean console;

    public CommandConsoleException() {
        this(false);
    }

    public CommandConsoleException(boolean console) {
        this.console = console;
    }

    public boolean isConsoleLevel() {
        return this.console;
    }
}
