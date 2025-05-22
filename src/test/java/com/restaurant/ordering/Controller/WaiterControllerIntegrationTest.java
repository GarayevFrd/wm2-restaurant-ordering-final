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
public class WaiterControllerIntegrationTest {

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
    void getReadyOrders_ReturnsReadyOrders() throws Exception {
        Order order = new Order();
        order.setTable(testTable);
        order.setStatus(OrderStatus.READY);
        order.setTotal(20.0);
        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setMenuItem(testMenuItem);
        item.setQuantity(2);
        items.add(item);
        order.setItems(items);
        orderRepository.save(order);

            mockMvc.perform(get("/api/waiter/ready-orders")
                .header("Authorization", authToken))
                    .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("READY")))
                .andExpect(jsonPath("$[0].total", is(20.0)));
        }

    @Test
    void getReadyOrders_Empty_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/waiter/ready-orders")
                .header("Authorization", authToken))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void markOrderDelivered_ValidOrder_UpdatesStatus() throws Exception {
        Order order = new Order();
        order.setTable(testTable);
        order.setStatus(OrderStatus.READY);
        order.setTotal(20.0);
        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setMenuItem(testMenuItem);
        item.setQuantity(2);
        items.add(item);
        order.setItems(items);
        order = orderRepository.save(order);

        mockMvc.perform(put("/api/waiter/" + order.getId() + "/deliver")
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Order marked as DELIVERED"));
    }

    @Test
    void markOrderDelivered_InvalidOrder_ReturnsNotFound() throws Exception {
        mockMvc.perform(put("/api/waiter/9999/deliver")
                .header("Authorization", authToken))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getReadyOrders_WithoutAuth_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/waiter/ready-orders"))
                .andExpect(status().isForbidden());
    }

    @Test
    void markOrderDelivered_WithoutAuth_ReturnsForbidden() throws Exception {
        mockMvc.perform(put("/api/waiter/1/deliver"))
                .andExpect(status().isForbidden());
    }
} 