package com.foodDeliveryFrontend.controller;


import com.foodDeliveryFrontend.service.ApiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String customers(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("role", session.getAttribute("role"));
        return "customers";
    }

    // ── Products Page ─────────────────────────
    @GetMapping("/products")
    public String products(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("role", session.getAttribute("role"));
        return "products";
    }
}