package com.restaurant.ordering.Service;

import com.restaurant.ordering.DTO.CreateOrderDTO;
import com.restaurant.ordering.DTO.OrderDTO;
import com.restaurant.ordering.Enums.OrderStatus;
import java.util.List;

public interface OrderService {


    OrderDTO createOrder(CreateOrderDTO orderDTO);
    OrderDTO updateOrderStatus(Long orderId, OrderStatus status);
    OrderDTO updateOrderItems(Long orderId, CreateOrderDTO updatedOrder);
    OrderDTO removeItemFromOrder(Long orderId, Long itemId);
    


    OrderDTO getOrder(Long orderId);
    OrderDTO getOrderByTable(Long tableId);
    OrderStatus getOrderStatus(Long orderId);
    


    List<OrderDTO> getOrdersByStatus(OrderStatus status);
    List<OrderDTO> getOrdersByTableId(Long tableId);
    List<OrderDTO> getAllOrders();
}