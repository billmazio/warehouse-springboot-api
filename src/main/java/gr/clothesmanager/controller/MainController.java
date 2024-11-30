package gr.clothesmanager.controller;

import gr.clothesmanager.repository.UserRepository;
import gr.clothesmanager.service.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*
package gr.clothesmanager.controller;

import gr.clothesmanager.dto.UserDTO;
import gr.clothesmanager.interfaces.*;
import gr.clothesmanager.model.UserRole;
import gr.clothesmanager.service.exceptions.UserNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MainController {

    private final UserService userService;
    private final RoleService roleService;
    private final OrderService orderService;
    private final MaterialService materialService;
    private final StoreService storeService;

    public MainController(UserService userService, RoleService roleService, OrderService orderService, MaterialService materialService, StoreService storeService) {
        this.userService = userService;
        this.roleService = roleService;
        this.orderService = orderService;
        this.materialService = materialService;
        this.storeService = storeService;
    }
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardData() {
        // Build and return JSON response
        Map<String, Object> response = new HashMap<>();
        response.put("key", "value");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);

        // Calculate counts and fetch additional info
        long userCount = userService.findAllUsers().stream().filter(u -> u.getEnable() == 1).count();
        long storageCount = storeService.findAll().stream().filter(s -> s.getEnable() == 1 && s.getId() != 1).count();
        long materialCount = materialService.findAll().size();
        long newOrdersCount = orderService.findAll().stream().filter(o -> o.getStatus() == 0).count();
        long ordersCount = orderService.findAll().size();
        String storeTitle = "My Store Name"; // Replace this with actual logic to fetch store title

        // Build response
        response.put("userCount", userCount);
        response.put("storageCount", storageCount);
        response.put("materialCount", materialCount);
        response.put("newOrdersCount", newOrdersCount);
        response.put("ordersCount", ordersCount);
        response.put("storeTitle", storeTitle);

        return ResponseEntity.ok(response);
    }

}*/
@RestController
@RequestMapping("/api")
public class MainController {

    private final UserRepository userRepository;

    public MainController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardData() {
        try {
            // Fetch active user count for the dashboard
            int activeUserCount = userRepository.countActiveUsersForDashboard();

            // Prepare the response with only the "user" field
            Map<String, Object> response = new HashMap<>();
            response.put("user", activeUserCount); // Active users

            // Commenting out other dashboard fields for now
            // response.put("storageCount", 0);
            // response.put("materialCount", 0);
            // response.put("newOrdersCount", 0);
            // response.put("ordersCount", 0);
            // response.put("storeTitle", "Store Name");

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to fetch dashboard data"));
        }
    }
}
