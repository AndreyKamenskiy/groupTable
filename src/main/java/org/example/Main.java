package org.example;

import org.example.table.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    //todo: add tests for null arguments passed to every method;


    private static final String HELP_COMMAND = "-help";
    private static final String PRINT_STAT_COMMAND = "-printStats";
    private static final String INPUT_FILE_COMMAND = "-inputFile";
    private static final String OUTPUT_FILE_COMMAND = "-outputFile";
    private static final String DEFAULT_OUTPUT_FILE_NAME = "groupingTable.xlsx";
    private static final int UNKNOWN_FORMAT_EXIT_STATUS = 1;
    private static final int LOAD_ERROR_EXIT_STATUS = 2;
    private static final int SAVE_ERROR_EXIT_STATUS = 3;
    private static final int OK_EXIT_STATUS = 0;

    private static String inputFileName;
    private static String outputFileName = DEFAULT_OUTPUT_FILE_NAME;

    private static Map<String, Long> timePoints;

    private record StatPoint(String name, String value) {

        @Override
        public String toString() {
            return String.format("%s %s", name, value);
        }
    }

    private static List<StatPoint> statPoints;

    private static boolean printTimeStats;

    public static void main(String[] args) {
        processArgs(args);
        addTimePoint("Begin");

        TableLoader loader = createTableLoader(inputFileName);

        Table inTable = null;
        try {
            inTable = loader.loadTable(inputFileName);
        } catch (IOException e) {
            System.out.printf("Table load error: %s%n", e.getMessage());
            System.exit(LOAD_ERROR_EXIT_STATUS);
        }
        if (printTimeStats) {
            addTimePoint("TableLoaded");
            addStatPoint("Table loaded in", getTime("Begin", "TableLoaded"));
            addStatPoint("input table width", String.valueOf(inTable.getWidth()));
            addStatPoint("input table heigth", String.valueOf(inTable.getHeight()));
        }

        TableGrouper grouper = new TrieTableGrouper();
        Table outTable = grouper.groupTable(inTable);
        if (printTimeStats) {
            addTimePoint("TableGrouped");
            addStatPoint("Table grouped in", getTime("TableLoaded", "TableGrouped"));
            addStatPoint("output table width", String.valueOf(outTable.getWidth()));
            addStatPoint("output table heigth", String.valueOf(outTable.getHeight()));
        }

        TableSaver saver = createTableSaver(outputFileName);
        try {
            saver.saveTable(outputFileName, outTable);
        } catch (IOException e) {
            System.out.printf("Table save error: %s%n", e.getMessage());
            System.exit(SAVE_ERROR_EXIT_STATUS);
        }
        if (printTimeStats) {
            addTimePoint("TheEnd");
            addStatPoint("Table saved in", getTime("TableGrouped", "TheEnd"));
            addStatPoint("Whole working time", getTime("Begin", "TheEnd"));
            printStatistics();
        }
    }

    private static void addTimePoint(String pointName) {
        if (!printTimeStats) return;
        long time = System.nanoTime();
        timePoints.put(pointName, time);
    }
    private static void addStatPoint(String pointName, String value) {
        statPoints.add(new StatPoint(pointName, value));
    }

    private static String getTime(String fromPoint, String toPoint) {
        long nanoSec = timePoints.get(toPoint) - timePoints.get(fromPoint);
        if (nanoSec < 1_000_000L) {
            return String.format("%d nanoseconds", nanoSec);
        }
        long milliSec = nanoSec / 1_000_000;
        nanoSec %= 1_000_000;
        if (milliSec < 1_000L) {
            return String.format("%d milliseconds %d nanosecond", milliSec, nanoSec);
        }
        long sec = milliSec / 1000;
        milliSec %= 1000;
        return String.format("%d seconds %d milliseconds", sec, milliSec);
    }

    private static void printStatistics() {
        if (!printTimeStats) return;
        System.out.println("Statistics:");
        statPoints.stream().map(StatPoint::toString).forEach(System.out::println);
    }


    private static void processArgs(String[] args) {
        ArgumentsProcessor argsProcessor = new ArgumentsProcessor(args);
        argsProcessor.setAvailableCommands(
                List.of(HELP_COMMAND, OUTPUT_FILE_COMMAND, INPUT_FILE_COMMAND, PRINT_STAT_COMMAND)
        );
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
        if (argsProcessor.hasCommandWithValue(OUTPUT_FILE_COMMAND)) {
            outputFileName = argsProcessor.getValue(OUTPUT_FILE_COMMAND);
        }
        if (argsProcessor.hasCommand(PRINT_STAT_COMMAND)) {
            printTimeStats = true;
            timePoints = new HashMap<>();
            statPoints = new ArrayList<>();
        } else {
            printTimeStats = false;
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
                    %5$s : print statistics"
                Usage example:
                java -jar groupTable.jar %2$s table.xlsx %3$s resultTable.xls
                """;
        System.out.printf(helpMessage,
                HELP_COMMAND,
                INPUT_FILE_COMMAND,
                OUTPUT_FILE_COMMAND,
                DEFAULT_OUTPUT_FILE_NAME,
                PRINT_STAT_COMMAND
        );

    }

}