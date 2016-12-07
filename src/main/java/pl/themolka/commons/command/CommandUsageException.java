package pl.themolka.commons.command;

public class CommandUsageException extends CommandException {
    public CommandUsageException() {
        super();
    }

    public CommandUsageException(String message) {
        super(message);
    }
}
