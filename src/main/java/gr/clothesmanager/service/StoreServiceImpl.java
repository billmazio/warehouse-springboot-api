package gr.clothesmanager.service;


import gr.clothesmanager.dto.StoreDTO;
import gr.clothesmanager.interfaces.StoreService;
import gr.clothesmanager.model.Store;
import gr.clothesmanager.repository.StoreRepository;
import gr.clothesmanager.service.exceptions.StoreAlreadyExistsException;
import gr.clothesmanager.service.exceptions.StoreNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    @Transactional
    public StoreDTO save(StoreDTO storeDTO) throws StoreAlreadyExistsException {
        // Convert DTO to entity
        Store store = new Store();
        store.setTitle(storeDTO.getTitle());
        store.setAddress(storeDTO.getAddress());
        // Save and return the saved entity as DTO
        Store savedStore = storeRepository.save(store);
        return StoreDTO.fromModel(savedStore);
    }

    @Transactional
    public StoreDTO findById(Long id) throws StoreNotFoundException {
        // Find store by ID and map to DTO
        return storeRepository.findById(id)
                .map(StoreDTO::fromModel)
                .orElseThrow(() -> new StoreNotFoundException("Store not found with ID: " + id));
    }

    @Transactional
    public List<StoreDTO> findAll() {
        // Get all stores and map to DTOs
        return storeRepository.findAll().stream()
                .map(StoreDTO::fromModel)
                .collect(Collectors.toList());
    }

    @Transactional
    public void edit(Long id, StoreDTO storeDTO) throws StoreNotFoundException {
        // Find the existing store entity
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new StoreNotFoundException("Store not found with ID: " + id));

        // Update the entity fields
        store.setTitle(storeDTO.getTitle());
        store.setAddress(storeDTO.getAddress());

        // Save the updated store
        storeRepository.save(store);
    }

 /*  @Transactional
    public void delete(Long id) {
        storeRepository.deleteById(id);
    }
*/
}
