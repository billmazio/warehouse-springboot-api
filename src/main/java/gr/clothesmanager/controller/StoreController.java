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
    public ResponseEntity<List<StoreDTO>> getAllStores() throws UserNotFoundException {
        List<StoreDTO> stores = storeService.findAll();
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreDTO> getStoreById(@PathVariable Long id) throws StoreNotFoundException {
        StoreDTO store = storeService.findById(id);
        return ResponseEntity.ok(store);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<StoreDTO> createStore(@RequestBody StoreDTO storeDTO) throws StoreAlreadyExistsException, UserNotFoundException {
        authorizationService.authorize(
                userServiceImpl.getAuthenticatedUserDetails().getUsername(),
                "SUPER_ADMIN"
        );

        StoreDTO createdStore = storeService.save(storeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStore);
    }

    @GetMapping("/{storeId}/materials")
    public ResponseEntity<List<MaterialDTO>> getMaterialsByStore(@PathVariable Long storeId) throws UserNotFoundException {
        List<MaterialDTO> materials = materialService.findMaterialsByStoreId(storeId);
        return ResponseEntity.ok(materials);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> editStore(@PathVariable Long id, @RequestBody StoreDTO storeDTO) throws StoreNotFoundException, UserNotFoundException {
        authorizationService.authorize(userServiceImpl.getAuthenticatedUserDetails().getUsername(), "SUPER_ADMIN");

        storeService.edit(id, storeDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteStore(@PathVariable Long id) throws UserNotFoundException, StoreNotFoundException {
        storeService.deleteStoreById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{storeId}/toggle-status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<StoreDTO> toggleStoreStatus(@PathVariable Long storeId, @RequestBody Map<String, String> payload) throws StoreNotFoundException {
        String statusStr = payload.get("status");

        if (statusStr == null) {
            throw new IllegalArgumentException("Status field is required");
        }

        Status status;
        try {
            status = Status.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + statusStr);
        }

        StoreDTO updatedStore = storeService.updateStoreStatus(storeId, status);
        return ResponseEntity.ok(updatedStore);
    }
}