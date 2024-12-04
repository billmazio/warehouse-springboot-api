package gr.clothesmanager.service;


import gr.clothesmanager.dto.StoreDTO;
import gr.clothesmanager.interfaces.StoreService;
import gr.clothesmanager.model.Store;
import gr.clothesmanager.repository.StoreRepository;
import gr.clothesmanager.service.exceptions.StoreAlreadyExistsException;
import gr.clothesmanager.service.exceptions.StoreNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreServiceImpl.class);
    private final StoreRepository storeRepository;

    @Transactional
    public StoreDTO save(StoreDTO storeDTO) throws StoreAlreadyExistsException{
        if (storeDTO.getTitle() == null || storeDTO.getTitle().isEmpty()) {
            try {
                throw new StoreNotFoundException("Title is required");
            } catch (StoreNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        if (storeDTO.getAddress() == null || storeDTO.getAddress().isEmpty()) {
            try {
                throw new StoreNotFoundException("Address is required");
            } catch (StoreNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        if (storeDTO.getEnable() == null) {
            try {
                throw new StoreNotFoundException("Enable status is required");
            } catch (StoreNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        Store store = new Store();
        store.setTitle(storeDTO.getTitle());
        store.setAddress(storeDTO.getAddress());
        store.setEnable(storeDTO.getEnable());

        Store savedStore = storeRepository.save(store);
        LOGGER.info("Successfully saved store with ID: {}", savedStore.getId());
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

   @Transactional
    public void deleteStoreById(Long id) throws StoreNotFoundException{
        if (!storeRepository.existsById(id)) {
            throw new StoreNotFoundException("Store not found with ID: " + id);
        }
        storeRepository.deleteById(id);
        LOGGER.info("Successfully deleted store with ID: {}", id);
    }
}
