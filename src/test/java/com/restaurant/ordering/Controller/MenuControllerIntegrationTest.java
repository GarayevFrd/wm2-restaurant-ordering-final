package com.restaurant.ordering.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.ordering.Enums.MenuCategory;
import com.restaurant.ordering.Model.MenuItem;
import com.restaurant.ordering.Repository.MenuItemRepository;
import com.restaurant.ordering.Security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MenuControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String authToken;

    @BeforeEach
    void setUp() {
        menuItemRepository.deleteAll();
        authToken = "Bearer " + jwtTokenProvider.createToken("manager", "ROLE_MANAGER");
    }

    @Test
    void getAllItems_ReturnsMenuItems() throws Exception {
        MenuItem item = new MenuItem();
        item.setName("Pizza");
        item.setDescription("Cheese Pizza");
        item.setPrice(12.5);
        item.setCategory(MenuCategory.MAIN_COURSE);
        menuItemRepository.save(item);

        mockMvc.perform(get("/manager/menu")
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Pizza")))
                .andExpect(jsonPath("$[0].description", is("Cheese Pizza")))
                .andExpect(jsonPath("$[0].price", is(12.5)))
                .andExpect(jsonPath("$[0].category", is("MAIN_COURSE")));
    }

    @Test
    void getAllItems_Empty_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/manager/menu")
                .header("Authorization", authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void addMenuItem_Valid_ReturnsCreatedItem() throws Exception {
        MenuItem item = new MenuItem();
        item.setName("Burger");
        item.setDescription("Beef Burger");
        item.setPrice(9.99);
        item.setCategory(MenuCategory.MAIN_COURSE);

        mockMvc.perform(post("/manager/menu")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Burger")))
                .andExpect(jsonPath("$.description", is("Beef Burger")))
                .andExpect(jsonPath("$.price", is(9.99)))
                .andExpect(jsonPath("$.category", is("MAIN_COURSE")));
    }

    @Test
    void addMenuItem_NullName_ReturnsBadRequest() throws Exception {
        MenuItem item = new MenuItem();
        item.setDescription("No Name");
        item.setPrice(5.0);
        item.setCategory(MenuCategory.DESSERT);

        mockMvc.perform(post("/manager/menu")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_ExistingItem_ReturnsOk() throws Exception {
        MenuItem item = new MenuItem();
        item.setName("Salad");
        item.setDescription("Green Salad");
        item.setPrice(7.0);
        item.setCategory(MenuCategory.APPETIZER);
        item = menuItemRepository.save(item);

        item.setDescription("Fresh Green Salad");
        item.setPrice(7.5);

        mockMvc.perform(put("/manager/menu/" + item.getId())
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Fresh Green Salad")))
                .andExpect(jsonPath("$.price", is(7.5)));
    }

    @Test
    void updateItem_NonExisting_ReturnsNotFound() throws Exception {
        MenuItem item = new MenuItem();
        item.setName("NonExistent");
        item.setDescription("Does not exist");
        item.setPrice(1.0);
        item.setCategory(MenuCategory.DESSERT);

        mockMvc.perform(put("/manager/menu/9999")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteItem_Existing_ReturnsOk() throws Exception {
        MenuItem item = new MenuItem();
        item.setName("DeleteMe");
        item.setDescription("To be deleted");
        item.setPrice(3.0);
        item.setCategory(MenuCategory.DESSERT);
        item = menuItemRepository.save(item);

        mockMvc.perform(delete("/manager/menu/" + item.getId())
                .header("Authorization", authToken))
                .andExpect(status().isOk());
    }

    @Test
    void deleteItem_NonExisting_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/manager/menu/9999")
                .header("Authorization", authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllItems_WithoutAuth_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/manager/menu"))
                .andExpect(status().isForbidden());
    }

    @Test
    void addMenuItem_WithoutAuth_ReturnsForbidden() throws Exception {
        MenuItem item = new MenuItem();
        item.setName("NoAuth");
        item.setDescription("No Auth");
        item.setPrice(1.0);
        item.setCategory(MenuCategory.DESSERT);

        mockMvc.perform(post("/manager/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateItem_WithoutAuth_ReturnsForbidden() throws Exception {
        MenuItem item = new MenuItem();
        item.setName("NoAuth");
        item.setDescription("No Auth");
        item.setPrice(1.0);
        item.setCategory(MenuCategory.DESSERT);

        mockMvc.perform(put("/manager/menu/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteItem_WithoutAuth_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/manager/menu/1"))
                .andExpect(status().isForbidden());
    }
} 