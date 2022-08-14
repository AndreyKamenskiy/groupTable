package org.example.table;

import org.example.table.Cell;

import java.util.ArrayList;
import java.util.List;

public class Row {

    private final List<Cell> row;

    public Row() {
        this.row = new ArrayList<>();
    }

    public void addCell(Cell cell) {
        row.add(cell);
    }

    public int getWidth() {
        return row.size();
    }

    public void setCell(int columnIndex, Cell cell) {
        row.set(columnIndex, cell);
    }

    public Cell getCell(int columnIndex) {
        if (columnIndex >= row.size() || row.get(columnIndex) == null) {
            // индекс может быть за пределами длины строки или ячейка может быть еще не инициализирована.
            // new Row().setCell(5, new Cell(5)) - создаст строку длиной 6, но первые 5 ячеек будут равны null
            row.set(columnIndex, new Cell());
        }
        return row.get(columnIndex);
    }

}
