package com.restaurant.ordering.ServiceImpl;

import com.restaurant.ordering.Model.MenuItem;
import com.restaurant.ordering.Repository.MenuItemRepository;
import com.restaurant.ordering.Service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Override
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    @Override
    public MenuItem addMenuItem(MenuItem item) {
        return menuItemRepository.save(item);
    }

    @Override
    public MenuItem updateMenuItem(Long id, MenuItem item) {
        if (!menuItemRepository.existsById(id)) {
            throw new NoSuchElementException("Menu item with id " + id + " not found");
        }
        item.setId(id);
        return menuItemRepository.save(item);
    }

    @Override
    public void deleteMenuItem(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new NoSuchElementException("Menu item with id " + id + " not found");
        }
        menuItemRepository.deleteById(id);
    }
}