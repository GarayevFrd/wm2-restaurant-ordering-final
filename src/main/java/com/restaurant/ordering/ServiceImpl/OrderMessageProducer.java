package com.restaurant.ordering.ServiceImpl;

import com.restaurant.ordering.Config.RabbitMQConfig;
import com.restaurant.ordering.DTO.OrderDTO;
import com.restaurant.ordering.Model.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
public class OrderMessageProducer {
    
    private final RabbitTemplate rabbitTemplate;
    
    public OrderMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    public void sendOrder(Order order) {
        OrderDTO message = convertToDTO(order);
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.ORDER_EXCHANGE,
            RabbitMQConfig.ORDER_ROUTING_KEY,
            message
        );
    }
    
    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setTableId(order.getTable().getTableId());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setTotalAmount(order.getTotal());
        
        dto.setItems(order.getItems().stream()
            .map(item -> {
                OrderDTO.OrderItemDTO itemDTO = new OrderDTO.OrderItemDTO();
                itemDTO.setMenuItemId(item.getMenuItem().getId());
                itemDTO.setItemName(item.getMenuItem().getName());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setPrice(item.getMenuItem().getPrice());
                return itemDTO;
            })
            .collect(Collectors.toList()));
            
        return dto;
    }
} 