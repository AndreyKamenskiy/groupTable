package org.example;

import org.example.table.Cell;
import org.example.table.Row;
import org.example.table.Table;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    Comparator<Row> rowComparator = new Comparator<Row>() {
        @Override
        public int compare(Row r1, Row r2) {
            int len = Math.max(r1.getWidth(), r2.getWidth());
            for (int i = 0; i < len; i++) {
                Cell c1 = r1.getCell(i);
                Cell c2 = r2.getCell(i);
                if (c1.equals(c2)) continue;
                return c1.toString().compareTo(c2.toString());
            }
            return 0;
        }
    };


    @Test
    void simpleTest() {
        String[] args = new String[]{
                "-inputFile",
                "src/test/testResources/example1.xlsx",
                "-outputFile",
                "src/test/testResources/out.xlsx"
        };

        String testerName = "src/test/testResources/answer1.xlsx";

        Main.main(args);
        assert isEqualsTables(testerName, args[3]);
    }

    private boolean isEqualsTables(String file1, String file2) {
        TableLoader loader = new ExcelTableLoader();
        boolean res = true;
        try {
            Table table1 = loader.loadTable(file1);
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

        Collections.sort(rows, rowComparator);
        for (int i = 0; i < rows.size(); ++i) {
            table.setRow(i, rows.get(i));
        }
    }


}