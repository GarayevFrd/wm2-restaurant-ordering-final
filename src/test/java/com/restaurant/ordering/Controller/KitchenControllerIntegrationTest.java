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
public class KitchenControllerIntegrationTest {

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

        authToken = "Bearer " + jwtTokenProvider.createToken("kitchen", "ROLE_KITCHEN");
    }

    @Test
    void getIncomingOrders_ReturnsIncomingOrders() throws Exception {
        Order order = new Order();
        order.setTable(testTable);
        order.setStatus(OrderStatus.CREATED);
        order.setTotal(20.0);
        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setMenuItem(testMenuItem);
        item.setQuantity(2);
        items.add(item);
        order.setItems(items);
        orderRepository.save(order);

        mockMvc.perform(get("/api/kitchen/incoming")
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("CREATED")))
                .andExpect(jsonPath("$[0].totalAmount", is(20.0)));
    }

    @Test
    void getIncomingOrders_Empty_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/kitchen/incoming")
                .header("Authorization", authToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("No incoming orders found.")));
    }

    @Test
    void markInPreparation_ValidOrder_UpdatesStatus() throws Exception {
        Order order = new Order();
        order.setTable(testTable);
        order.setStatus(OrderStatus.CREATED);
        order.setTotal(20.0);
        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setMenuItem(testMenuItem);
        item.setQuantity(2);
        items.add(item);
        order.setItems(items);
        order = orderRepository.save(order);

        mockMvc.perform(put("/api/kitchen/" + order.getId() + "/prepare")
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Order marked as IN_PREPARATION"));
    }

    @Test
    void markInPreparation_InvalidOrder_ReturnsNotFound() throws Exception {
        mockMvc.perform(put("/api/kitchen/9999/prepare")
                .header("Authorization", authToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Order with ID 9999 not found.")));
    }

    @Test
    void markReady_ValidOrder_UpdatesStatus() throws Exception {
        Order order = new Order();
        order.setTable(testTable);
        order.setStatus(OrderStatus.IN_PREPARATION);
        order.setTotal(20.0);
        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setMenuItem(testMenuItem);
        item.setQuantity(2);
        items.add(item);
        order.setItems(items);
        order = orderRepository.save(order);

        mockMvc.perform(put("/api/kitchen/" + order.getId() + "/ready")
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Order marked as READY"));
    }

    @Test
    void markReady_InvalidOrder_ReturnsNotFound() throws Exception {
        mockMvc.perform(put("/api/kitchen/9999/ready")
                .header("Authorization", authToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Order with ID 9999 not found.")));
    }

    @Test
    void getOrdersInPreparation_ReturnsInPreparationOrders() throws Exception {
        Order order = new Order();
        order.setTable(testTable);
        order.setStatus(OrderStatus.IN_PREPARATION);
        order.setTotal(20.0);
        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setMenuItem(testMenuItem);
        item.setQuantity(2);
        items.add(item);
        order.setItems(items);
        orderRepository.save(order);

            mockMvc.perform(get("/api/kitchen/preparing")
                .header("Authorization", authToken))
                    .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("IN_PREPARATION")))
                .andExpect(jsonPath("$[0].totalAmount", is(20.0)));
        }

    @Test
    void getOrdersInPreparation_Empty_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/kitchen/preparing")
                .header("Authorization", authToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("No orders currently in preparation.")));
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

            mockMvc.perform(get("/api/kitchen/ready")
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("READY")))
                .andExpect(jsonPath("$[0].totalAmount", is(20.0)));
    }

    @Test
    void getReadyOrders_Empty_ReturnsNotFound() throws Exception {
            mockMvc.perform(get("/api/kitchen/ready")
                .header("Authorization", authToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("No orders are currently ready.")));
    }

    @Test
    void getIncomingOrders_WithoutAuth_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/kitchen/incoming"))
                .andExpect(status().isForbidden());
    }

    @Test
    void markInPreparation_WithoutAuth_ReturnsForbidden() throws Exception {
        mockMvc.perform(put("/api/kitchen/1/prepare"))
                .andExpect(status().isForbidden());
    }

    @Test
    void markReady_WithoutAuth_ReturnsForbidden() throws Exception {
        mockMvc.perform(put("/api/kitchen/1/ready"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getOrdersInPreparation_WithoutAuth_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/kitchen/preparing"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getReadyOrders_WithoutAuth_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/kitchen/ready"))
                .andExpect(status().isForbidden());
        }
} 