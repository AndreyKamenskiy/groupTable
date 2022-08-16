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

    public void setCell(int columnIndex, Cell cell) throws IndexOutOfBoundsException {
        if (columnIndex + 1 == row.size() && cell != null && cell.isEmpty()) {
            //Если последнюю ячейку меняют пустую, то удалим все пустые ячейки с хвоста
            row.remove(columnIndex);
            while (!row.isEmpty() && row.get(row.size() - 1).isEmpty()) {
                row.remove(row.size() - 1);
            }
            return;
        }
        if (columnIndex >= row.size()) {
            for (int i = row.size(); i < columnIndex; ++i) {
                row.add(new Cell());
            }
            row.add(cell);
        } else {
            row.set(columnIndex, cell);
        }
    }

    public Cell getCell(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex >= row.size() || row.get(columnIndex) == null) {
            // индекс может быть за пределами длины строки,
            setCell(columnIndex, new Cell());
        }
        return row.get(columnIndex);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.size(); i++) {
            sb.append(getCell(i).toString());
            sb.append(' ');
        }
        return sb.toString();
    }

}
