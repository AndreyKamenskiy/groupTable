package org.example;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class Main {

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




        // ParseCommandLine();
        // cl
        // TableLoader loader = new ExcellTableLoader(fileName);
        //Table table = loader.loadTable();
        //TableProcessor processor = new TableProcessor();
        //processor.groupTable(table);
        //TableSaver saver = new ExcellTableSaver();


        /*String templateFile = "src/test/testResources/simpleTable.xlsx";

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(templateFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            Workbook wb = null;
            if (templateFile.matches(".+[.]xlsx")) {
                wb = new XSSFWorkbook(inputStream);
            } else if (templateFile.matches(".+[.]xls")) {
                wb = new HSSFWorkbook(inputStream);
            }

            for (Sheet sheet : wb) {
                System.out.printf("Sheet name: %s%n", sheet.getSheetName());
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        System.out.printf("row: %d column: %d type: %s%n",
                                cell.getRowIndex(), cell.getColumnIndex(),
                                cell.getCellType().name());
                        switch (cell.getCellType()) {
                            case STRING -> System.out.printf("String value: \"%s\"%n", cell.getStringCellValue());
                            case NUMERIC -> System.out.printf("Numeric value: %f%n", cell.getNumericCellValue());
                            case FORMULA -> System.out.printf("Formula value: %s%n", cell.getCellFormula());
                        }
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
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