package org.example.table;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RowTest {

    @Test
    void addCell() {
        Row r = new Row();
        r.addCell(new Cell(5));
        assert(r.getWidth() == 1);
        assert(r.getCell(0).getDoubleValue() == 5);
        r.addCell(new Cell(1));
        assert(r.getWidth() == 2);
        assert(r.getCell(1).getDoubleValue() == 1);
        r.addCell(new Cell("test"));
        assert(r.getWidth() == 3);
        assert("test".equals(r.getCell(2).getStringValue()));
        r.setCell(4, new Cell("s1"));
        r.addCell(new Cell(4));
        assert(r.getWidth() == 6);
        assert("s1".equals(r.getCell(4).getStringValue()));
        assert(r.getCell(3).isEmpty());
        assert(r.getCell(5).getDoubleValue() == 4);
    }

    @Test
    void getWidth() {
        Row r = new Row();
        assert(r.getWidth() == 0);
        r.addCell(new Cell(3));
        assert(r.getWidth() == 1);
        r.setCell(5, new Cell(50));
        assert(r.getWidth() == 6);
        assert(r.getCell(5).getDoubleValue() == 50);
        assert(r.getCell(8).isEmpty());
        assert(r.getWidth() == 9);
        assert(r.getCell(6).isEmpty());
        assert(r.getCell(7).isEmpty());
    }


    @Test
    void setCell() {
        Row r = new Row();
        r.setCell(5, new Cell("5ths"));
        assert(r.getWidth() == 6);
        assert(r.getCell(0).isEmpty());
        assert(r.getCell(1).isEmpty());
        assert(r.getCell(2).isEmpty());
        assert(r.getCell(3).isEmpty());
        assert(r.getCell(4).isEmpty());
        assertThrowsExactly(IndexOutOfBoundsException.class, () -> r.getCell(-1));
        r.setCell(5, new Cell());
        assert(r.getWidth() == 0);


    }

    @Test
    void getCell() {
        Row r = new Row();
        assert(r.getCell(0).isEmpty());
        assert(r.getWidth() == 1);
        r.setCell(2, new Cell("asd"));
        assert(r.getWidth() == 3);
        assertEquals(r.getCell(2).getStringValue(), "asd");
        assertThrowsExactly(IndexOutOfBoundsException.class, () -> r.getCell(-500));
    }
}