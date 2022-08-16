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
        c = new Cell("some text");
        assert c.getType() == Cell.CellType.STRING;
    }

    @Test
    void getDoubleValue() {
        Cell c = new Cell(-5);
        assert c.getDoubleValue() == -5;
        final Cell cc = new Cell("deus moris");
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


    @Test
    void setDoubleValue() {
        Cell c = new Cell();
        assertThrowsExactly(IllegalArgumentException.class, () -> c.setDoubleValue(100));
        c.changeTypeTo(Cell.CellType.DOUBLE);
        assert c.getDoubleValue() == 0;
        c.setDoubleValue(50);
        assert c.getDoubleValue() == 50;
        Cell c2 = new Cell("initValue");
        assertThrowsExactly(IllegalArgumentException.class, () -> c2.setDoubleValue(100));
    }

    @Test
    void setStringValue() {
        Cell c = new Cell();
        assertThrowsExactly(IllegalArgumentException.class, () -> c.setStringValue("100"));
        c.changeTypeTo(Cell.CellType.STRING);
        c.setStringValue("50");
        assert c.getStringValue().equals("50");
        Cell c2 = new Cell(45);
        assertThrowsExactly(IllegalArgumentException.class, () -> c2.setStringValue("450"));
    }

    @Test
    void toStringTest() {
        Cell c = new Cell();
        assert "".equals(c.toString());
        c = new Cell("test");
        assert "test".equals(c.toString());
        c = new Cell(50);
        assertEquals("50", c.toString());
        c.changeTypeTo(Cell.CellType.STRING);
        assertEquals("50", c.getStringValue());
        c = new Cell(100.000000000000001);
        Cell c1 = new Cell(100);
        assertEquals(c.getDoubleValue(), c1.getDoubleValue());
        assert c.equals(c1);
        assertEquals(c.hashCode(), c1.hashCode());

        long l1 = 100000000000000001L;
        long l2 = 100000000000000000L;
        c = new Cell(l1);
        c1 = new Cell(l2);
        assertEquals(c.getDoubleValue(), c1.getDoubleValue());
        assertEquals(c.toString(), c1.toString());
        assertEquals(String.valueOf(l2), c1.toString());
        assertNotEquals(String.valueOf(l1), c1.toString());
        assert c.equals(c1);
        assertEquals(c.hashCode(), c1.hashCode());
    }


}