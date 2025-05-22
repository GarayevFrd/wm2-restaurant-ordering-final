package com.restaurant.ordering.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TableControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    void getAllTables_ReturnsTables() throws Exception {
        mockMvc.perform(get("/tables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tableId", is(101)));
    }

    @Test
    void getAllTables_Empty_ReturnsNotFound() throws Exception {
        tableItemRepository.deleteAll();
        mockMvc.perform(get("/tables"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getTableById_ValidId_ReturnsTable() throws Exception {
        mockMvc.perform(get("/tables/" + testTable.getTableId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tableId", is(101)));
    }

    @Test
    void getTableById_InvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/tables/9999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void addTable_ValidTable_ReturnsCreatedTable() throws Exception {
        TableItem newTable = new TableItem();
        newTable.setTableId(102L);

        mockMvc.perform(post("/tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTable)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tableId", is(102)));
    }

    @Test
    void deleteTable_ValidId_ReturnsOk() throws Exception {
        mockMvc.perform(delete("/tables/" + testTable.getTableId()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTable_InvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/tables/9999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateTable_ValidId_ReturnsUpdatedTable() throws Exception {
        testTable.setTableId(103L);

        mockMvc.perform(put("/tables/" + testTable.getTableId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTable)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tableId", is(103)));
    }

    @Test
    void updateTable_InvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(put("/tables/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTable)))
                .andExpect(status().isInternalServerError());
    }
} 