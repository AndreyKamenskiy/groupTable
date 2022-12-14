package org.example;

import org.example.table.Cell;
import org.example.table.Row;
import org.example.table.Table;

import java.util.*;
import java.util.function.BiFunction;

public class TrieTableGrouper implements TableGrouper {

    //типы столбцов, указываются в первой строке.
    enum ColumnType {
        CRITERIA,
        UNUSED,
        SUM,
        MIN,
        MAX,
        CONCAT
    }

    //класс для реализации аналога префиксного дерева (Trie или бор). в отличие от классического значение хранится
    // только в листьях. промежуточные значения нам не нужны. Так же в отличии от обычного в нашем бору будет
    // фиксированная глубина соответствующая количеству критериев. лист дерева - это строка итоговой таблицы с
    // уникальным набором значений критериев и столбцами мерами.
    private class TrieNode {
        private Map<Cell, TrieNode> children;

        private Row leaf = null;

        public TrieNode() {
            children = new HashMap<>();
        }

        public TrieNode addChild(Cell child) {
            TrieNode newNode = new TrieNode();
            children.put(child, newNode);
            return newNode;
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
                        case SUM -> leaf.addCell(new Cell(0));
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

        public boolean hasLeaf() {
            return leaf != null;
        }

        public Collection<TrieNode> getChildren() {
            return children.values();
        }

        public Row getLeaf() {
            return leaf;
        }
    }

    private static final String EMPTY_TABLE_ERROR = "Grouping error: empty parameters row";
    private static final String UNKNOWN_PARAMETER_ERROR = "Grouping error: unknown parameter %s on cell(0;%d)";
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

    //если в итоговой строке столбец - мера оказался пустым, то этот словарь покажет в какой тип ее конвертировать.
    private static final Map<ColumnType, Cell.CellType> leafEmptyCellType = Map.of(
            ColumnType.SUM, Cell.CellType.DOUBLE,
            ColumnType.MIN, Cell.CellType.DOUBLE,
            ColumnType.MAX, Cell.CellType.DOUBLE,
            ColumnType.CONCAT, Cell.CellType.EMPTY
    );

    private static final BiFunction<Cell, Cell, Cell> sum = (lhs, rhs) -> {
        if (lhs.isEmpty()) return rhs;
        if (rhs.getType() != Cell.CellType.DOUBLE) {
            if (!rhs.changeTypeTo(Cell.CellType.DOUBLE)) {
                throwTransformError(rhs, rhs.getType(), Cell.CellType.DOUBLE, ColumnType.SUM);
            }
        }
        rhs.setDoubleValue(rhs.getDoubleValue() + lhs.getDoubleValue());
        return rhs;
    };

    private static final BiFunction<Cell, Cell, Cell> min = (lhs, rhs) -> {
        //минимум из пустых ячеек = 0;
        //если все положительные и 1 пустая, то она не учитывается в сравненнии
        if (rhs.isEmpty()) return lhs;
        if (lhs.isEmpty()) return rhs;

        if (lhs.getType() != Cell.CellType.DOUBLE) {
            if (!lhs.changeTypeTo(Cell.CellType.DOUBLE)) {
                throwTransformError(lhs, lhs.getType(), Cell.CellType.DOUBLE, ColumnType.MIN);
            }
        }

        if (rhs.getType() != Cell.CellType.DOUBLE) {
            if (!rhs.changeTypeTo(Cell.CellType.DOUBLE)) {
                throwTransformError(rhs, rhs.getType(), Cell.CellType.DOUBLE, ColumnType.MIN);
            }
        }

        return rhs.getDoubleValue() < lhs.getDoubleValue() ? rhs : lhs;
    };

    private static final BiFunction<Cell, Cell, Cell> max = (lhs, rhs) -> {
        //максимум из пустых ячеек = 0;
        //если все отрицательные и есть пустые, то они не учитываются в сравненнии
        if (rhs.isEmpty()) return lhs;
        if (lhs.isEmpty()) return rhs;

        if (lhs.getType() != Cell.CellType.DOUBLE) {
            if (!lhs.changeTypeTo(Cell.CellType.DOUBLE)) {
                throwTransformError(lhs, lhs.getType(), Cell.CellType.DOUBLE, ColumnType.MAX);
            }
        }

        if (rhs.getType() != Cell.CellType.DOUBLE) {
            if (!rhs.changeTypeTo(Cell.CellType.DOUBLE)) {
                throwTransformError(rhs, rhs.getType(), Cell.CellType.DOUBLE, ColumnType.MAX);
            }
        }

        return lhs.getDoubleValue() < rhs.getDoubleValue() ? rhs : lhs;
    };

