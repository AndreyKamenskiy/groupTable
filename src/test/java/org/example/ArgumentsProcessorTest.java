package org.example;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArgumentsProcessorTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/argumentParseTest.csv", numLinesToSkip = 1)
    void argumentsParseTest(String prefix, String args, String allCommands, String command, String value) {
        ArgumentsProcessor processor = new ArgumentsProcessor(args.split("\\s+"));
        if (hasArgument(prefix)) {
            processor.setCommandPrefix(prefix);
        }
        if (hasArgument(allCommands)) {
            processor.setAvailableCommands(List.of(allCommands.split("\\s+")));
        }
        processor.parseArguments();
        if (command != null) {
            assert processor.hasCommand(command);
            if (value != null && !value.isEmpty()) {
                assert processor.hasCommandWithValue(command);
                assert processor.getValue(command).equals(value);
            } else {
                assertFalse(processor.hasCommandWithValue(command));
            }
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/argumentParseErrorTest.csv", numLinesToSkip = 1)
    void argumentsParseErrorTest(String prefix, String args, String allCommands, String command, String value, String error) {
        ArgumentsProcessor processor = new ArgumentsProcessor(args.split("\\s+"));
        if (hasArgument(prefix)) {
            processor.setCommandPrefix(prefix);
        }
        if (hasArgument(allCommands)) {
            processor.setAvailableCommands(List.of(allCommands.split("\\s+")));
        }
        IllegalArgumentException ex;
        if (hasArgument(command) && hasArgument(value)) {
            // Если передан параметр command и value, то значит исключение должно возникнуть не раньше поиска команды
            processor.parseArguments();
            ex = assertThrowsExactly(IllegalArgumentException.class, () -> processor.getValue(command));
        } else {
            // ждем исключение на этапе парсинга аргументов
            ex = assertThrowsExactly(IllegalArgumentException.class, processor::parseArguments);
        }

        assertEquals(String.format(error), ex.getMessage());
    }

    private boolean hasArgument(String arg) {
        return arg != null && !arg.isEmpty();
    }

    @ParameterizedTest
    @CsvSource(value = {
            ",ArgumentsProcessor error: 'null' is illegal command prefix.",
            " ,ArgumentsProcessor error: ' ' is illegal command prefix."
    }, ignoreLeadingAndTrailingWhitespace = false)
    void illegalPrefixTest(String prefix, String error) {
        final String[] args = {"asdf"};
        ArgumentsProcessor processor = new ArgumentsProcessor(args);
        IllegalArgumentException ex = assertThrowsExactly(IllegalArgumentException.class, () -> processor.setCommandPrefix(prefix));
        assertEquals(String.format(error), ex.getMessage());
    }


}