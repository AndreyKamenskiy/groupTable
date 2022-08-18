package org.example;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.table.Table;

import java.io.*;

public class ExcelTableSaver implements TableSaver {

    private Workbook selectWBClass(String fileName) {
        Workbook wb = null;
        if (fileName.matches(".+[.]xlsx")) {
            wb = new XSSFWorkbook();
        } else if (fileName.matches(".+[.]xls")) {
            wb = new HSSFWorkbook();
        }
        return wb;
    }

    @Override
    public void saveTable(String fileName, Table table) throws IOException {
        Workbook wb = selectWBClass(fileName);
        Sheet sheet = wb.createSheet("grouped table");

        for (int rowIndex = 0; rowIndex < table.getHeight(); ++rowIndex) {
            Row excelRow = sheet.createRow(rowIndex);
            org.example.table.Row tableRow = table.getRow(rowIndex);
            //так как ширина строк в table может быть разная, то перебирать по ширине строки
            // выгоднее, чем по ширине таблицы, что равна ширине самой широкой строки.
            for (int colIndex = 0; colIndex < tableRow.getWidth(); ++colIndex) {
                org.example.table.Cell tableCell = tableRow.getCell(colIndex);
                switch (tableCell.getType()) {
                    case DOUBLE -> excelRow.createCell(colIndex).setCellValue(tableCell.getDoubleValue());
                    case STRING -> excelRow.createCell(colIndex).setCellValue(tableCell.getStringValue());
                    // пустые ячейки сохранять не будем
                }
            }
        }
        wb.write(new FileOutputStream(fileName));
        wb.close();
    }

}