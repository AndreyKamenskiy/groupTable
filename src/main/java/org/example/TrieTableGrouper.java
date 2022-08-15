package org.example;

import org.example.table.Cell;
import org.example.table.Row;
import org.example.table.Table;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class TrieTableGrouper implements TableGrouper {

    enum ColumnType {
        CRITERIA,
        UNUSED,
        SUM,
        MIN,
        MAX,
        CONCAT
    }

    class TrieNode {
        private Map<Cell, TrieNode> children;

        private Row leaf = null;

        public TrieNode() {
            children = new HashMap<>();
        }

        public TrieNode addChild(Cell child) {
            return children.put(child, new TrieNode());
        }

        public boolean hasChild(Cell child) {
            return children.containsKey(child);
        }

        public TrieNode getOrCreateChild(Cell child) {
            if (hasChild(child)) {
                return children.get(child);
            }
            return addChild(child);
        }

        public void addToLeaf(Row row) {
            //метод вызывается только для конца цепочки критериев.
            //у промежуточных звеньев бора(trie) не должно быть листьев
            if (leaf == null) {
                children = null; // из листьев не растут ветки - нет детей
                leaf = new Row();
                //так как строка может быть уже всей таблицы переберем весь массив параметры
                // он по ширине равен таблице
                for (int column = 0; column < parameters.length; ++column) {
                    switch (parameters[column]) {
                        // case UNUSED -> nothing to do
                        case CRITERIA -> leaf.addCell(row.getCell(column));
                        case MIN, MAX -> leaf.addCell(new Cell());
                        case CONCAT -> leaf.addCell(new Cell(""));
                    }
                }
            }
            //лист уже есть, ячейки критериев заполнены, вычислим меры
            for (int column = 0, leafColumn = 0; column < parameters.length; ++column) {
                ColumnType type = parameters[column];
                if (type == ColumnType.UNUSED) {
                    continue;
                }
                if (type == ColumnType.CRITERIA) {
                    ++leafColumn;
                    continue;
                }
                leaf.setCell(
                        leafColumn,
                        typeToProcessor.get(type).apply(leaf.getCell(leafColumn), row.getCell(column))
                );
                ++leafColumn;
            }

        }
    }

    private static final String EMPTY_TABLE_ERROR = "Grouping error: empty parameters row";
    private static final String UNKNOWN_PARAMETER_ERROR = "Grouping error: unknown parameter %s on cell(0;%d)";
    private static final String NO_CRITERIA_ERROR = "Grouping error: there are no criteria columns in the table";
    private static final String TRANSFORM_ERROR =
            "Grouping error: could not transform %s from type %s to %s for %s measure.";

    private static void throwTransformError(
            Cell cell,
            Cell.CellType fromType,
            Cell.CellType toType,
            ColumnType measure) throws IllegalArgumentException {
        throw new IllegalArgumentException(String.format(
                TRANSFORM_ERROR,
                cell,
                fromType.name(),
                toType.name(),
                measure.name()
        ));
    }

    private static final Map<String, ColumnType> nameToType = Map.of(
            "-", ColumnType.UNUSED,
            "sum", ColumnType.SUM,
            "min", ColumnType.MIN,
            "max", ColumnType.MAX,
            "concat", ColumnType.CONCAT
    );

    private static BiFunction<Cell, Cell, Cell> sum = (lhs, rhs) -> {
        if (lhs.isEmpty()) return rhs;
        if (rhs.getType() != Cell.CellType.DOUBLE) {
            if (!rhs.changeTypeTo(Cell.CellType.DOUBLE)) {
                throwTransformError(rhs, rhs.getType(), Cell.CellType.DOUBLE, ColumnType.SUM);
            }
        }
        rhs.setDoubleValue(rhs.getDoubleValue() + lhs.getDoubleValue());
        return rhs;
    };

    private static BiFunction<Cell, Cell, Cell> min = (lhs, rhs) -> {
        //минимум из пустых ячеек = 0;
        //если все положительные и 1 пустая, то она не учитывается в сравненнии
        if (lhs.isEmpty()) {
            if (rhs.isEmpty()) {
                return rhs;
            } else if (rhs.getType() != Cell.CellType.DOUBLE) {
                if (!rhs.changeTypeTo(Cell.CellType.DOUBLE)) {
                    throwTransformError(rhs, rhs.getType(), Cell.CellType.DOUBLE, ColumnType.MIN);
                }
            }
            //любое число меньше, чем пустая ячейка
            return rhs;
        }
        if (rhs.getType() != Cell.CellType.DOUBLE) {
            if (!rhs.changeTypeTo(Cell.CellType.DOUBLE)) {
                throwTransformError(rhs, rhs.getType(), Cell.CellType.DOUBLE, ColumnType.SUM);
            }
        }

        return rhs.getDoubleValue() < lhs.getDoubleValue() ? rhs : lhs;
    };

    private static BiFunction<Cell, Cell, Cell> max = (lhs, rhs) -> {
        //максимум из пустых ячеек = 0;
        //если все отрицательные и есть пустые, то они не учитываются в сравненнии
        if (lhs.isEmpty()) {
            if (rhs.isEmpty()) {
                return rhs;
            } else if (rhs.getType() != Cell.CellType.DOUBLE) {
                if (!rhs.changeTypeTo(Cell.CellType.DOUBLE)) {
                    throwTransformError(rhs, rhs.getType(), Cell.CellType.DOUBLE, ColumnType.MIN);
                }
            }
            //любое число больше, чем пустая ячейка
            return rhs;
        }
        if (rhs.getType() != Cell.CellType.DOUBLE) {
            if (!rhs.changeTypeTo(Cell.CellType.DOUBLE)) {
                throwTransformError(rhs, rhs.getType(), Cell.CellType.DOUBLE, ColumnType.SUM);
            }
        }

        return lhs.getDoubleValue() < rhs.getDoubleValue() ? rhs : lhs;
    };

    private static BiFunction<Cell, Cell, Cell> concat = (lhs, rhs) -> {
        if (lhs.isEmpty() && rhs.isEmpty()) {
            // если обе пустые вернем пустую
            return rhs;
        }
        if (lhs.getType() != Cell.CellType.STRING) {
            //в строку можно сконвертировать все что угодно, результат можно не проверять
            rhs.changeTypeTo(Cell.CellType.STRING);
        }
        lhs.setStringValue(lhs.getStringValue() + rhs.toString());
        return lhs;
    };


    private static final Map<ColumnType, BiFunction<Cell, Cell, Cell>> typeToProcessor = Map.of(
            ColumnType.SUM, sum,
            ColumnType.MIN, min,
            ColumnType.MAX, max,
            ColumnType.CONCAT, concat
    );

    private Table inTable;

    private ColumnType[] parameters;

    private int[] criteriaColumns;

    private int criteriaNum;
    private int unusedNum;

    @Override
    public Table groupTable(Table inTable) throws IllegalArgumentException {
        this.inTable = inTable;
        //parse 0 row.
        parseParameters();
        //make trie
        TrieNode root = buildTrie();
        //convert trie to the table
        return trieToTable(root);
    }

    private Table trieToTable(TrieNode root) {
        Table table = new Table();
        //todo: write method


        return table;
    }

    private void parseParameters() throws IllegalArgumentException {
        if (inTable.getHeight() < 2) {
            throw new IllegalArgumentException(EMPTY_TABLE_ERROR);
        }
        Row firstRow = inTable.getRow(0);
        int inTableWidth = inTable.getWidth();
        parameters = new ColumnType[inTableWidth];
        criteriaNum = 0;
        unusedNum = 0;
        for (int column = 0; column < inTableWidth; ++column) {
            parseParameterCell(column, firstRow.getCell(column));
        }
        if (criteriaNum == 0) {
            throw new IllegalArgumentException(NO_CRITERIA_ERROR);
        }
        criteriaColumns = new int[criteriaNum];
        int i = 0;
        for (int column = 0; column < inTableWidth; ++column) {
            if (parameters[column] == ColumnType.CRITERIA) {
                criteriaColumns[i++] = column;
            }
        }
    }

    private void parseParameterCell(int column, Cell cell) throws IllegalArgumentException {
        switch (cell.getType()) {
            case EMPTY -> {
                parameters[column] = ColumnType.CRITERIA;
                ++criteriaNum;
            }
            case STRING -> {
                String lowCaseCellValue = cell.getStringValue().toLowerCase();
                if (!nameToType.containsKey(lowCaseCellValue)) {
                    throw new IllegalArgumentException(String.format(UNKNOWN_PARAMETER_ERROR, cell, column));
                }
                parameters[column] = nameToType.get(lowCaseCellValue);
                if (parameters[column] == ColumnType.UNUSED) {
                    ++unusedNum;
                }
            }
            case DOUBLE -> throw new IllegalArgumentException(
                    String.format(UNKNOWN_PARAMETER_ERROR, cell, column)
            );
        }
    }

    private TrieNode buildTrie() {
        TrieNode root = new TrieNode();
        for (int rowIndex = 1; rowIndex < inTable.getHeight(); rowIndex++) {
            Row row = inTable.getRow(rowIndex);
            TrieNode currentNode = root;
            //переберем параметры, чтобы добраться до листа
            for (int criteriaIndex : criteriaColumns) {
                currentNode = currentNode.getOrCreateChild(row.getCell(criteriaIndex));
            }
            //теперь в currentNode - нода с листом. добавим к листу строку
            currentNode.addToLeaf(row);
        }
        return root;
    }

}
