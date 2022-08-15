package org.example;

import org.example.table.Table;

import java.io.IOException;

public interface TableSaver {
    void saveTable(String fileName, Table table) throws IOException;
}
