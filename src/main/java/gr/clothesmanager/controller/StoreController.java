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

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreServiceImpl storeService;
    private final MaterialServiceImpl materialService;

    // Fetch all stores
    @GetMapping
    public ResponseEntity<List<StoreDTO>> getAllStores() {
        List<StoreDTO> stores = storeService.findAll();
        return ResponseEntity.ok(stores);
    }


    // Fetch a store by ID
    @GetMapping("/{id}")
    public ResponseEntity<StoreDTO> getStoreById(@PathVariable Long id) throws StoreNotFoundException {
        StoreDTO store = storeService.findById(id);
        return ResponseEntity.ok(store);
    }


    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<StoreDTO> createStore(@RequestBody StoreDTO storeDTO) throws StoreAlreadyExistsException {
        if (storeDTO.getTitle() == null || storeDTO.getAddress() == null) {
            throw new IllegalArgumentException("Title and Address are required");
        }
        StoreDTO createdStore = storeService.save(storeDTO);
        return ResponseEntity.ok(createdStore);
    }

    @GetMapping("/{storeId}/materials")
    public ResponseEntity<List<MaterialDTO>> getMaterialsByStore(@PathVariable Long storeId) {
        List<MaterialDTO> materials = materialService.findMaterialsByStoreId(storeId);
        return ResponseEntity.ok(materials);
    }

    // Edit an existing store
    @PutMapping("/{id}")
    public ResponseEntity<Void> editStore(@PathVariable Long id, @RequestBody StoreDTO storeDTO) throws StoreNotFoundException {
        storeService.edit(id, storeDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteStore(@PathVariable Long id) throws StoreNotFoundException {
        storeService.deleteStoreById(id);
        return ResponseEntity.noContent().build();
    }

}
