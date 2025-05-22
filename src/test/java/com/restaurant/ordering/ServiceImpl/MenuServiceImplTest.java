package com.restaurant.ordering.ServiceImpl;

import com.restaurant.ordering.Enums.MenuCategory;
import com.restaurant.ordering.Model.MenuItem;
import com.restaurant.ordering.Repository.MenuItemRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuServiceImplTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private MenuServiceImpl menuService;

    private MenuItem testMenuItem;

    @BeforeEach
    void setUp() {
        testMenuItem = new MenuItem();
        testMenuItem.setId(1L);
        testMenuItem.setName("Test Item");
        testMenuItem.setDescription("Test Description");
        testMenuItem.setPrice(10.0);
        testMenuItem.setCategory(MenuCategory.MAIN_COURSE);
    }

    @Test
    void getAllMenuItems_ReturnsAllItems() {
        // Arrange
        List<MenuItem> menuItems = List.of(testMenuItem);
        when(menuItemRepository.findAll()).thenReturn(menuItems);

        // Act
        List<MenuItem> result = menuService.getAllMenuItems();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Item", result.get(0).getName());
        verify(menuItemRepository, times(1)).findAll();
    }

    @Test
    void addMenuItem_ValidItem_ReturnsAddedItem() {
        // Arrange
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(testMenuItem);

        // Act
        MenuItem result = menuService.addMenuItem(testMenuItem);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Item", result.getName());
        assertEquals(10.0, result.getPrice());
        verify(menuItemRepository, times(1)).save(testMenuItem);
    }

    @Test
    void updateMenuItem_ValidIdAndItem_ReturnsUpdatedItem() {
        // Arrange
        MenuItem updatedItem = new MenuItem();
        updatedItem.setName("Updated Item");
        updatedItem.setDescription("Updated Description");
        updatedItem.setPrice(15.0);
        updatedItem.setCategory(MenuCategory.DESSERT);

        MenuItem expectedItem = new MenuItem();
        expectedItem.setId(1L);
        expectedItem.setName("Updated Item");
        expectedItem.setDescription("Updated Description");
        expectedItem.setPrice(15.0);
        expectedItem.setCategory(MenuCategory.DESSERT);

        when(menuItemRepository.existsById(1L)).thenReturn(true);
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(expectedItem);

        // Act
        MenuItem result = menuService.updateMenuItem(1L, updatedItem);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated Item", result.getName());
        assertEquals(15.0, result.getPrice());
        assertEquals(MenuCategory.DESSERT, result.getCategory());
        verify(menuItemRepository, times(1)).save(any(MenuItem.class));
    }

    @Test
    void updateMenuItem_InvalidId_ThrowsNoSuchElementException() {
        // Arrange
        MenuItem updatedItem = new MenuItem();
        when(menuItemRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            menuService.updateMenuItem(999L, updatedItem);
        });
        verify(menuItemRepository, never()).save(any(MenuItem.class));
    }

    @Test
    void deleteMenuItem_ValidId_DeletesItem() {
        // Arrange
        when(menuItemRepository.existsById(1L)).thenReturn(true);
        doNothing().when(menuItemRepository).deleteById(1L);

        // Act
        menuService.deleteMenuItem(1L);

        // Assert
        verify(menuItemRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteMenuItem_InvalidId_ThrowsNoSuchElementException() {
        // Arrange
        when(menuItemRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            menuService.deleteMenuItem(999L);
        });
        verify(menuItemRepository, never()).deleteById(any());
    }
}