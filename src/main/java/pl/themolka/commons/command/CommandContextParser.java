package pl.themolka.commons.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandContextParser implements CommandContext.IContextParser {
    public static final char FLAG_PREFIX = '-';
    public static final char FLAG_QUOTE = '\"';

    private Map<String, String> flags = new HashMap<>();
    private List<String> params = new ArrayList<>();

    @Override
    public CommandContext parse(Command command, String label, String[] args) {
        String flag = null;
        StringBuilder flagValue = null;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (flag != null && flagValue == null && arg.startsWith(String.valueOf(FLAG_QUOTE))) {
                if (arg.endsWith(String.valueOf(FLAG_QUOTE))) {
                    this.flags.put(flag, arg.substring(1, arg.length() - 1));
                    flag = null;
                    flagValue = null;
                    continue;
                }

                flagValue = new StringBuilder().append(arg.substring(1));
                continue;
            }

            if (flagValue != null) {
                flagValue.append(" ");

                if (arg.endsWith(String.valueOf(FLAG_QUOTE))) {
                    flagValue.append(arg.substring(0, arg.length() - 1));

                    this.flags.put(flag, flagValue.toString());
                    flag = null;
                    flagValue = null;
                    continue;
                }

                flagValue.append(arg);
                continue;
            }

            if (arg.startsWith(String.valueOf(FLAG_PREFIX))) {
                flag = arg.substring(1);

                if (command.hasFlag(flag)) {
                    this.flags.put(flag, null);
                    continue;
                }
            }

            flag = null;
            this.params.add(arg);
        }

        return new CommandContext(args, command, this.flags, label, this.params);
    }

    public Map<String, String> getFlags() {
        return this.flags;
    }

    public List<String> getParams() {
        return this.params;
    }
}