package com.restaurant.ordering.Service;

import com.restaurant.ordering.Model.MenuItem;
import java.util.List;

public interface MenuService {
    List<MenuItem> getAllMenuItems();
    MenuItem addMenuItem(MenuItem item);
    MenuItem updateMenuItem(Long id, MenuItem item);
    void deleteMenuItem(Long id);
}