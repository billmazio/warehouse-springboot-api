package gr.clothesmanager.service;


import gr.clothesmanager.auth.AuthorizationService;
import gr.clothesmanager.dto.StoreDTO;
import gr.clothesmanager.dto.UserDTO;
import gr.clothesmanager.interfaces.StoreService;
import gr.clothesmanager.model.Store;
import gr.clothesmanager.repository.MaterialRepository;
import gr.clothesmanager.repository.OrderRepository;
import gr.clothesmanager.repository.StoreRepository;
import gr.clothesmanager.repository.UserRepository;
import gr.clothesmanager.service.exceptions.StoreAlreadyExistsException;
import gr.clothesmanager.service.exceptions.StoreNotFoundException;
import gr.clothesmanager.service.exceptions.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreServiceImpl.class);
    private final StoreRepository storeRepository;
    private final MaterialRepository materialRepository;
    private final OrderRepository orderRepository;
    private final AuthorizationService authorizationService;
    private final UserServiceImpl userServiceImpl;

    @Transactional
    public StoreDTO save(StoreDTO storeDTO) throws StoreAlreadyExistsException {
        // Authorization check for creating a store
        authorizationService.authorize(getAuthenticatedUsername(), "SUPER_ADMIN");

        validateStore(storeDTO);

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
        String username = getAuthenticatedUsername();
        authorizationService.authorize(username, "SUPER_ADMIN", "LOCAL_ADMIN");

        return storeRepository.findById(id)
                .map(StoreDTO::fromModel)
                .orElseThrow(() -> new StoreNotFoundException("Store not found with ID: " + id));
    }


    @SneakyThrows
    @Transactional
    public List<StoreDTO> findAll() {
        String username = getAuthenticatedUsername();
        UserDTO userDTO = userServiceImpl.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Check if the user is a SUPER_ADMIN
        boolean isSuperAdmin = userDTO.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("SUPER_ADMIN"));

        if (isSuperAdmin) {
            return storeRepository.findAll().stream()
                    .map(StoreDTO::fromModel)
                    .collect(Collectors.toList());
        }
        boolean isLocalAdmin = userDTO.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("LOCAL_ADMIN"));

        if (isLocalAdmin) {
            if (userDTO.getStore() == null) {
                throw new AccessDeniedException("You do not have a store assigned to your account.");
            }

            return storeRepository.findById(userDTO.getStore().getId())
                    .map(store -> List.of(StoreDTO.fromModel(store)))
                    .orElseThrow(() -> new StoreNotFoundException("Store not found for your account."));
        }

        throw new AccessDeniedException("You do not have permission to view stores.");
    }


    @Transactional
    public void edit(Long id, StoreDTO storeDTO) throws StoreNotFoundException {
        authorizationService.authorize(getAuthenticatedUsername(), "SUPER_ADMIN");

        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new StoreNotFoundException("Store not found with ID: " + id));

        validateStore(storeDTO);

        store.setTitle(storeDTO.getTitle());
        store.setAddress(storeDTO.getAddress());
        store.setEnable(storeDTO.getEnable());

        storeRepository.save(store);
        LOGGER.info("Successfully edited store with ID: {}", id);
    }

    @Transactional
    public void deleteStoreById(Long id) throws StoreNotFoundException, UserNotFoundException {
        // Explicit authorization check
        authorizationService.authorize(userServiceImpl.getAuthenticatedUserDetails().getUsername(), "SUPER_ADMIN");

        LOGGER.info("Attempting to delete store with ID: {}", id);

        // Check if the store exists
        if (!storeRepository.existsById(id)) {
            throw new StoreNotFoundException("Η αποθήκη με ID " + id + " δεν βρέθηκε.");
        }

        // Check if the store has related materials or orders
        boolean hasMaterials = materialRepository.existsByStoreId(id);
        boolean hasOrders = orderRepository.existsByStoreId(id);

        if (hasMaterials || hasOrders) {
            String errorMessage = hasMaterials
                    ? "Η αποθήκη έχει συνδεδεμένα υλικά και δεν μπορεί να διαγραφεί."
                    : "Η αποθήκη έχει συνδεδεμένες παραγγελίες και δεν μπορεί να διαγραφεί.";
            throw new IllegalStateException(errorMessage);
        }

        // Attempt to delete the store
        try {
            storeRepository.deleteById(id);
            LOGGER.info("Successfully deleted store with ID: {}", id);
        } catch (Exception ex) {
            LOGGER.error("Error deleting store: {}", ex.getMessage());
            throw new RuntimeException("Παρουσιάστηκε σφάλμα κατά τη διαγραφή της αποθήκης.", ex);
        }
    }



    private void validateStore(StoreDTO storeDTO) {
        if (storeDTO.getTitle() == null || storeDTO.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title is required.");
        }
        if (storeDTO.getAddress() == null || storeDTO.getAddress().isEmpty()) {
            throw new IllegalArgumentException("Address is required.");
        }
        if (storeDTO.getEnable() == null) {
            throw new IllegalArgumentException("Enable status is required.");
        }
    }

    private String getAuthenticatedUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }



}

