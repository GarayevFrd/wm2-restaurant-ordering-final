package com.restaurant.ordering.Service;

import com.restaurant.ordering.Model.TableItem;

import java.util.List;

public interface TableService {
    List<TableItem> getAllTables();
    TableItem getTableById(Long id);
    TableItem getTableByTableId(Long tableId);
    TableItem addTable(TableItem tableItem);
    void deleteTable(Long id);
    TableItem updateTable(Long id, TableItem updatedTable);
}
