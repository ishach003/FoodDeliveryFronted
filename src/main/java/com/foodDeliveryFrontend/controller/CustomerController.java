package com.foodDeliveryFrontend.controller;


import com.foodDeliveryFrontend.dto.CustomerAddressDto;
import com.foodDeliveryFrontend.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private ApiService apiService;

    /** Show address lookup form */
    @GetMapping("/address")
    public String addressForm() {
        return "customer/address";
    }

    /** Handle address lookup form submission */
    @PostMapping("/address")
    public String lookupAddress(@RequestParam Integer customerId, Model model) {
        List<CustomerAddressDto> addresses = apiService.getCustomerAddress(customerId);
        model.addAttribute("addresses", addresses);
        model.addAttribute("customerId", customerId);
        return "customer/address";
    }

    /** Direct path-variable address lookup (backwards compat) */
    @GetMapping("/{id}/address")
    public String getCustomerAddresses(@PathVariable Integer id, Model model) {
        List<CustomerAddressDto> addresses = apiService.getCustomerAddress(id);
        model.addAttribute("addresses", addresses);
        model.addAttribute("customerId", id);
        return "customer/address";
    }

    /** Show orders lookup form */
    @GetMapping("/orders")
    public String ordersForm() {
        return "customer/orders";
    }

    /** Handle orders lookup form submission */
    @PostMapping("/orders")
    public String lookupOrders(@RequestParam Integer customerId, Model model) {
        model.addAttribute("orders", apiService.getOrdersByCustomerId(customerId));
        model.addAttribute("customerId", customerId);
        return "customer/orders";
    }
}
