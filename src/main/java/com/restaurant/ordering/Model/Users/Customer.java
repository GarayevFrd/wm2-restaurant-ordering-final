package com.restaurant.ordering.Model.Users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restaurant.ordering.Model.Order;
import com.restaurant.ordering.Model.TableItem;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

import java.util.*;


@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to the Table that the Customer is associated with
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", referencedColumnName = "id")
    private TableItem table;  // This will map to the 'Table' entity

    @JsonIgnore
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();
}
