package org.example;

import org.apache.poi.ss.usermodel.*;
import org.example.table.Table;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

// warning: loads to first empty row!!
public class ExcelTableLoader implements TableLoader {


    private static final String CELL_TYPE_ERROR = "Cannot read cell(%d,%d) of %s format.";
    private static final String ILLEGAL_SHEET_NAME_ERROR = "Sheet %s is absent";

    private String sheetName = null;

    @Override
    public Table loadTable(String fileName) throws IOException {
        Set<Integer> columnsWithData = new HashSet<>();
        Workbook wb = loadWorkBook(fileName);
        Sheet sheet = getSheet(wb);
        Table table = new Table();
        int maxColumnIndex = 0;
        for (Row row : sheet) {
            if (row.getRowNum() > table.getHeight()) {
                // итератор пропустил одну или несколько строк.
                // значит нашлась как минимум одна пустая строка - можно дальше не загружать
                break;
            }
            org.example.table.Row tableRow = new org.example.table.Row();
            boolean rowHasData = false;
            for (Cell cell : row) {
                int columnIndex = cell.getColumnIndex();
                org.example.table.Cell tableCell = getTableCell(cell);
                tableRow.setCell(columnIndex, tableCell);
                if (tableCell.getType() != org.example.table.Cell.CellType.EMPTY) {
                    // сохраним все непустые столбцы для поиска полностью пустых столбцов
                    columnsWithData.add(columnIndex);
                    maxColumnIndex = Math.max(maxColumnIndex, columnIndex);
                    rowHasData = true;
                }
            }
            if (rowHasData) {
                table.setRow(table.getHeight(), tableRow);
            }
        }
        wb.close();
        int emptyColumnIndex = findEmptyColumn(columnsWithData, maxColumnIndex);
        if (emptyColumnIndex != -1) {
            deleteColumnsAfterEmpty(table, emptyColumnIndex);
        }
        return table;
    }

    private void deleteColumnsAfterEmpty(Table table, int emptyColumnIndex) {
        int tableHeight = table.getHeight();
        for (int i = 0; i < tableHeight; i++) {
            table.getRow(i).narrow(emptyColumnIndex);
        }
    }

    private int findEmptyColumn(Set<Integer> columnsWithData, int tableWidth) {
        //метод ищет пустые столбцы для удаления всех, что справа от пустого.
        for (int i = 0; i < tableWidth; i++) {
            if (!columnsWithData.contains(i)) {
                return i; // не было непустых ячеек с таким индексом
            }
        }
        return -1; // нет пустых столбцов
    }

    private org.example.table.Cell getTableCell(Cell excelCell) {
        org.example.table.Cell tableCell;
        switch (excelCell.getCellType()) {
            case STRING -> tableCell = new org.example.table.Cell(excelCell.getStringCellValue());
            case NUMERIC -> tableCell = new org.example.table.Cell(excelCell.getNumericCellValue());
            case FORMULA -> {
                String form = excelCell.getCellFormula();
                try {
                    Double.parseDouble(form);
                    form = form.replace('.', ',');
                } catch (NumberFormatException ignored) {
                }
                //особенность appache.poi - она формулу "=1,5" загружает как строку "1,5"
                // поэтому проверяем, если формула может быть преобразована в число, то меняем точку на запятую
                tableCell = new org.example.table.Cell(form);
            }
            case BLANK -> tableCell = new org.example.table.Cell();
            case BOOLEAN -> tableCell = new org.example.table.Cell(String.valueOf(excelCell.getBooleanCellValue()));
            case ERROR -> tableCell = new org.example.table.Cell(String.valueOf(excelCell.toString()));
            default -> throw new IllegalArgumentException(
                    String.format(CELL_TYPE_ERROR, excelCell.getRowIndex(),
                            excelCell.getColumnIndex(), excelCell.getCellType().name())
            );
        }
        return tableCell;
    }

    private Sheet getSheet(Workbook wb) {
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
        return sheet;
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