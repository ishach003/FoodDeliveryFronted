package com.foodDeliveryFrontend.controller;


import com.foodDeliveryFrontend.dto.MenuItemResponseDto;
import com.foodDeliveryFrontend.dto.RestaurantResponseDto;
import com.foodDeliveryFrontend.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/restaurant")
public class RestaurantController {

    @Autowired
    private ApiService apiService;

    @GetMapping
    public String restaurantForm() {
        return "restaurant/details";
    }

    @PostMapping("/lookup")
    public String lookupRestaurant(@RequestParam Integer restaurantId, Model model) {
        RestaurantResponseDto restaurant = apiService.getRestaurantById(restaurantId);
        List<MenuItemResponseDto> menuItems = apiService.getMenuItemsByRestaurantId(restaurantId);
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("menuItems", menuItems);
        return "restaurant/details";
    }

    @PostMapping("/menu")
    public String lookupMenu(@RequestParam Integer restaurantId, Model model) {
        List<MenuItemResponseDto> menuItems = apiService.getMenuItemsByRestaurantId(restaurantId);
        model.addAttribute("menuItems", menuItems);
        model.addAttribute("restaurantId", restaurantId);
        return "restaurant/menu";
    }

    @GetMapping("/{id}")
    public String getRestaurant(@PathVariable Integer id, Model model) {
        model.addAttribute("restaurant", apiService.getRestaurantById(id));
        model.addAttribute("menuItems", apiService.getMenuItemsByRestaurantId(id));
        return "restaurant/details";
    }

    @GetMapping("/{id}/menu")
    public String getMenu(@PathVariable Integer id, Model model) {
        model.addAttribute("menuItems", apiService.getMenuItemsByRestaurantId(id));
        return "restaurant/menu";
    }
}
