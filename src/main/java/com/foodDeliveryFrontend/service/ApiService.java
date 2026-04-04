package com.foodDeliveryFrontend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodDeliveryFrontend.dto.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ApiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${backend.api.base-url}")
    private String baseUrl;

    public ApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpSession session = attrs.getRequest().getSession(false);
            if (session != null) {
                String token = (String) session.getAttribute("token");
                if (token != null) {
                    headers.setBearerAuth(token);
                }
            }
        }
        return headers;
    }

    private <T> T get(String url, Class<T> responseType) {
        ResponseEntity<Map> response =
                restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(authHeaders()), Map.class);
        Map<?, ?> body = response.getBody();
        if (body == null || body.get("data") == null) return null;
        return objectMapper.convertValue(body.get("data"), responseType);
    }

    private <T> List<T> getList(String url, Class<T> elementType) {
        ResponseEntity<Map> response =
                restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(authHeaders()), Map.class);
        Map<?, ?> body = response.getBody();
        if (body == null || body.get("data") == null) return Collections.emptyList();
        Object data = body.get("data");
        if (!(data instanceof List)) return Collections.emptyList();
        List<?> rawList = (List<?>) data;
        return rawList.stream()
                .map(item -> objectMapper.convertValue(item, elementType))
                .collect(Collectors.toList());
    }

    public List<CustomerAddressDto> getCustomerAddress(Integer customerId) {
        return getList(baseUrl + "/api/customer/" + customerId + "/addresses", CustomerAddressDto.class);
    }

    public DeliveryDriverDto getDriverById(Integer driverId) {
        return get(baseUrl + "/api/driver/" + driverId, DeliveryDriverDto.class);
    }

    public MenuItemResponseDto getMenuItemById(Integer itemId) {
        return get(baseUrl + "/api/menuitems/" + itemId, MenuItemResponseDto.class);
    }

    public List<MenuItemResponseDto> getMenuItemsByRestaurantId(Integer restaurantId) {
        return getList(baseUrl + "/api/restaurant/" + restaurantId + "/menuitems", MenuItemResponseDto.class);
    }


    public List<OrderDto> getOrdersByCustomerId(Integer customerId) {
        return getList(baseUrl + "/api/customers/" + customerId + "/orders", OrderDto.class);
    }

    public OrderDto getOrderDetailsById(Integer orderId) {
        return get(baseUrl + "/api/orders/" + orderId, OrderDto.class);
    }


    public RatingDto getRatingById(Integer ratingId) {
        return get(baseUrl + "/api/rating/" + ratingId, RatingDto.class);
    }

    public List<RatingDto> getRatingsByRestaurant(Integer restaurantId) {
        return getList(baseUrl + "/api/rating/restaurant/" + restaurantId, RatingDto.class);
    }
    public RestaurantResponseDto getRestaurantById(Integer id) {
        return get(baseUrl + "/restaurants/" + id, RestaurantResponseDto.class);
    }
    // GET /api/orders
    public List<OrderDto> getAllOrders() {
        return getList(baseUrl + "/api/orders", OrderDto.class);
    }


}
