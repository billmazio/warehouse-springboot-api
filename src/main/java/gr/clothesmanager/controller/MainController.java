package gr.clothesmanager.controller;

import gr.clothesmanager.core.enums.Status;
import gr.clothesmanager.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MainController {

    private final UserRepository userRepository;
    private final MaterialRepository materialRepository;
    private final SizeRepository sizeRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    @GetMapping("/dashboard")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        Map<String, Object> dashboardData = Map.of(
                "user", userRepository.countActiveUsersForDashboard(Status.ACTIVE),
                "materials", materialRepository.countMaterials(),
                "sizes", sizeRepository.countSizes(),
                "orders", orderRepository.countOrders(),
                "stores", storeRepository.countStores()
        );

        return ResponseEntity.ok(dashboardData);
    }
}