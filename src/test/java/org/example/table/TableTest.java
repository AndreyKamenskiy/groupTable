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
        row2.setCell(5, new Cell("test"));
        t.setRow(4, row2);
        assert t.getHeight() == 5;
        assert t.getWidth() == 6;
    }

    @Test
    void getRow() {
        Table t = new Table();
        assert t.getHeight() == 0;
        Row r = t.getRow(3);
        assert t.getHeight() == 4;
        assert r.getWidth() == 0;
        assert t.getWidth() == 0;
        r.setCell(60, new Cell("rrr"));
        assert t.getWidth() == 61;
        t.getRow(50);
        assert t.getHeight() == 51;
        assert t.getRow(3) == r;
        assert t.getRow(35).getWidth() == 0;
    }

    @Test
    void getCell() {
        Table t = new Table();
        Cell c = t.getCell(0, 15);
        assert c.isEmpty();
        assert t.getWidth() == 16;
        assert t.getHeight() == 1;
        Row r = new Row();
        r.setCell(55, new Cell(555));
        t.setRow(1, r);
        c = t.getCell(1, 55);
        assert c.getDoubleValue() == 555;
        assert t.getWidth() == 56;
    }

    @Test
    void setCell() {
        Table t = new Table();
        t.setCell(50, 100, new Cell("final"));
        assert t.getWidth() == 101;
        assert t.getHeight() == 51;
        t.setCell(50, 100, new Cell());
        assert t.getWidth() == 0;
        assert t.getHeight() == 51;
    }

    @Test
    void exceptionsTest() {
        assertThrowsExactly(IndexOutOfBoundsException.class, () -> new Table().setRow(-1, new Row()));
        assertThrowsExactly(IndexOutOfBoundsException.class, () -> new Table().getRow(-100));
        assertThrowsExactly(IndexOutOfBoundsException.class, () -> new Table().getCell(-100, 0));
        assertThrowsExactly(IndexOutOfBoundsException.class, () -> new Table().getCell(100, -50));
        assertThrowsExactly(IndexOutOfBoundsException.class, () -> new Table().getCell(-100, 0));
        assertThrowsExactly(IndexOutOfBoundsException.class, () ->
                new Table().setCell(100, -50, new Cell(52)));
        assertThrowsExactly(IndexOutOfBoundsException.class, () ->
                new Table().setCell(-90, 5, new Cell(532)));
    }

}