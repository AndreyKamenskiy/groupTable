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

public class Main {

    public static void main(String[] args) {
        // ParseCommandLine();
        // cl
        // TableLoader loader = new ExcellTableLoader(fileName);
        //Table table = loader.loadTable();
        //TableProcessor processor = new TableProcessor();
        //processor.groupTable(table);
        //TableSaver saver = new ExcellTableSaver();



        String templateFile = "src/test/testResources/simpleTable.xlsx";

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

            for (Sheet sheet : wb ) {
                System.out.printf("Sheet name: %s%n", sheet.getSheetName());
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        System.out.printf("row: %d column: %d type: %s%n",
                                cell.getRowIndex(), cell.getColumnIndex(),
                                cell.getCellType().name());
                        switch (cell.getCellType()) {
                            case STRING -> System.out.printf("String value: \"%s\"%n", cell.getStringCellValue());
                            case NUMERIC ->
                                    System.out.printf("Numeric value: %f%n", cell.getNumericCellValue());
                            case FORMULA ->
                                    System.out.printf("Formula value: %s%n", cell.getCellFormula());
                        }
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}