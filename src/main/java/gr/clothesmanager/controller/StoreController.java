package gr.clothesmanager.controller;

import gr.clothesmanager.auth.AuthorizationService;
import gr.clothesmanager.core.enums.Status;
import gr.clothesmanager.dto.MaterialDTO;
import gr.clothesmanager.dto.StoreDTO;
import gr.clothesmanager.service.MaterialServiceImpl;
import gr.clothesmanager.service.StoreServiceImpl;
import gr.clothesmanager.service.UserServiceImpl;
import gr.clothesmanager.service.exceptions.StoreAlreadyExistsException;
import gr.clothesmanager.service.exceptions.StoreNotFoundException;
import gr.clothesmanager.service.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreServiceImpl storeService;
    private final MaterialServiceImpl materialService;
    private final AuthorizationService authorizationService;
    private final UserServiceImpl userServiceImpl;

    @GetMapping
    public ResponseEntity<List<StoreDTO>> getAllStores() {
        try {
            List<StoreDTO> stores = storeService.findAll();
            return ResponseEntity.ok(stores);
        } catch (Exception ex) {
            return ResponseEntity.status(500)
                    .body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreDTO> getStoreById(@PathVariable Long id) {
        try {
            StoreDTO store = storeService.findById(id);
            return ResponseEntity.ok(store);
        } catch (StoreNotFoundException ex) {
            return ResponseEntity.status(404)
                    .body(null); // Store not found
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createStore(@RequestBody StoreDTO storeDTO) {
        try {
            authorizationService.authorize(
                    userServiceImpl.getAuthenticatedUserDetails().getUsername(),
                    "SUPER_ADMIN"
            );

            StoreDTO createdStore = storeService.save(storeDTO);
            return ResponseEntity.ok(createdStore);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", ex.getMessage()));
        } catch (StoreAlreadyExistsException ex) {
            return ResponseEntity.status(409)
                    .body(Map.of("message", "Store already exists."));
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "You do not have permission to create a store."));
        } catch (Exception ex) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "An error occurred while creating the store."));
        }
    }

    @GetMapping("/{storeId}/materials")
    public ResponseEntity<List<MaterialDTO>> getMaterialsByStore(@PathVariable Long storeId) {
        try {
            List<MaterialDTO> materials = materialService.findMaterialsByStoreId(storeId);
            return ResponseEntity.ok(materials);
        } catch (Exception ex) {
            return ResponseEntity.status(500)
                    .body(null);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> editStore(@PathVariable Long id, @RequestBody StoreDTO storeDTO) {
        try {
            authorizationService.authorize(userServiceImpl.getAuthenticatedUserDetails().getUsername(), "SUPER_ADMIN");

            storeService.edit(id, storeDTO);
            return ResponseEntity.noContent().build();
        } catch (StoreNotFoundException ex) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Store not found."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", ex.getMessage()));
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "You do not have permission to edit this store."));
        } catch (Exception ex) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "An error occurred while editing the store."));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteStore(@PathVariable Long id) {
        try {
            storeService.deleteStoreById(id);
            return ResponseEntity.noContent().build();
        } catch (StoreNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", ex.getMessage())); // Use detailed message
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "You do not have permission to delete warehouses."));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An error occurred while deleting the warehouse."));
        }
    }

    @PatchMapping("/{storeId}/toggle-status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> toggleStoreStatus(@PathVariable Long storeId, @RequestBody Map<String, String> payload) {
        try {
            String statusStr = payload.get("status");

            if (statusStr == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Status field is required"));
            }

            Status status;
            try {
                status = Status.valueOf(statusStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Invalid status value: " + statusStr));
            }

            // Call service method with the enum status directly
            StoreDTO updatedStore = storeService.updateStoreStatus(storeId, status);


            return ResponseEntity.ok(updatedStore);
        } catch (StoreNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Store not found."));
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An error occurred while updating the store"));
        }
    }
}
