package org.example;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;


import static org.junit.jupiter.api.Assertions.*;

class ArgumentsProcessorTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/argumentParseTest.csv", numLinesToSkip = 1)
    void argumentsParseTest(String args, String command, String value) {
        ArgumentsProcessor processor = new ArgumentsProcessor(args.split("\\s+"));
        processor.parseArguments();
        assert(processor.hasCommand(command));
        assert(processor.hasCommandWithValue(command));
        assert(processor.getValue(command).equals(value));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/argumentParseErrorTest.csv", numLinesToSkip = 1)
    void argumentsParseErrorTest(String args, String error) {
        ArgumentsProcessor processor = new ArgumentsProcessor(args.split("\\s+"));
        IllegalArgumentException ex = assertThrowsExactly(IllegalArgumentException.class, processor::parseArguments);
        assertEquals(String.format(error), ex.getMessage());
    }





}