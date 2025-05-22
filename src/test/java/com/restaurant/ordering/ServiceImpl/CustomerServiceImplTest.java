package com.restaurant.ordering.ServiceImpl;

import com.restaurant.ordering.DTO.OrderItemRequestDTO;
import com.restaurant.ordering.Enums.MenuCategory;
import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Model.MenuItem;
import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Model.OrderItem;
import com.restaurant.ordering.Model.TableItem;
import com.restaurant.ordering.Model.Users.Customer;
import com.restaurant.ordering.Repository.MenuItemRepository;
import com.restaurant.ordering.Repository.OrderRepository;
import com.restaurant.ordering.Repository.TableItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {

    @Mock
    private TableItemRepository tableItemRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private TableItem testTable;
    private MenuItem testMenuItem;
    private Order testOrder;
    private List<OrderItem> testOrderItems;
    private List<OrderItemRequestDTO> testOrderItemRequests;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        // Set up test customer
        testCustomer = new Customer();
        testCustomer.setId(1L);

        // Set up test table
        testTable = new TableItem();
        testTable.setId(1L);
        testTable.setTableId(101L);
        testTable.setCustomer(testCustomer);
        testCustomer.setTable(testTable);

        // Set up test menu item
        testMenuItem = new MenuItem();
        testMenuItem.setId(1L);
        testMenuItem.setName("Test Item");
        testMenuItem.setPrice(10.0);
        testMenuItem.setCategory(MenuCategory.MAIN_COURSE);

        // Set up test order items
        testOrderItems = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setId(1L);
        item.setMenuItem(testMenuItem);
        item.setQuantity(2);
        testOrderItems.add(item);

        // Set up test order
        testOrder = Order.builder()
                .id(1L)
                .customer(testCustomer)
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .items(testOrderItems)
                .total(20.0)
                .build();

        // Set up test order item requests
        testOrderItemRequests = new ArrayList<>();
        OrderItemRequestDTO requestDTO = new OrderItemRequestDTO();
        requestDTO.setMenuItemId(1L);
        requestDTO.setQuantity(2);
        testOrderItemRequests.add(requestDTO);

        // Link order to table
        List<Order> orders = new ArrayList<>();
        orders.add(testOrder);
        testTable.setOrders(orders);

        // Link order item to order
        for (OrderItem item1 : testOrderItems) {
            item1.setOrder(testOrder);
        }
    }

    @Test
    void getMenu_ReturnsAllMenuItems() {
        // Arrange
        List<MenuItem> menuItems = List.of(testMenuItem);
        when(menuItemRepository.findAll()).thenReturn(menuItems);

        // Act
        List<MenuItem> result = customerService.getMenu();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Item", result.get(0).getName());
        verify(menuItemRepository, times(1)).findAll();
    }

    @Test
    void createOrder_ValidTableAndItems_ReturnsCreatedOrder() {
        // Arrange
        when(tableItemRepository.findByTableId(101L)).thenReturn(Optional.of(testTable));
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(testMenuItem));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = customerService.createOrder(101L, testOrderItemRequests);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.CREATED, result.getStatus());
        assertEquals(testCustomer, result.getCustomer());
        verify(tableItemRepository, times(1)).findByTableId(101L);
        verify(menuItemRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_InvalidTable_ThrowsException() {
        // Arrange
        when(tableItemRepository.findByTableId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerService.createOrder(999L, testOrderItemRequests);
        });
        assertEquals("Table not found", exception.getMessage());
        verify(tableItemRepository, times(1)).findByTableId(999L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void updateOrder_ValidOrderAndItems_ReturnsUpdatedOrder() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(testMenuItem));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = customerService.updateOrder(1L, testOrderItemRequests);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRepository, times(2)).findById(1L);
        verify(menuItemRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void updateOrder_InvalidOrder_ThrowsException() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerService.updateOrder(999L, testOrderItemRequests);
        });
        assertEquals("Order not found", exception.getMessage());
        verify(orderRepository, times(1)).findById(999L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void calculateTotal_ValidOrder_ReturnsCorrectTotal() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        double result = customerService.calculateTotal(1L);

        // Assert
        assertEquals(20.0, result);
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void calculateTotal_InvalidOrder_ThrowsException() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerService.calculateTotal(999L);
        });
        assertEquals("Order not found", exception.getMessage());
        verify(orderRepository, times(1)).findById(999L);
    }

    @Test
    void getOrderStatus_ValidOrder_ReturnsCorrectStatus() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        OrderStatus result = customerService.getOrderStatus(1L);

        // Assert
        assertEquals(OrderStatus.CREATED, result);
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderStatus_InvalidOrder_ThrowsException() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerService.getOrderStatus(999L);
        });
        assertEquals("Order not found", exception.getMessage());
        verify(orderRepository, times(1)).findById(999L);
    }
}
