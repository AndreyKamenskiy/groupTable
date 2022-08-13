package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class ArgumentsProcessor {
    private static final String UNKNOWN_ARGUMENT = "Unknown argument: %s.%nUse %shelp key for uses instructions.";
    private static final String UNKNOWN_COMMAND = "Unknown command: %s.%nUse %shelp key for uses instructions.";
    private static final String DOUBLED_COMMAND = "Doubled command: %s.";
    private static final String EMPTY_COMMAND = "Empty command found.";
    private static final String EMPTY_COMMAND_LIST = "ArgumentsProcessor error: Empty all commands list.";
    private static final String COMMAND_ABSENT = "ArgumentsProcessor error: command %s is absent.";
    private static final String COMMAND_VALUE_ABSENT = "ArgumentsProcessor error: %s command's value is absent.";
    private static final String ILLEGAL_PREFIX = "ArgumentsProcessor error: '%s' is illegal command prefix.";

    private String commandPrefix = "-";

    private final String[] args;

    private final Map<String, String> keys;

    private boolean hasCommandList = false;
    // Список всех доступных ключей. Хранится в нижнем регистре для регистронезависимого поиска
    private Set<String> availableCommands = null;

    public ArgumentsProcessor(String[] args) {
        this.args = args;
        keys = new HashMap<>();
    }

    public void setCommandPrefix(String commandPrefix) {
        if (commandPrefix == null || commandPrefix.isBlank()) {
            throw new IllegalArgumentException(String.format(ILLEGAL_PREFIX, commandPrefix));
        }
        this.commandPrefix = commandPrefix;
    }


    public void parseArguments() throws IllegalArgumentException {
        for (int i = 0; i < args.length; ++i) {
            if (args[i].startsWith(commandPrefix)) {
                String command = args[i].toLowerCase();
                if (commandPrefix.equals(command)) {
                    throw new IllegalArgumentException(EMPTY_COMMAND);
                }
                if (keys.containsKey(command)) {
                    throw new IllegalArgumentException(String.format(DOUBLED_COMMAND, args[i]));
                }
                if (hasCommandList && !availableCommands.contains(command)) {
                    throw new IllegalArgumentException(String.format(UNKNOWN_COMMAND, args[i], commandPrefix));
                }
                String value = null;
                if (i + 1 < args.length && !args[i + 1].startsWith(commandPrefix)) {
                    value = args[++i];
                }
                keys.put(command, value);
            } else {
                throw new IllegalArgumentException(String.format(UNKNOWN_ARGUMENT, args[i], commandPrefix));
            }
        }
    }

    public void setAvailableCommands(Collection<String> commands) throws IllegalArgumentException {
        if (commands == null || commands.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_COMMAND_LIST);
        }
        availableCommands = commands.stream().map(String::toLowerCase).collect(Collectors.toSet());
        hasCommandList = true;
    }

    public boolean hasCommandWithValue(String command) {
        String lowCaseCommand = command.toLowerCase();
        return keys.containsKey(lowCaseCommand) && keys.get(lowCaseCommand) != null;
    }

    public boolean hasCommand(String command) {
        return keys.containsKey(command.toLowerCase());
    }

    public String getValue(String command) throws IllegalArgumentException {
        if (!hasCommandWithValue(command)) {
            if (!hasCommand(command)) {
                throw new IllegalArgumentException(String.format(COMMAND_ABSENT, command));
            } else {
                throw new IllegalArgumentException(String.format(COMMAND_VALUE_ABSENT, command));
            }
        }
        return keys.get(command.toLowerCase());
    }

}
