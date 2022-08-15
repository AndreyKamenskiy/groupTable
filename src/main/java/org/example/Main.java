package org.example;

import org.example.table.Table;

import java.io.IOException;
import java.util.List;

public class Main {

    //todo: add tests for null arguments passed to every method;

    private static final String HELP_COMMAND = "-help";
    private static final String INPUT_FILE_COMMAND = "-inputFile";
    private static final String OUTPUT_FILE_COMMAND = "-outputFile";
    private static final String DEFAULT_OUTPUT_FILE_NAME = "groupingTable.xls";
    private static final int UNKNOWN_FORMAT_EXIT_STATUS = 1;
    private static final int LOAD_ERROR_EXIT_STATUS = 2;
    private static final int SAVE_ERROR_EXIT_STATUS = 3;
    private static final int OK_EXIT_STATUS = 0;

    private static String inputFileName;
    private static String outputFileName = DEFAULT_OUTPUT_FILE_NAME;

    public static void main(String[] args) {
        processArgs(args);

        TableLoader loader = createTableLoader(inputFileName);

        Table table = null;
        try {
            table = loader.loadTable(inputFileName);
        } catch (IOException e) {
            System.out.printf("Table load error: %s%n", e.getMessage());
            System.exit(LOAD_ERROR_EXIT_STATUS);
        }

        for (int row = 0; row < table.getHeight(); ++row) {
            for (int col = 0; col < table.getWidth(); col++) {
                System.out.printf("%s\t", table.getCell(row, col));
            }
            System.out.print('\n');
        }

        TableSaver saver = createTableSaver(outputFileName);
        try {
            saver.saveTable(outputFileName, table);
        } catch (IOException e) {
            System.out.printf("Table save error: %s%n", e.getMessage());
            System.exit(SAVE_ERROR_EXIT_STATUS);
        }
    }

    private static void processArgs(String[] args) {
        ArgumentsProcessor argsProcessor = new ArgumentsProcessor(args);
        argsProcessor.setAvailableCommands(List.of(HELP_COMMAND, OUTPUT_FILE_COMMAND, INPUT_FILE_COMMAND));
        argsProcessor.parseArguments();
        if (argsProcessor.hasCommand(HELP_COMMAND)) {
            showHelp();
            System.exit(OK_EXIT_STATUS);
        }
        if (!argsProcessor.hasCommandWithValue(INPUT_FILE_COMMAND)) {
            System.out.printf("Input file name is absent. Use: %s <filename>%n", INPUT_FILE_COMMAND);
            System.exit(LOAD_ERROR_EXIT_STATUS);
        }
        inputFileName = argsProcessor.getValue(INPUT_FILE_COMMAND);
        if (argsProcessor.hasCommandWithValue(INPUT_FILE_COMMAND)) {
            outputFileName = argsProcessor.getValue(OUTPUT_FILE_COMMAND);
        }
    }

    private static TableLoader createTableLoader(String fileName) {
        if (fileName.matches(".+[.]xlsx?")) {
            return new ExcelTableLoader();
        }
        System.out.printf("Cannot read file %s. Unknown format", fileName);
        System.exit(UNKNOWN_FORMAT_EXIT_STATUS);
        return null;
    }

    private static TableSaver createTableSaver(String fileName) {
        if (fileName.matches(".+[.]xlsx?")) {
            return new ExcelTableSaver();
        }
        System.out.printf("Cannot write file %s. Unknown format", fileName);
        System.exit(UNKNOWN_FORMAT_EXIT_STATUS);
        return null;
    }

    private static void showHelp() {
        //todo: add description
        String helpMessage = """
                description coming soon...
                Available commands:
                    %1$s : help.
                    %2$s : command sets input file name.
                           No default value. Required command.
                    %3$s : command sets output file name and format.
                           Default value is "%4$s"
                Usage example:
                java -jar groupTable.jar %2$s table.xlsx %3$s resultTable.xls
                """;
        System.out.printf(helpMessage,
                HELP_COMMAND,
                INPUT_FILE_COMMAND,
                OUTPUT_FILE_COMMAND,
                DEFAULT_OUTPUT_FILE_NAME
        );

    }

}