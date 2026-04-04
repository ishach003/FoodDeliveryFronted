package com.foodDeliveryFrontend.controller;


import com.foodDeliveryFrontend.dto.DeliveryDriverDto;
import com.foodDeliveryFrontend.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/driver")
public class DriverController {

    @Autowired
    private ApiService apiService;

    @GetMapping
    public String driverForm() {
        return "driver/driver-details";
    }

    @PostMapping
    public String lookupDriver(@RequestParam Integer driverId, Model model) {
        DeliveryDriverDto driver = apiService.getDriverById(driverId);
        model.addAttribute("driver", driver);
        return "driver/driver-details";
    }

    @GetMapping("/{id}")
    public String getDriver(@PathVariable Integer id, Model model) {
        model.addAttribute("driver", apiService.getDriverById(id));
        return "driver/driver-details";
    }
}
