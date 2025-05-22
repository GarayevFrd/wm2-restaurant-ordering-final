package com.restaurant.ordering.ServiceImpl;

import com.restaurant.ordering.DTO.CreateOrderDTO;
import com.restaurant.ordering.DTO.OrderDTO;
import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Model.*;
import com.restaurant.ordering.Repository.*;
import com.restaurant.ordering.Service.OrderService;
import com.restaurant.ordering.Service.RedisOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final TableItemRepository tableItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMessageProducer orderMessageProducer;
    private final RedisOrderService redisOrderService;
    
    public OrderServiceImpl(
            OrderRepository orderRepository,
            TableItemRepository tableItemRepository,
            MenuItemRepository menuItemRepository,
            OrderItemRepository orderItemRepository,
            OrderMessageProducer orderMessageProducer,
            RedisOrderService redisOrderService) {
        this.orderRepository = orderRepository;
        this.tableItemRepository = tableItemRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderMessageProducer = orderMessageProducer;
        this.redisOrderService = redisOrderService;
    }
    
    @Override
    @Transactional
    public OrderDTO createOrder(CreateOrderDTO orderDTO) {
        TableItem table = tableItemRepository.findByTableId(orderDTO.getTableId())
            .orElseThrow(() -> new RuntimeException("Table not found"));
            
        Order order = new Order();
        order.setTable(table);
        order.setStatus(OrderStatus.CREATED);
        
        Order savedOrder = orderRepository.save(order);
        
        List<OrderItem> orderItems = orderDTO.getItems().stream()
            .map(itemDTO -> {
                MenuItem menuItem = menuItemRepository.findById(itemDTO.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("MenuItem not found"));
                    
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(savedOrder);
                orderItem.setMenuItem(menuItem);
                orderItem.setQuantity(itemDTO.getQuantity());
                return orderItem;
            })
            .collect(Collectors.toList());
            
        orderItemRepository.saveAll(orderItems);
        savedOrder.setItems(orderItems);
        

        double total = orderItems.stream()
            .mapToDouble(item -> item.getMenuItem().getPrice() * item.getQuantity())
            .sum();
        savedOrder.setTotal(total);
        orderRepository.save(savedOrder);

        redisOrderService.saveOrderStatus(savedOrder.getId(), savedOrder.getStatus());
        redisOrderService.saveOrderSession(table.getId(), savedOrder.getId());

        orderMessageProducer.sendOrder(savedOrder);
        
        return convertToDTO(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
            
        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);

        redisOrderService.saveOrderStatus(savedOrder.getId(), savedOrder.getStatus());

        orderMessageProducer.sendOrder(savedOrder);
        
        return convertToDTO(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderDTO updateOrderItems(Long orderId, CreateOrderDTO updatedOrder) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
            

        orderItemRepository.deleteAll(order.getItems());
        

        List<OrderItem> orderItems = updatedOrder.getItems().stream()
            .map(itemDTO -> {
                MenuItem menuItem = menuItemRepository.findById(itemDTO.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("MenuItem not found"));
                    
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setMenuItem(menuItem);
                orderItem.setQuantity(itemDTO.getQuantity());
                return orderItem;
            })
            .collect(Collectors.toList());
            
        orderItemRepository.saveAll(orderItems);
        order.setItems(orderItems);
        
        Order savedOrder = orderRepository.save(order);

        redisOrderService.saveOrderStatus(savedOrder.getId(), savedOrder.getStatus());

        orderMessageProducer.sendOrder(savedOrder);
        
        return convertToDTO(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderDTO removeItemFromOrder(Long orderId, Long itemId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
            
        OrderItem itemToRemove = order.getItems().stream()
            .filter(item -> item.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Order item not found"));
            
        order.getItems().remove(itemToRemove);
        orderItemRepository.delete(itemToRemove);
        
        Order savedOrder = orderRepository.save(order);
        

        redisOrderService.saveOrderStatus(savedOrder.getId(), savedOrder.getStatus());
        

        orderMessageProducer.sendOrder(savedOrder);
        
        return convertToDTO(savedOrder);
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToDTO(order);
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderByTable(Long tableId) {
        List<Order> orders = orderRepository.findByTableId(tableId);
        Order activeOrder = orders.stream()
            .filter(order -> order.getStatus() != OrderStatus.DELIVERED)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No active order found for table"));
        return convertToDTO(activeOrder);
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrderStatus getOrderStatus(Long orderId) {

        OrderStatus cachedStatus = redisOrderService.getOrderStatus(orderId);
        if (cachedStatus != null) {
            return cachedStatus;
        }
        

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
            
        redisOrderService.saveOrderStatus(orderId, order.getStatus());
        return order.getStatus();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
            .<OrderDTO>map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByTableId(Long tableId) {
        return orderRepository.findByTableId(tableId).stream()
            .<OrderDTO>map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
            .<OrderDTO>map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setTableId(order.getTable().getTableId());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        

        double total = order.getItems().stream()
            .mapToDouble(item -> item.getMenuItem().getPrice() * item.getQuantity())
            .sum();
        dto.setTotalAmount(total);
        
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