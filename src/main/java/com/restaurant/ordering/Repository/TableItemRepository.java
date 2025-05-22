package com.restaurant.ordering.Repository;

import com.restaurant.ordering.Model.TableItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableItemRepository extends JpaRepository<TableItem, Long> {

    Optional<TableItem> findById(Long id);

    Optional<TableItem> findByTableId(Long tableId);

    List<TableItem> findAll();
}