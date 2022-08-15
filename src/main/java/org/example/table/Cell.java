package org.example.table;

public class Cell {

    private static final String TYPE_MISMATCH_ERROR = "Cell type mismatch";

    public enum CellType {
        DOUBLE,
        STRING,
        EMPTY;
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
        //todo: add tests for method
        if (type != CellType.DOUBLE) throw new IllegalArgumentException(TYPE_MISMATCH_ERROR);
        doubleValue = value;
    }

    public void setStringValue(String stringValue) throws IllegalArgumentException {
        //todo: add tests for method
        if (type != CellType.STRING) throw new IllegalArgumentException(TYPE_MISMATCH_ERROR);
        this.stringValue = stringValue;
    }

    public boolean changeTypeTo(CellType toType) {
        boolean res = false;
        switch (type) {
            case DOUBLE -> {
                if (toType == CellType.STRING) {
                    type = CellType.STRING;
                    stringValue = String.valueOf(doubleValue);
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
                //todo: add tests for case;
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
        //todo: add test
        return switch (type) {
            case DOUBLE -> String.valueOf(doubleValue);
            case STRING -> stringValue;
            case EMPTY -> "";
        };
    }

    //todo: add hashCode and equals realization

}
