package org.example.table;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    @Test
    void getType() {
        Cell c = new Cell();
        assert c.getType() == Cell.CellType.EMPTY;
        c = new Cell(570);
        assert c.getType() == Cell.CellType.DOUBLE;
        c = new Cell("");
        assert c.getType() == Cell.CellType.STRING;
        c = new Cell("dfgsd");
        assert c.getType() == Cell.CellType.STRING;
    }

    @Test
    void getDoubleValue() {
        Cell c = new Cell(-5);
        assert c.getDoubleValue() == -5;
        final Cell cc = new Cell("asdf");
        assertThrowsExactly(IllegalArgumentException.class, cc::getDoubleValue);
    }

    @Test
    void getStringValue() {
        Cell c = new Cell("fine test");
        assert "fine test".equals(c.getStringValue());
        final Cell cc = new Cell(8E-50);
        assertThrowsExactly(IllegalArgumentException.class, cc::getStringValue);
    }

    @Test
    void changeTypeTo() {
        Cell c = new Cell();
        assertFalse(c.changeTypeTo(Cell.CellType.DOUBLE));
        assertFalse(c.changeTypeTo(Cell.CellType.STRING));
        assertFalse(c.changeTypeTo(Cell.CellType.EMPTY)); // уже пустая ячейка
        c = new Cell(5);
        assertFalse(c.changeTypeTo(Cell.CellType.EMPTY));
        assert c.changeTypeTo(Cell.CellType.STRING);
        assert c.getType() == Cell.CellType.STRING;
        assert "5.0".equals(c.getStringValue());
        assertFalse(c.changeTypeTo(Cell.CellType.EMPTY));
        assert c.changeTypeTo(Cell.CellType.DOUBLE);
        assert c.getType() == Cell.CellType.DOUBLE;
        assert c.getDoubleValue() == 5;
        c = new Cell("--5");
        assertFalse(c.changeTypeTo(Cell.CellType.DOUBLE));
        c = new Cell("");
        assertFalse(c.changeTypeTo(Cell.CellType.DOUBLE));
        assert c.changeTypeTo(Cell.CellType.EMPTY); // а вот их пустой строки можно сделать пустую ячейку
        assert c.isEmpty();
        assert c.getType() == Cell.CellType.EMPTY;
    }

    @Test
    void isEmpty() {
        Cell c = new Cell();
        assert c.isEmpty();
        c = new Cell(0);
        assertFalse(c.isEmpty());
        c = new Cell("");
        assert c.isEmpty();
        c = new Cell(-5);
        assertFalse(c.isEmpty());
        c = new Cell("foo");
        assertFalse(c.isEmpty());
    }
}