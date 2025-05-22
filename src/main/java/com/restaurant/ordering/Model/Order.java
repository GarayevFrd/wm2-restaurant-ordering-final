package com.restaurant.ordering.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restaurant.ordering.Enums.OrderStatus;
import com.restaurant.ordering.Model.Users.Customer;
import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to customer (who made the order)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // Status of the order (NEW, IN_PREPARATION, etc.)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // Timestamp for when order was created
    private LocalDateTime createdAt;

    // Total price
    private double total;

    // Items in the order
    @JsonIgnore
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    private TableItem table;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        status = OrderStatus.CREATED;
    }
}
