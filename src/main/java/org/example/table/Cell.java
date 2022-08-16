package org.example.table;

import java.util.Objects;

public class Cell {

    private static final String TYPE_MISMATCH_ERROR = "Cell type mismatch";

    public enum CellType {
        DOUBLE,
        STRING,
        EMPTY
    }
    private CellType type;

    private double doubleValue;

    private String stringValue;

    public Cell(double doubleValue) {
        this.doubleValue = doubleValue;
        type = CellType.DOUBLE;
    }

    public Cell(String stringValue) {
        this.stringValue = stringValue;
        type = CellType.STRING;
    }

    public Cell() {
        type = CellType.EMPTY;
    }

    public CellType getType() {
        return type;
    }

    public double getDoubleValue() throws IllegalArgumentException {
        if (type != CellType.DOUBLE) throw new IllegalArgumentException(TYPE_MISMATCH_ERROR);
        return doubleValue;
    }

    public String getStringValue() {
        if (type != CellType.STRING) throw new IllegalArgumentException(TYPE_MISMATCH_ERROR);
        return stringValue;
    }

    public void setDoubleValue(double value) throws IllegalArgumentException {
        if (type != CellType.DOUBLE) throw new IllegalArgumentException(TYPE_MISMATCH_ERROR);
        doubleValue = value;
    }

    public void setStringValue(String stringValue) throws IllegalArgumentException {
        if (type != CellType.STRING) throw new IllegalArgumentException(TYPE_MISMATCH_ERROR);
        this.stringValue = stringValue;
    }

    public boolean changeTypeTo(CellType toType) {
        boolean res = false;
        switch (type) {
            case DOUBLE -> {
                if (toType == CellType.STRING) {
                    type = CellType.STRING;
                    stringValue = doubleToString(doubleValue);
                    // todo double 5.0 to String == "5", but not "5.0";
                    res = true;
                }
            }
            case STRING -> {
                if (toType == CellType.DOUBLE) {
                    try {
                        doubleValue = Double.parseDouble(stringValue);
                        type = CellType.DOUBLE;
                        res = true;
                    } catch (NumberFormatException ex) {
                        // не получилось преобразовать
                    }
                } else if (toType == CellType.EMPTY && stringValue.isEmpty()) {
                    type = CellType.EMPTY;
                    res = true;
                }
            }
            case EMPTY -> {
                if (toType == CellType.DOUBLE) {
                    type = CellType.DOUBLE;
                    doubleValue = 0;
                    res = true;
                } else if (toType == CellType.STRING) {
                    type = CellType.STRING;
                    res = true;
                    stringValue = "";
                }
            }
        }
        return res;
    }

    public boolean isEmpty() {
        return type == CellType.EMPTY || (type == CellType.STRING && stringValue.isEmpty());
    }

    @Override
    public String toString() {
        return switch (type) {
            case DOUBLE -> doubleToString(doubleValue);
            case STRING -> stringValue;
            case EMPTY -> "";
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cell cell = (Cell) o;
        if (type != cell.type) return false;
        boolean res = false;
        switch (type) {
            case DOUBLE -> res = Double.compare(cell.doubleValue, doubleValue) == 0;
            case EMPTY -> res = true;
            case STRING -> res = Objects.equals(stringValue, cell.stringValue);
        }
        return res;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        switch (type) {
            case DOUBLE -> {
                long temp = Double.doubleToLongBits(doubleValue);
                result = 31 * result + (int) (temp ^ (temp >>> 32));
            }
            case STRING -> result = 31 * result + (stringValue != null ? stringValue.hashCode() : 0);
        }
        return result;
    }

    private String doubleToString(double val) {
        if (val % 1 == 0) {
            return String.valueOf((long) val);
        }
        return String.valueOf(val);
    }

}
