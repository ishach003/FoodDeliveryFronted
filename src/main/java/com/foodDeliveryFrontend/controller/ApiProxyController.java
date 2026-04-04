package com.foodDeliveryFrontend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Controller
@RequiredArgsConstructor
public class ApiProxyController {

    private final WebClient webClient;

    /**
     * Proxy every /api/** request to the backend.
     * JWT token is read from the HTTP session (stored there by LoginController).
     */
    @RequestMapping("/api/**")
    @ResponseBody
    public ResponseEntity<String> proxyApi(
            HttpServletRequest request,
            HttpSession session,
            @RequestBody(required = false) String body) {


        String path  = request.getRequestURI();

        if (path.startsWith("/api")) {
            path = path.substring(4);
        }
        String query = request.getQueryString();
        String backendUri = (query != null) ? path + "?" + query : path;


        String token = (String) session.getAttribute("token");

        if (token == null) {
            return ResponseEntity.status(401)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body("{\"success\":false,\"message\":\"Not authenticated. Please log in.\"}");
        }


        try {
            WebClient.RequestBodySpec requestSpec = webClient
                    .method(HttpMethod.valueOf(request.getMethod()))
                    .uri(backendUri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            String contentType = request.getContentType();
            if (contentType != null) {
                requestSpec = requestSpec.header(HttpHeaders.CONTENT_TYPE, contentType);
            }

            ResponseEntity<String> backendResponse;
            if (body != null && !body.isBlank()) {
                backendResponse = requestSpec.bodyValue(body)
                        .retrieve().toEntity(String.class).block();
            } else {
                backendResponse = requestSpec
                        .retrieve().toEntity(String.class).block();
            }

            if (backendResponse == null) {
                return ResponseEntity.status(502)
                        .header(HttpHeaders.CONTENT_TYPE, "application/json")
                        .body("{\"success\":false,\"message\":\"No response from backend.\"}");
            }

            return ResponseEntity
                    .status(backendResponse.getStatusCode())
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body(backendResponse.getBody());

        } catch (WebClientResponseException ex) {
            // Backend returned 4xx / 5xx — pass it through transparently
            return ResponseEntity
                    .status(ex.getStatusCode())
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body(ex.getResponseBodyAsString());

        } catch (Exception ex) {
            String msg = ex.getMessage() != null ? ex.getMessage().replace("\"", "'") : "unknown";
            return ResponseEntity.status(503)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body("{\"success\":false,\"message\":\"Cannot reach backend: " + msg + "\"}");
        }
    }
}
