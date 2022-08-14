package org.example.table;

import java.util.ArrayList;
import java.util.List;

public class Table {

    private final List<Row> table;

    public Table() {
        table = new ArrayList<>();
    }

    public Row addRow() {
        table.add(new Row());
        return table.get(table.size() - 1);
    }

    public void setRow(int rowIndex, Row row) {
        table.set(rowIndex, row);
    }

    public Row getRow(int rowIndex) {
        if (rowIndex >= table.size() || table.get(rowIndex) == null) {
            //есть две ситуации, когда строка может быть равна null:
            // 1. когда индекс строки больше их текукщего количества
            // 2. когда строка с индексом не создана. например:
            // var table = new Table();
            // table.setRow(2, new Row())
            // этот пример создаст таблицу из трех строк, но проинициализирована будет только строка с индексом 2
            setRow(rowIndex, new Row());
        }
        return table.get(rowIndex);
    }

    public Cell getCell(int rowIndex, int columnIndex) {
        return getRow(rowIndex).getCell(columnIndex);
    }

    public void setCell(int rowIndex, int columnIndex, Cell cell) {
        getRow(rowIndex).setCell(columnIndex, cell);
    }

    public int getWidth() {
        //ширина таблицы равна длине самой длинной строки.
        //строки могут быть разной длины, часть из них могут быть равны null
        return table.stream().mapToInt(row -> row == null ? 0 : row.getWidth()).max().orElse(0);
    }

    public int getHeight() {
        return table.size();
    }

}
