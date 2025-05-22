package com.restaurant.ordering.Controller;

import com.restaurant.ordering.Model.TableItem;
import com.restaurant.ordering.Service.QRCodeService;
import com.restaurant.ordering.Service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/tables")
public class TableController {

    @Autowired
    private TableService tableService;

    @Autowired
    private QRCodeService qrCodeService;


    @GetMapping
    public List<TableItem> getAllTables() {
        List<TableItem> tables = tableService.getAllTables();
        if (tables.isEmpty()) {
            throw new NoSuchElementException("No tables found.");
        }
        return tables;
    }


    @GetMapping("/{id}")
    public TableItem getTableById(@PathVariable Long id) {
        try {
            return tableService.getTableById(id);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Table with ID " + id + " not found.");
        }
    }


    @PostMapping
    public TableItem addTable(@RequestBody TableItem tableItem) {
        return tableService.addTable(tableItem);
    }


    @DeleteMapping("/{id}")
    public void deleteTable(@PathVariable Long id) {
        try {
            tableService.deleteTable(id);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Table with ID " + id + " not found.");
        }
    }


    @PutMapping("/{id}")
    public TableItem updateTable(@PathVariable Long id, @RequestBody TableItem tableItem) {
        try {
            return tableService.updateTable(id, tableItem);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Table with ID " + id + " not found.");
        }
    }

}
