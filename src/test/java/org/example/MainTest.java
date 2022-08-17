package org.example;

import org.example.table.Cell;
import org.example.table.Row;
import org.example.table.Table;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class MainTest {
    @ParameterizedTest
    @CsvSource(value = {
            "src/test/testResources/simpleTests/concat.xlsx",
            "src/test/testResources/simpleTests/emptyLeafs2.xlsx",
            "src/test/testResources/simpleTests/tableSize.xlsx",
            "src/test/testResources/simpleTests/simple.xlsx",
            "src/test/testResources/simpleTests/emptymeasures.xlsx",
            "src/test/testResources/simpleTests/noCriteria.xlsx",
            "src/test/testResources/simpleTests/noMeasures1.xlsx",
            "src/test/testResources/simpleTests/noMeasures2.xlsx",
            "src/test/testResources/simpleTests/onlyUnused.xlsx",
            "src/test/testResources/simpleTests/criteria1.xlsx",
            "src/test/testResources/simpleTests/criteria2.xlsx",
            "src/test/testResources/simpleTests/emptyLeafs.xlsx",
            "src/test/testResources/simpleTests/transform1.xlsx",
            "src/test/testResources/simpleTests/10000-100-5-95-32row.xlsx",

    }, ignoreLeadingAndTrailingWhitespace = false)
    void complexTests(String inputFileName) {
        String outputFileName = "src/test/testResources/out.xlsx";
        String[] args = String
                .format("-inputFile %s -outputFile %s", inputFileName, outputFileName)
                .split("\\s+");
        Main.main(args);
        assert isEqualsTables(inputFileName, "out", outputFileName, null);
    }


    Comparator<Row> rowComparator = (r1, r2) -> {
        int len = Math.max(r1.getWidth(), r2.getWidth());
        for (int i = 0; i < len; i++) {
            Cell c1 = r1.getCell(i);
            Cell c2 = r2.getCell(i);
            if (c1.equals(c2)) continue;
            return c1.toString().compareTo(c2.toString());
        }
        return 0;
    };


    private boolean isEqualsTables(String file1, String sheet1, String file2, String sheet2) {
        //здесь мы точно знаем, что будем работать с excel, поэтому не шспользуем интерфейс
        ExcelTableLoader loader = new ExcelTableLoader();
        boolean res = true;
        try {
            loader.setSheetName(sheet1); // может быть null
            Table table1 = loader.loadTable(file1);
            loader.setSheetName(sheet2); // может быть null
            Table table2 = loader.loadTable(file2);
            assert table1.getHeight() == table2.getHeight();
            assert table1.getWidth() == table2.getWidth();
            sortTable(table1);
            sortTable(table2);
            for (int i = 0; i < table1.getHeight(); i++) {
                if (rowComparator.compare(table1.getRow(i), table2.getRow(i)) != 0 ) {
                    System.out.printf("rows %d is not equals.%n", i);
                    System.out.println(table1.getRow(i));
                    System.out.println(table2.getRow(i));
                    res = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return res;
    }

    private void sortTable(Table table) {
        List<Row> rows = new ArrayList<>();
        for (int i = 0; i < table.getHeight(); i++) {
            rows.add(table.getRow(i));
        }

        rows.sort(rowComparator);
        for (int i = 0; i < rows.size(); ++i) {
            table.setRow(i, rows.get(i));
        }
    }

}