    private static final BiFunction<Cell, Cell, Cell> concat = (lhs, rhs) -> {
        if (lhs.isEmpty() && rhs.isEmpty()) {
            // если обе пустые вернем пустую
            return rhs;
        }
        if (lhs.getType() != Cell.CellType.STRING) {
            //в строку можно сконвертировать все что угодно, результат можно не проверять
            lhs.changeTypeTo(Cell.CellType.STRING);
        }
        lhs.setStringValue(lhs.getStringValue() + rhs.toString());
        return lhs;
    };

    //словарь позволяет выбирать обработчик для меры. Нужен для удобства добавления новых видов мер.
    private static final Map<ColumnType, BiFunction<Cell, Cell, Cell>> typeToProcessor = Map.of(
            ColumnType.SUM, sum,
            ColumnType.MIN, min,
            ColumnType.MAX, max,
            ColumnType.CONCAT, concat
    );

    private Table inTable;

    //хранит параметы первой строки
    private ColumnType[] parameters;

    //хранит номера колонок с критериями. нужен, чтобы не перебирать все ячейки строки при поиске совпадающего
    // набора критериев, а сразу итерировать по столбцам-критериям
    private int[] criteriaColumns;

    private int criteriaNum;

    @Override
    public Table groupTable(Table inTable) throws IllegalArgumentException {
        this.inTable = inTable;
        //разбираем первую строку с параметрами
        parseParameters();
        //строим бор.
        TrieNode root = buildTrie();
        //обходим бор в ширину, собираем все листья в итоговую таблицу
        return trieToTable(root);
    }

    private Table trieToTable(TrieNode root) {
        Table table = new Table();
        Queue<TrieNode> queue = new ArrayDeque<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            TrieNode node = queue.poll();
            if (node.hasLeaf()) {
                Row leaf = node.getLeaf();
                checkEmptyLeafCells(leaf);
                table.setRow(table.getHeight(), leaf);
            } else {
                queue.addAll(node.getChildren());
            }
        }
        return table;
    }

    private void checkEmptyLeafCells(Row leaf) {
        //метод нужен для замены пустых ячеек в итоговой таблице на соответствующие типы.
        // для sum, min, max невозможны пустые ячейки в итоге. Заменим их на соответствующие типы.
        for (int column = 0, leafColumn = 0; column < parameters.length; ++column) {
            ColumnType type = parameters[column];
            if (type == ColumnType.UNUSED) {
                //для unused столбцов не увеличиваем указатель leafColumn,т.к. эти столбцы отсутствуют в итоговой
                continue;
            }
            if (type == ColumnType.CRITERIA || leaf.getCell(leafColumn).getType() != Cell.CellType.EMPTY) {
                ++leafColumn;
                continue;
            }
            // нашли пустую ячейку. указатель parameters[column] указывает тип меры.
            // leafColumn указывает на столбец в листе, соответствующий column
            Cell cell = leaf.getCell(leafColumn);
            ColumnType measureType = parameters[column];
            cell.changeTypeTo(leafEmptyCellType.get(measureType));
            ++leafColumn;
        }
    }

    private void parseParameters() throws IllegalArgumentException {
        if (inTable.getHeight() < 2) {
            //минимально должны быть строка с параметрами и строка с данными
            throw new IllegalArgumentException(EMPTY_TABLE_ERROR);
        }
        Row firstRow = inTable.getRow(0);
        int inTableWidth = inTable.getWidth();
        parameters = new ColumnType[inTableWidth];
        criteriaNum = 0;
        for (int column = 0; column < inTableWidth; ++column) {
            parseParameterCell(column, firstRow.getCell(column));
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
            }
            case DOUBLE -> throw new IllegalArgumentException(
                    String.format(UNKNOWN_PARAMETER_ERROR, cell, column)
            );
        }
    }

    private TrieNode buildTrie() {
        TrieNode root = new TrieNode();
        for (int rowIndex = 1; rowIndex < inTable.getHeight(); ++rowIndex) {
            Row row = inTable.getRow(rowIndex);
            TrieNode currentNode = root;
            //переберем критерии, чтобы добраться до листа
            //глубина бора у нас равна количеству критериев. Лист-строка есть только у последнего узла в ветке.
            for (int criteriaIndex : criteriaColumns) {
                currentNode = currentNode.getOrCreateChild(row.getCell(criteriaIndex));
            }
            //теперь в currentNode - нода с листом. добавим к листу строку
            currentNode.addToLeaf(row);
        }
        return root;
    }

}
