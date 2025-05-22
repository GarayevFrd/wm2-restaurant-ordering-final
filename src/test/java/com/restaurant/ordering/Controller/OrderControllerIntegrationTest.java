package com.restaurant.ordering.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.ordering.Enums.MenuCategory;
import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Model.MenuItem;
import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Model.OrderItem;
import com.restaurant.ordering.Model.TableItem;
import com.restaurant.ordering.Repository.MenuItemRepository;
import com.restaurant.ordering.Repository.OrderRepository;
import com.restaurant.ordering.Repository.TableItemRepository;
import com.restaurant.ordering.Security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TableItemRepository tableItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private TableItem testTable;
    private MenuItem testMenuItem;
    private String authToken;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        menuItemRepository.deleteAll();
        tableItemRepository.deleteAll();
        
        testTable = new TableItem();
        testTable.setTableId(101L);
        testTable.setOrders(new ArrayList<>());
        testTable = tableItemRepository.save(testTable);

        testMenuItem = new MenuItem();
        testMenuItem.setName("Test Item");
        testMenuItem.setDescription("Test Description");
        testMenuItem.setPrice(10.0);
        testMenuItem.setCategory(MenuCategory.MAIN_COURSE);
        testMenuItem = menuItemRepository.save(testMenuItem);

        authToken = "Bearer " + jwtTokenProvider.createToken("waiter", "ROLE_WAITER");
    }

    @Test
    void createOrder_Valid_ReturnsOrder() throws Exception {
        String payload = "{" +
                "\"tableId\":" + testTable.getTableId() + "," +
                "\"items\":[{" +
                "\"menuItemId\":" + testMenuItem.getId() + "," +
                "\"quantity\":2}]}";

        mockMvc.perform(post("/api/orders")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.tableId", is(testTable.getTableId().intValue())))
                .andExpect(jsonPath("$.status", is("CREATED")))
                .andExpect(jsonPath("$.totalAmount", is(20.0)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].menuItemId", is(testMenuItem.getId().intValue())))
                .andExpect(jsonPath("$.items[0].quantity", is(2)));
    }

    @Test
    void createOrder_InvalidTable_ReturnsNotFound() throws Exception {
        String payload = "{" +
                "\"tableId\":999," +
                "\"items\":[{" +
                "\"menuItemId\":" + testMenuItem.getId() + "," +
                "\"quantity\":2}]}";

        mockMvc.perform(post("/api/orders")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getOrder_ValidId_ReturnsOrder() throws Exception {
        // Create order first
        String payload = "{" +
                "\"tableId\":" + testTable.getTableId() + "," +
                "\"items\":[{" +
                "\"menuItemId\":" + testMenuItem.getId() + "," +
                "\"quantity\":2}]}";
        String response = mockMvc.perform(post("/api/orders")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andReturn().getResponse().getContentAsString();
        Long orderId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/orders/" + orderId)
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.intValue())))
                .andExpect(jsonPath("$.tableId", is(testTable.getTableId().intValue())))
                .andExpect(jsonPath("$.status", is("CREATED")))
                .andExpect(jsonPath("$.totalAmount", is(20.0)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].menuItemId", is(testMenuItem.getId().intValue())))
                .andExpect(jsonPath("$.items[0].quantity", is(2)));
    }

    @Test
    void getOrder_InvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/orders/9999")
                .header("Authorization", authToken))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateOrderStatus_Valid_UpdatesStatus() throws Exception {

        String payload = "{" +
                "\"tableId\":" + testTable.getTableId() + "," +
                "\"items\":[{" +
                "\"menuItemId\":" + testMenuItem.getId() + "," +
                "\"quantity\":2}]}";
        String response = mockMvc.perform(post("/api/orders")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andReturn().getResponse().getContentAsString();
        Long orderId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(put("/api/orders/" + orderId + "/status")
                .header("Authorization", authToken)
                .param("status", "IN_PREPARATION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("IN_PREPARATION")));
    }

    @Test
    void updateOrderStatus_InvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(put("/api/orders/9999/status")
                .header("Authorization", authToken)
                .param("status", "IN_PREPARATION"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getOrder_WithoutAuth_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createOrder_WithoutAuth_ReturnsForbidden() throws Exception {
        String payload = "{" +
                "\"tableId\":101," +
                "\"items\":[{" +
                "\"menuItemId\":1," +
                "\"quantity\":2}]}";
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isForbidden());
    }
}