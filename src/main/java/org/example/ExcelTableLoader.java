package org.example;

import org.example.table.Table;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

// warning: loads to first empty row!!
public class ExcelTableLoader implements TableLoader {

    //todo: add tests

    private static final String CELL_TYPE_ERROR = "Cannot read cell(%d,%d) of %s format.";
    private static final String ILLEGAL_SHEET_NAME_ERROR = "Sheet %s is absent";

    private String sheetName = null;

    @Override
    public Table loadTable(String fileName) throws IOException {
        Workbook wb = loadWorkBook(fileName);
        Sheet sheet;
        if (sheetName != null) {
            sheet = wb.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException(String.format(ILLEGAL_SHEET_NAME_ERROR, sheetName));
            }
        } else {
            //по умолчанию загружается самая первая таблица
            sheet = wb.getSheetAt(0);
        }
        Table table = new Table();
        for (Row row : sheet) {
            if (row.getRowNum() > table.getHeight()) {
                // итератор пропустил одну или несколько строк.
                // значит нашлась как минимум одна пустая строка - можно дальше не загружать
                break;
            }
            org.example.table.Row tableRow = table.addRow();
            for (Cell cell : row) {
                org.example.table.Cell tableCell;
                switch (cell.getCellType()) {
                    case STRING -> tableCell = new org.example.table.Cell(cell.getStringCellValue());
                    case NUMERIC -> tableCell = new org.example.table.Cell(cell.getNumericCellValue());
                    case FORMULA -> tableCell = new org.example.table.Cell(cell.getCellFormula());
                    case BLANK -> tableCell = new org.example.table.Cell();
                    case BOOLEAN -> tableCell = new org.example.table.Cell(String.valueOf(cell.getBooleanCellValue()));
                    default ->
                            throw new IllegalArgumentException(String.format(CELL_TYPE_ERROR, cell.getRowIndex(), cell.getColumnIndex(), cell.getCellType().name()));
                }
                tableRow.setCell(cell.getColumnIndex(), tableCell);
            }
        }
        wb.close();
        return table;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    private Workbook loadWorkBook(String fileName) throws IOException {
        FileInputStream inputStream = new FileInputStream(fileName);
        Workbook wb = null;
        if (fileName.matches(".+[.]xlsx")) {
            wb = new XSSFWorkbook(inputStream);
        } else if (fileName.matches(".+[.]xls")) {
            wb = new HSSFWorkbook(inputStream);
        }
        return wb;
    }

}