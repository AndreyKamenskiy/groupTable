package org.example.table;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TableTest {

    @Test
    void addRow() {
        Table t = new Table();
        Row r = t.addRow();
        assert r.getWidth() == 0;
        assert t.getHeight() == 1;
        assert t.getWidth() == 0;
        t.addRow();
        assert t.getHeight() == 2;
    }

    @Test
    void setRow() {
        Table t = new Table();
        Row row = new Row();
        row.setCell(15, new Cell(5));
        t.setRow(4, row);
        assert t.getHeight() == 5;
        assert t.getWidth() == 16;
        Row row2 = new Row();
        row2.setCell(5, new Cell("asdf"));
        t.setRow(4, row2);
        assert t.getHeight() == 5;
        assert t.getWidth() == 6;
        



    }

    @Test
    void getRow() {
    }

    @Test
    void getCell() {
    }

    @Test
    void setCell() {
    }

    @Test
    void getWidth() {
    }

    @Test
    void getHeight() {
    }
}