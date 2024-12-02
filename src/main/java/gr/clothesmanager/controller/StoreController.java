package gr.clothesmanager.controller;

import gr.clothesmanager.dto.StoreDTO;
import gr.clothesmanager.service.StoreServiceImpl;
import gr.clothesmanager.service.exceptions.StoreNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreServiceImpl storeService;

    // Fetch all stores
    @GetMapping
    public ResponseEntity<List<StoreDTO>> getAllStores() {
        List<StoreDTO> stores = storeService.findAll();
        return ResponseEntity.ok(stores);
    }


    // Fetch a store by ID
    @GetMapping("/{id}")
    public ResponseEntity<StoreDTO> getStoreById(@PathVariable Long id) {
        StoreDTO store = storeService.findById(id);
        return ResponseEntity.ok(store);
    }

    // Create a new store
    @PostMapping
    public ResponseEntity<StoreDTO> createStore(@RequestBody StoreDTO storeDTO) {
        StoreDTO createdStore = storeService.save(storeDTO);
        return ResponseEntity.ok(createdStore);
    }

    // Edit an existing store
    @PutMapping("/{id}")
    public ResponseEntity<Void> editStore(@PathVariable Long id, @RequestBody StoreDTO storeDTO) throws StoreNotFoundException {
        storeService.edit(id, storeDTO);
        return ResponseEntity.noContent().build();
    }

    // Delete a store by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStore(@PathVariable Long id) {
        storeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
