package gr.clothesmanager.controller;

import gr.clothesmanager.auth.dto.SetupRequestDTO;
import gr.clothesmanager.core.enums.Status;
import gr.clothesmanager.dto.StoreDTO;
import gr.clothesmanager.dto.UserDTO;
import gr.clothesmanager.model.Store;
import gr.clothesmanager.service.StoreService;
import gr.clothesmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
public class SetupController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetupController.class);

    private final UserService userService;
    private final StoreService storeService;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> checkSetupStatus() {
        boolean setupRequired = userService.isSetupRequired();
        return ResponseEntity.ok(Map.of("setupRequired", setupRequired));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> initialSetup(@RequestBody SetupRequestDTO setupRequest) {
        try {
            // Check if setup already completed
            if (!userService.isSetupRequired()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of(
                                "error", true,
                                "code", "SETUP_ALREADY_COMPLETED",
                                "message", "Setup has already been completed"
                        ));
            }

            // Validate input
            String validationError = validateSetupRequest(setupRequest);
            if (validationError != null) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error", true,
                                "code", "VALIDATION_ERROR",
                                "message", validationError
                        ));
            }

            // Create store
            StoreDTO storeDTO = new StoreDTO();
            storeDTO.setTitle(setupRequest.getStoreTitle());
            storeDTO.setAddress(setupRequest.getStoreAddress());
            storeDTO.setStatus(Status.ACTIVE);

            StoreDTO savedStoreDTO = storeService.saveForSetup(storeDTO);
            Store savedStore = savedStoreDTO.toModel();

            LOGGER.info("Created initial store: {}", savedStore.getTitle());

            // Create super admin user
            UserDTO createdUser = userService.createSuperAdminUser(
                    setupRequest.getUsername(),
                    setupRequest.getPassword(),
                    savedStore
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Setup completed successfully",
                    "user", createdUser,
                    "store", savedStoreDTO
            ));

        } catch (Exception ex) {
            LOGGER.error("Setup failed: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", true,
                            "code", "SETUP_FAILED",
                            "message", "Setup failed: " + ex.getMessage()
                    ));
        }
    }

    private String validateSetupRequest(SetupRequestDTO setupRequest) {
        if (setupRequest.getUsername() == null || setupRequest.getUsername().trim().isEmpty()) {
            return "Username is required";
        }
        if (setupRequest.getPassword() == null || setupRequest.getPassword().trim().isEmpty()) {
            return "Password is required";
        }
        if (setupRequest.getStoreTitle() == null || setupRequest.getStoreTitle().trim().isEmpty()) {
            return "Store title is required";
        }
        if (setupRequest.getStoreAddress() == null || setupRequest.getStoreAddress().trim().isEmpty()) {
            return "Store address is required";
        }
        return null; // No validation errors
    }
}