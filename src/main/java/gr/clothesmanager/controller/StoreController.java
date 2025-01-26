package gr.clothesmanager.controller;

import gr.clothesmanager.auth.AuthorizationService;
import gr.clothesmanager.dto.MaterialDTO;
import gr.clothesmanager.dto.StoreDTO;
import gr.clothesmanager.service.MaterialServiceImpl;
import gr.clothesmanager.service.StoreServiceImpl;
import gr.clothesmanager.service.UserServiceImpl;
import gr.clothesmanager.service.exceptions.StoreAlreadyExistsException;
import gr.clothesmanager.service.exceptions.StoreNotFoundException;
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

    // Fetch all stores
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
            // Explicit authorization check
            authorizationService.authorize(
                    userServiceImpl.getAuthenticatedUserDetails().getUsername(),
                    "SUPER_ADMIN"
            );

            StoreDTO createdStore = storeService.save(storeDTO);
            return ResponseEntity.ok(createdStore);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", ex.getMessage())); // Validation failure
        } catch (StoreAlreadyExistsException ex) {
            return ResponseEntity.status(409)
                    .body(Map.of("message", "Store already exists.")); // Duplicate store
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "You do not have permission to create a store.")); // Authorization failure
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
            authorizationService.authorize(
                    userServiceImpl.getAuthenticatedUserDetails().getUsername(),
                    "SUPER_ADMIN"
            );

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
            // Call service to delete the store
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
                    .body(Map.of("message", "Δεν έχετε δικαίωμα να διαγράψετε αποθήκες."));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Παρουσιάστηκε σφάλμα κατά τη διαγραφή της αποθήκης."));
        }
    }


}
