package com.restaurant.ordering.Controller;

import com.restaurant.ordering.Model.TableItem;
import com.restaurant.ordering.Repository.TableItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class QRCodeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TableItemRepository tableItemRepository;

    private TableItem testTable;

    @BeforeEach
    void setUp() {
        tableItemRepository.deleteAll();

        testTable = new TableItem();
        testTable.setTableId(101L);
        testTable = tableItemRepository.save(testTable);
    }

    @Test
    void generateQRCode_ValidTable_ReturnsQRCode() throws Exception {
        mockMvc.perform(get("/api/qr/generate/" + testTable.getTableId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE));
    }

    @Test
    void generateQRCode_InvalidTable_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/qr/generate/9999"))
                .andExpect(status().isNotFound());
    }
} 