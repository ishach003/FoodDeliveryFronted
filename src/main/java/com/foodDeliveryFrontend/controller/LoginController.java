package com.foodDeliveryFrontend.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final RestTemplate restTemplate;

    @Value("${backend.api.base-url}")
    private String baseUrl;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            HttpSession session,
                            Model model) {
        if (session.getAttribute("token") != null) return "redirect:/admin/dashboard";
        if (error  != null) model.addAttribute("error",   "Invalid username or password.");
        if (logout != null) model.addAttribute("message", "You have been logged out.");
        return "pages/login";
    }

    @PostMapping("/do-login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        try {
            String url = baseUrl + "/auth/login";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String body = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.POST, new HttpEntity<>(body, headers), Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<?, ?> rb = response.getBody();

                // Backend wraps payload in a "data" object:
                // { "status":200, "message":"Login successful", "data":{ "token":"...", "username":"...", "role":"..." } }
                Map<?, ?> data = rb.containsKey("data") && rb.get("data") instanceof Map
                        ? (Map<?, ?>) rb.get("data")
                        : rb;   // fallback: top-level (handles flat responses)

                String token = firstNonNull(data, "token", "accessToken", "jwt");
                String role  = data.containsKey("role") ? (String) data.get("role") : "USER";

                if (token != null) {
                    session.setAttribute("token",    token);
                    session.setAttribute("username", username);
                    session.setAttribute("role",     role);
                    return "redirect:/admin/dashboard";
                }
            }
            model.addAttribute("error", "Login failed: backend returned no token.");
        } catch (HttpClientErrorException.Unauthorized ex) {
            model.addAttribute("error", "Invalid username or password.");
        } catch (Exception ex) {
            model.addAttribute("error", "Cannot reach backend: " + ex.getMessage());
        }
        return "pages/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }

    private String firstNonNull(Map<?, ?> map, String... keys) {
        for (String key : keys) {
            if (map.containsKey(key) && map.get(key) != null) return (String) map.get(key);
        }
        return null;
    }
}