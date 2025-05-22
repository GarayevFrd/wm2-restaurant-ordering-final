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
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WaiterServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private WaiterServiceImpl waiterService;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setStatus(OrderStatus.READY);
    }

    @Test
    void getReadyOrders_ReturnsOrdersWithReadyStatus() {
        // Arrange
        List<Order> readyOrders = List.of(testOrder);
        when(orderRepository.findByStatus(OrderStatus.READY)).thenReturn(readyOrders);

        // Act
        List<Order> result = waiterService.getReadyOrders();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(OrderStatus.READY, result.get(0).getStatus());
        verify(orderRepository, times(1)).findByStatus(OrderStatus.READY);
    }

    @Test
    void markOrderDelivered_ValidOrder_UpdatesStatus() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        waiterService.markOrderDelivered(1L);

        // Assert
        assertEquals(OrderStatus.DELIVERED, testOrder.getStatus());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    void markOrderDelivered_InvalidOrder_ThrowsException() {
        // Arrange
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            waiterService.markOrderDelivered(99L);
        });
        verify(orderRepository, times(1)).findById(99L);
        verify(orderRepository, never()).save(any(Order.class));
    }
}