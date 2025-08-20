package gr.clothesmanager.controller;

import gr.clothesmanager.auth.dto.SetupRequestDTO;
import gr.clothesmanager.core.enums.Status;
import gr.clothesmanager.dto.StoreDTO;
import gr.clothesmanager.dto.UserDTO;
import gr.clothesmanager.model.Store;
import gr.clothesmanager.service.StoreServiceImpl;
import gr.clothesmanager.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
public class SetupController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetupController.class);

    private final UserServiceImpl userService;
    private final StoreServiceImpl storeService;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> checkSetupStatus() {
        boolean setupRequired = userService.isSetupRequired();
        Map<String, Boolean> response = new HashMap<>();
        response.put("setupRequired", setupRequired);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> initialSetup(@RequestBody SetupRequestDTO setupRequest) {
        try {
            if (!userService.isSetupRequired()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Setup has already been completed"));
            }

            if (setupRequest.getUsername() == null || setupRequest.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Username is required"));
            }

            if (setupRequest.getPassword() == null || setupRequest.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Password is required"));
            }

            if (setupRequest.getStoreTitle() == null || setupRequest.getStoreTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Store title is required"));
            }

            if (setupRequest.getStoreAddress() == null || setupRequest.getStoreAddress().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Store address is required"));
            }

            StoreDTO storeDTO = new StoreDTO();
            storeDTO.setTitle(setupRequest.getStoreTitle());
            storeDTO.setAddress(setupRequest.getStoreAddress());
            storeDTO.setStatus(Status.ACTIVE);

            StoreDTO savedStoreDTO = storeService.saveForSetup(storeDTO);
            Store savedStore = savedStoreDTO.toModel();

            LOGGER.info("Created initial store: {}", savedStore.getTitle());

            UserDTO createdUser = userService.createSuperAdminUser(
                    setupRequest.getUsername(),
                    setupRequest.getPassword(),
                    savedStore
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Setup completed successfully");
            response.put("user", createdUser);
            response.put("store", savedStoreDTO);

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            LOGGER.error("Setup failed: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Setup failed: " + ex.getMessage()));
        }
    }
}