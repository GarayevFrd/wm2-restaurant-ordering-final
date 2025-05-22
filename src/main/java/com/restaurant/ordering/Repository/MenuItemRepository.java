package com.restaurant.ordering.Repository;

import com.restaurant.ordering.Model.MenuItem;
import com.restaurant.ordering.Enums.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {


    Optional<MenuItem> findById(Long id);

    List<MenuItem> findByCategory(MenuCategory category);

    List<MenuItem> findByNameContainingIgnoreCase(String name);

    List<MenuItem> findAllByOrderByPriceAsc();

    List<MenuItem> findAllByOrderByPriceDesc();
}