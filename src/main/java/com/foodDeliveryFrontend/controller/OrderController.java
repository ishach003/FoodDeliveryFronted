package com.foodDeliveryFrontend.controller;


import com.foodDeliveryFrontend.dto.OrderDto;
import com.foodDeliveryFrontend.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private ApiService apiService;

    /** Show order search form */
    @GetMapping
    public String orderForm() {
        return "order/order";
    }

    /** Lookup orders by customer ID (form POST) */
    @PostMapping("/customer")
    public String lookupByCustomer(@RequestParam Integer customerId, Model model) {
        List<OrderDto> orders = apiService.getOrdersByCustomerId(customerId);
        model.addAttribute("orders", orders);
        model.addAttribute("customerId", customerId);
        model.addAttribute("mode", "list");
        return "order/order";
    }

    /** Lookup single order by order ID (form POST) */
    @PostMapping("/detail")
    public String lookupByOrderId(@RequestParam Integer orderId, Model model) {
        OrderDto order = apiService.getOrderDetailsById(orderId);
        model.addAttribute("order", order);
        model.addAttribute("mode", "single");
        return "order/order";
    }

    /** Direct path-variable lookups (backwards compat) */
    @GetMapping("/customer/{customerId}")
    public String getOrdersByCustomer(@PathVariable Integer customerId, Model model) {
        model.addAttribute("orders", apiService.getOrdersByCustomerId(customerId));
        model.addAttribute("customerId", customerId);
        model.addAttribute("mode", "list");
        return "order/order";
    }

    @GetMapping("/{orderId}")
    public String getOrderDetails(@PathVariable Integer orderId, Model model) {
        model.addAttribute("order", apiService.getOrderDetailsById(orderId));
        model.addAttribute("mode", "single");
        return "order/order";
    }
}
