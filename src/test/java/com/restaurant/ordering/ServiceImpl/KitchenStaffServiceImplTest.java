package com.restaurant.ordering.ServiceImpl;

import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KitchenStaffServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private KitchenStaffServiceImpl kitchenStaffService;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setStatus(OrderStatus.CREATED);
    }

    @Test
    void getIncomingOrders_ReturnsOrdersWithCreatedStatus() {
        // Arrange
        List<Order> createdOrders = List.of(testOrder);
        when(orderRepository.findByStatus(OrderStatus.CREATED)).thenReturn(createdOrders);

        // Act
        List<Order> result = kitchenStaffService.getIncomingOrders();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(OrderStatus.CREATED, result.get(0).getStatus());
        verify(orderRepository, times(1)).findByStatus(OrderStatus.CREATED);
    }

    @Test
    void markOrderInPreparation_ValidOrder_UpdatesStatus() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        kitchenStaffService.markOrderInPreparation(1L);

        // Assert
        assertEquals(OrderStatus.IN_PREPARATION, testOrder.getStatus());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    void markOrderInPreparation_InvalidOrder_ThrowsException() {
        // Arrange
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            kitchenStaffService.markOrderInPreparation(99L);
        });
        assertEquals("Order not found", exception.getMessage());
        verify(orderRepository, times(1)).findById(99L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void markOrderReady_ValidOrder_UpdatesStatus() {
        // Arrange
        testOrder.setStatus(OrderStatus.IN_PREPARATION);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        kitchenStaffService.markOrderReady(1L);

        // Assert
        assertEquals(OrderStatus.READY, testOrder.getStatus());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    void markOrderReady_InvalidOrder_ThrowsException() {
        // Arrange
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            kitchenStaffService.markOrderReady(99L);
        });
        assertEquals("Order not found", exception.getMessage());
        verify(orderRepository, times(1)).findById(99L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrdersInPreparation_ReturnsOrdersWithInPreparationStatus() {
        // Arrange
        testOrder.setStatus(OrderStatus.IN_PREPARATION);
        List<Order> inPreparationOrders = List.of(testOrder);
        when(orderRepository.findByStatus(OrderStatus.IN_PREPARATION)).thenReturn(inPreparationOrders);

        // Act
        List<Order> result = kitchenStaffService.getOrdersInPreparation();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(OrderStatus.IN_PREPARATION, result.get(0).getStatus());
        verify(orderRepository, times(1)).findByStatus(OrderStatus.IN_PREPARATION);
    }

    @Test
    void getReadyOrders_ReturnsOrdersWithReadyStatus() {
        // Arrange
        testOrder.setStatus(OrderStatus.READY);
        List<Order> readyOrders = List.of(testOrder);
        when(orderRepository.findByStatus(OrderStatus.READY)).thenReturn(readyOrders);

        // Act
        List<Order> result = kitchenStaffService.getReadyOrders();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(OrderStatus.READY, result.get(0).getStatus());
        verify(orderRepository, times(1)).findByStatus(OrderStatus.READY);
    }
}