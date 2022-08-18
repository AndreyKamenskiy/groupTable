package org.example;

import org.example.table.Table;

import java.io.IOException;

public interface TableLoader {
    Table loadTable(String fileName) throws IOException;
}
