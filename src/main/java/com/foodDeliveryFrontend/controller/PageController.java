package com.foodDeliveryFrontend.controller;

import com.foodDeliveryFrontend.service.ApiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class PageController {

    private final ApiService apiService;

    // ── Dashboard ─────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("role", session.getAttribute("role"));
        return "admin/dashboard";
    }

    // ── Orders Page ───────────────────────────
    @GetMapping("/orders")
    public String orders(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("role", session.getAttribute("role"));
        return "customer/orders";
    }

    // ── Customers Page ────────────────────────
    @GetMapping("/customers")
    public String customers(@RequestParam(required = false) Integer customerId,
                            HttpSession session,
                            Model model) {
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("role", session.getAttribute("role"));
        if (customerId != null) {
            model.addAttribute("customerId", customerId);
            model.addAttribute("addresses", apiService.getCustomerAddress(customerId));
            model.addAttribute("orders", apiService.getOrdersByCustomerId(customerId));
        }
        return "admin/customers";  // ← was "customers"
    }

    // ── Products Page ─────────────────────────
    @GetMapping("/products")
    public String products(@RequestParam(required = false) Integer restaurantId,
                           HttpSession session,
                           Model model) {
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("role", session.getAttribute("role"));
        if (restaurantId != null) {
            model.addAttribute("restaurantId", restaurantId);
            model.addAttribute("menuItems", apiService.getMenuItemsByRestaurantId(restaurantId));
        }
        return "admin/products";  // ← was "products"
    }
}