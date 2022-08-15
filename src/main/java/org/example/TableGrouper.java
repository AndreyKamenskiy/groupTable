package org.example;

import org.example.table.Table;

public interface TableGrouper {
    Table groupTable(Table inTable) throws IllegalArgumentException;
}
