package gr.clothesmanager.controller;

import gr.clothesmanager.dto.MaterialDTO;
import gr.clothesmanager.dto.StoreDTO;
import gr.clothesmanager.service.MaterialServiceImpl;
import gr.clothesmanager.service.StoreServiceImpl;
import gr.clothesmanager.service.exceptions.StoreAlreadyExistsException;
import gr.clothesmanager.service.exceptions.StoreNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    // Fetch all stores
    @GetMapping
    public ResponseEntity<List<StoreDTO>> getAllStores() {
        try {
            List<StoreDTO> stores = storeService.findAll();
            return ResponseEntity.ok(stores);
        } catch (Exception ex) {
            return ResponseEntity.status(500)
                    .body(null); // Generic error for fetch failure
        }
    }

    // Fetch a store by ID
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

    // Create a new store
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createStore(@RequestBody StoreDTO storeDTO) {
        try {
            StoreDTO createdStore = storeService.save(storeDTO);
            return ResponseEntity.ok(createdStore);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", ex.getMessage())); // Validation failure
        } catch (StoreAlreadyExistsException ex) {
            return ResponseEntity.status(409)
                    .body(Map.of("message", "Store already exists.")); // Duplicate store
        } catch (Exception ex) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "An error occurred while creating the store."));
        }
    }

    // Fetch materials for a store
    @GetMapping("/{storeId}/materials")
    public ResponseEntity<List<MaterialDTO>> getMaterialsByStore(@PathVariable Long storeId) {
        try {
            List<MaterialDTO> materials = materialService.findMaterialsByStoreId(storeId);
            return ResponseEntity.ok(materials);
        } catch (Exception ex) {
            return ResponseEntity.status(500)
                    .body(null); // Generic error for fetch failure
        }
    }

    // Edit an existing store
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> editStore(@PathVariable Long id, @RequestBody StoreDTO storeDTO) {
        try {
            storeService.edit(id, storeDTO);
            return ResponseEntity.noContent().build(); // Successfully edited
        } catch (StoreNotFoundException ex) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Store not found.")); // Store not found
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", ex.getMessage())); // Validation failure
        } catch (Exception ex) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "An error occurred while editing the store."));
        }
    }

    // Delete a store
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteStore(@PathVariable Long id) {
        try {
            storeService.deleteStoreById(id);
            return ResponseEntity.noContent().build(); // Successfully deleted
        } catch (StoreNotFoundException ex) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Store not found.")); // Store not found
        } catch (Exception ex) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "An error occurred while deleting the store."));
        }
    }
}
