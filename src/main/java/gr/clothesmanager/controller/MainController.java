package gr.clothesmanager.controller;

import gr.clothesmanager.repository.*;
import jakarta.servlet.http.HttpServletRequest;
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

@RestController
@RequestMapping("/api")
public class MainController {

    private final UserRepository userRepository;
    private final MaterialRepository materialRepository;
    private final SizeRepository sizeRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    public MainController(
            UserRepository userRepository,
            MaterialRepository materialRepository,
            SizeRepository sizeRepository,
            OrderRepository orderRepository,
            StoreRepository storeRepository
    ) {
        this.userRepository = userRepository;
        this.materialRepository = materialRepository;
        this.sizeRepository = sizeRepository;
        this.orderRepository = orderRepository;
        this.storeRepository = storeRepository;
    }

    /**
     * Endpoint for fetching dashboard data.
     * Requires authenticated access with either ROLE_USER or ROLE_ADMIN.
     */
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardData(HttpServletRequest request) {
        try {
            // Validate Authorization header
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Missing or invalid token");
            }

            // Fetch counts for each entity
            int activeUserCount = userRepository.countActiveUsersForDashboard();
            int materialCount = materialRepository.countMaterials();
            int sizeCount = sizeRepository.countSizes();
            int orderCount = orderRepository.countOrders();
            int storeCount = storeRepository.countStores();

            // Prepare the response
            Map<String, Object> response = new HashMap<>();
            response.put("user", activeUserCount);
            response.put("materials", materialCount);
            response.put("sizes", sizeCount);
            response.put("orders", orderCount);
            response.put("stores", storeCount);

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to fetch dashboard data"));
        }
    }
}
