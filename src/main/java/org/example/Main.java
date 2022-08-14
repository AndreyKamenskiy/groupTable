package org.example;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.table.Table;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class Main {

    //todo: add tests for null arguments passed to every method;

    private static final String HELP_COMMAND = "-help";
    private static final String INPUT_FILE_COMMAND = "-inputFile";
    private static final String OUTPUT_FILE_COMMAND = "-outputFile";
    private static final String DEFAULT_OUTPUT_FILE_NAME = "groupingTable.xls";



    public static void main(String[] args) {
        ArgumentsProcessor argsProcessor = new ArgumentsProcessor(args);
        argsProcessor.setAvailableCommands(List.of(HELP_COMMAND, OUTPUT_FILE_COMMAND, INPUT_FILE_COMMAND));
        argsProcessor.parseArguments();
        if (argsProcessor.hasCommand(HELP_COMMAND)) {
            showHelp();
            return;
        }
        if (!argsProcessor.hasCommandWithValue(INPUT_FILE_COMMAND)) {
            System.out.printf("Input file name is absent. Use: %s <filename>", INPUT_FILE_COMMAND);
            return;
        }
        String fileName = argsProcessor.getValue(INPUT_FILE_COMMAND);

        TableLoader loader = chooseLoader(fileName);

        Table table;
        try {
            table = loader.loadTable(fileName);
        } catch (IOException e) {
            System.out.printf("Load table error: %s", e.getMessage());
            return;
        }

        for (int row = 0; row < table.getHeight(); ++row) {
            for (int col = 0; col < table.getWidth(); col++) {
                System.out.printf("%s\t", table.getCell(row, col));
            }
            System.out.print('\n');
        }


    }

    private static TableLoader chooseLoader(String fileName) {
        if (fileName.matches(".+[.]xlsx?")) {
            return new ExcelTableLoader();
        }
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
                java -jar grouptable.jar %2$s table.xlsx %3$s resultTable.xls
                """;
        System.out.printf(helpMessage,
                HELP_COMMAND,
                INPUT_FILE_COMMAND,
                OUTPUT_FILE_COMMAND,
                DEFAULT_OUTPUT_FILE_NAME
        );

    }

}