package com.restaurant.ordering.ServiceImpl;

import com.restaurant.ordering.Model.TableItem;
import com.restaurant.ordering.Repository.TableItemRepository;
import com.restaurant.ordering.Service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableServiceImpl implements TableService {

    private final TableItemRepository tableRepository;

    @Autowired
    public TableServiceImpl(TableItemRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @Override
    public List<TableItem> getAllTables() {
        return tableRepository.findAll();
    }

    @Override
    public TableItem getTableById(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found with ID: " + id));
    }

    @Override
    public TableItem getTableByTableId(Long tableId) {
        return tableRepository.findByTableId(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found with tableId: " + tableId));
    }

    @Override
    public TableItem addTable(TableItem tableItem) {
        return tableRepository.save(tableItem);
    }

    @Override
    public void deleteTable(Long id) {
        tableRepository.deleteById(id);
    }

    @Override
    public TableItem updateTable(Long id, TableItem updatedTable) {
        TableItem existingTable = getTableById(id);
        existingTable.setTableId(updatedTable.getTableId());
        existingTable.setCustomer(updatedTable.getCustomer()); // optional
        return tableRepository.save(existingTable);
    }
}
