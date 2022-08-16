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
        assert c.changeTypeTo(Cell.CellType.DOUBLE);
        assert c.changeTypeTo(Cell.CellType.STRING);
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

    @Test
    void equals() {
        Cell c1 = new Cell();
        Cell c2 = new Cell();
        assert c1.hashCode() == c2.hashCode();
        assert c1.equals(c2);
        c1.changeTypeTo(Cell.CellType.DOUBLE);
        c1.setDoubleValue(50);
        c2.changeTypeTo(Cell.CellType.STRING);
        c2.setStringValue(c1.toString());
        c1.changeTypeTo(Cell.CellType.STRING);
        assert c1.getType() == Cell.CellType.STRING;
        assert c2.getType() == Cell.CellType.STRING;
        assert c2.getStringValue().equals(c1.getStringValue());
        assert c1.equals(c2);
        assert c1.hashCode() == c2.hashCode();

        c1 = new Cell(0.5);
        c2 = new Cell("0.5");
        c2.changeTypeTo(Cell.CellType.DOUBLE);
        assert c1.getType() == Cell.CellType.DOUBLE;
        assert c2.getType() == Cell.CellType.DOUBLE;
        assert Double.compare(c1.getDoubleValue(), c2.getDoubleValue()) == 0;
        assert c1.equals(c2);
        assert c1.hashCode() == c2.hashCode();
    }




}