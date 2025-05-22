package com.restaurant.ordering.ServiceImpl;

import com.restaurant.ordering.DTO.CreateOrderDTO;
import com.restaurant.ordering.DTO.OrderDTO;
import com.restaurant.ordering.Enums.MenuCategory;
import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Model.MenuItem;
import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Model.OrderItem;
import com.restaurant.ordering.Model.TableItem;
import com.restaurant.ordering.Repository.OrderRepository;
import com.restaurant.ordering.Repository.TableItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private TableItemRepository tableItemRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private TableItem testTable;
    private List<OrderItem> testItems;
    private CreateOrderDTO createOrderDTO;
    private MenuItem testMenuItem;

    @BeforeEach
    void setUp() {
        // Set up test table
        testTable = new TableItem();
        testTable.setId(1L);
        testTable.setTableId(101L);

        // Set up test menu item
        testMenuItem = new MenuItem();
        testMenuItem.setId(1L);
        testMenuItem.setName("Test Item");
        testMenuItem.setPrice(10.0);
        testMenuItem.setCategory(MenuCategory.MAIN_COURSE);

        // Set up test order items
        testItems = new ArrayList<>();
        OrderItem item1 = new OrderItem();
        item1.setId(1L);
        item1.setQuantity(2);
        item1.setMenuItem(testMenuItem);
        testItems.add(item1);

        // Set up test order
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setTable(testTable);
        testOrder.setItems(testItems);
        testOrder.setStatus(OrderStatus.CREATED);
        testOrder.setTotal(20.0);

        // Set up test order item requests
        createOrderDTO = new CreateOrderDTO();
        createOrderDTO.setTableId(101L);
        List<CreateOrderDTO.OrderItemRequest> orderItemRequests = new ArrayList<>();
        CreateOrderDTO.OrderItemRequest request = new CreateOrderDTO.OrderItemRequest();
        request.setMenuItemId(1L);
        request.setQuantity(2);
        orderItemRequests.add(request);
        createOrderDTO.setItems(orderItemRequests);
    }

    @Test
    void createOrder_Success() {
        // Arrange
        when(tableItemRepository.findByTableId(101L)).thenReturn(Optional.of(testTable));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        OrderDTO result = orderService.createOrder(createOrderDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(OrderStatus.CREATED, result.getStatus());
        assertEquals(20.0, result.getTotalAmount());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_TableNotFound_ThrowsException() {
        // Arrange
        when(tableItemRepository.findByTableId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(createOrderDTO);
        });
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrder_ValidId_ReturnsOrder() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        OrderDTO result = orderService.getOrder(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(OrderStatus.CREATED, result.getStatus());
        assertEquals(20.0, result.getTotalAmount());
    }

    @Test
    void getOrder_InvalidId_ThrowsException() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            orderService.getOrder(999L);
        });
    }

    @Test
    void updateOrderStatus_ValidId_UpdatesStatus() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        OrderDTO result = orderService.updateOrderStatus(1L, OrderStatus.IN_PREPARATION);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.IN_PREPARATION, result.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_InvalidId_ThrowsException() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            orderService.updateOrderStatus(999L, OrderStatus.IN_PREPARATION);
        });
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getAllOrders_ReturnsAllOrders() {
        // Arrange
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findAll()).thenReturn(orders);

        // Act
        List<OrderDTO> result = orderService.getAllOrders();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getOrdersByTableId_ReturnsOrdersForTable() {
        // Arrange
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findByTableId(1L)).thenReturn(orders);

        // Act
        List<OrderDTO> result = orderService.getOrdersByTableId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(101L, result.get(0).getTableId());
        verify(orderRepository, times(1)).findByTableId(1L);
    }

    @Test
    void updateOrderItems_ExistingOrder_UpdatesItems() {
        // Arrange
        CreateOrderDTO updatedOrder = new CreateOrderDTO();
        updatedOrder.setTableId(101L);
        updatedOrder.setItems(new ArrayList<>());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        OrderDTO result = orderService.updateOrderItems(1L, updatedOrder);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    void updateOrderItems_NonExistingOrder_ThrowsException() {
        // Arrange
        CreateOrderDTO updatedOrder = new CreateOrderDTO();
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateOrderItems(99L, updatedOrder);
        });
        assertEquals("Order not found", exception.getMessage());
        verify(orderRepository, times(1)).findById(99L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void removeItemFromOrder_ExistingOrderAndItem_RemovesItem() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        OrderDTO result = orderService.removeItemFromOrder(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    void removeItemFromOrder_NonExistingOrder_ThrowsException() {
        // Arrange
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.removeItemFromOrder(99L, 1L);
        });
        assertEquals("Order not found", exception.getMessage());
        verify(orderRepository, times(1)).findById(99L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrderStatus_ExistingOrder_ReturnsStatus() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        OrderStatus result = orderService.getOrderStatus(1L);

        // Assert
        assertEquals(OrderStatus.CREATED, result);
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderStatus_NonExistingOrder_ThrowsException() {
        // Arrange
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderStatus(99L);
        });
        assertEquals("Order not found", exception.getMessage());
        verify(orderRepository, times(1)).findById(99L);
    }

    @Test
    void getOrdersByStatus_ReturnsOrdersWithSpecifiedStatus() {
        // Arrange
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findByStatus(OrderStatus.CREATED)).thenReturn(orders);

        // Act
        List<OrderDTO> result = orderService.getOrdersByStatus(OrderStatus.CREATED);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(OrderStatus.CREATED, result.get(0).getStatus());
        verify(orderRepository, times(1)).findByStatus(OrderStatus.CREATED);
    }
}
