package com.foodDeliveryFrontend.controller;


import com.foodDeliveryFrontend.dto.RatingDto;
import com.foodDeliveryFrontend.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/ratings")
public class RatingController {

    @Autowired
    private ApiService apiService;

    // Load page
    @GetMapping
    public String ratingsForm() {
        return "rating/restaurantrating"; // direct page load
    }

    // Fetch by Rating ID
    @PostMapping("/lookup")
    public String lookupRating(@RequestParam Integer ratingId, Model model) {
        model.addAttribute("rating", apiService.getRatingById(ratingId));
        model.addAttribute("mode", "single");
        return "rating/rating-details";
    }

    // Fetch ratings by restaurant (FORM SUBMIT)
    @PostMapping("/restaurant")
    public String lookupRestaurantRatings(@RequestParam Integer restaurantId, Model model) {

        List<RatingDto> ratings = apiService.getRatingsByRestaurant(restaurantId);

        model.addAttribute("ratings", ratings);
        model.addAttribute("restaurantId", restaurantId);

        return "rating/restaurantrating";
    }

    // Fetch single rating (URL)
    @GetMapping("/{id}")
    public String getRating(@PathVariable Integer id, Model model) {
        model.addAttribute("rating", apiService.getRatingById(id));
        return "rating/rating-details";
    }

    // Fetch ratings by restaurant (URL)
    @GetMapping("/restaurant/{restaurantId}")
    public String getRatingsByRestaurant(@PathVariable Integer restaurantId, Model model) {

        List<RatingDto> ratings = apiService.getRatingsByRestaurant(restaurantId);

        model.addAttribute("ratings", ratings);
        model.addAttribute("restaurantId", restaurantId);

        return "rating/restaurantrating";
    }
}