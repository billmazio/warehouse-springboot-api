package gr.clothesmanager.service;


import gr.clothesmanager.auth.AuthorizationService;
import gr.clothesmanager.core.enums.Status;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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
    private final UserRepository userRepository;

    @Transactional
    public StoreDTO save(StoreDTO storeDTO) throws StoreAlreadyExistsException {
        authorizationService.authorize(getAuthenticatedUsername(), "SUPER_ADMIN");

        validateStore(storeDTO);

        Store store = new Store();
        store.setTitle(storeDTO.getTitle());
        store.setAddress(storeDTO.getAddress());
        store.setStatus(storeDTO.getStatus());
        store.setIsSystemEntity(storeDTO.getIsSystemEntity() != null ? storeDTO.getIsSystemEntity() : false);

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
                .orElseThrow(() -> new StoreNotFoundException("STORE_NOT_FOUND"));
    }

    @Transactional
    public List<StoreDTO> findAll() throws UserNotFoundException {
        String username = getAuthenticatedUsername();
        UserDTO userDTO = userServiceImpl.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("USER_NOT_FOUND"));

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
                throw new AccessDeniedException("NO_STORE_ASSIGNED");
            }

            try {
                return storeRepository.findById(userDTO.getStore().getId())
                        .map(store -> List.of(StoreDTO.fromModel(store)))
                        .orElseThrow(() -> new StoreNotFoundException("STORE_NOT_FOUND"));
            } catch (StoreNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        throw new AccessDeniedException("ACCESS_DENIED");
    }

    @Transactional
    public void edit(Long id, StoreDTO storeDTO) throws StoreNotFoundException {
        authorizationService.authorize(getAuthenticatedUsername(), "SUPER_ADMIN");

        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new StoreNotFoundException("STORE_NOT_FOUND"));

        validateStore(storeDTO);

        store.setTitle(storeDTO.getTitle());
        store.setAddress(storeDTO.getAddress());
        store.setStatus(storeDTO.getStatus());

        // Only update isSystemEntity if it's provided (to avoid accidentally changing it)
        if (storeDTO.getIsSystemEntity() != null) {
            store.setIsSystemEntity(storeDTO.getIsSystemEntity());
        }

        storeRepository.save(store);
        LOGGER.info("Successfully edited store with ID: {}", id);
    }

    @Transactional
    public void deleteStoreById(Long id) throws StoreNotFoundException, UserNotFoundException {
        authorizationService.authorize(userServiceImpl.getAuthenticatedUserDetails().getUsername(), "SUPER_ADMIN");
        LOGGER.info("Attempting to delete store with ID: {}", id);

        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new StoreNotFoundException("STORE_NOT_FOUND"));

        // Check if it's a system entity
        if (store.getIsSystemEntity() != null && store.getIsSystemEntity()) {
            throw new IllegalStateException("SYSTEM_STORE_PROTECTED");
        }

        boolean hasMaterials = materialRepository.existsByStoreId(id);
        boolean hasOrders = orderRepository.existsByStoreId(id);
        boolean hasUsers = userRepository.existsByStoreId(id);

        if (hasMaterials) throw new IllegalStateException("STORE_DELETE_HAS_MATERIALS");
        if (hasOrders)   throw new IllegalStateException("STORE_DELETE_HAS_ORDERS");
        if (hasUsers) throw new IllegalStateException("STORE_DELETE_HAS_USERS");

        try {
            storeRepository.deleteById(id);
            LOGGER.info("Successfully deleted store with ID: {}", id);
        } catch (Exception ex) {
            LOGGER.error("Error deleting store: {}", ex.getMessage());
            throw new RuntimeException("Παρουσιάστηκε σφάλμα κατά τη διαγραφή της αποθήκης.", ex);
        }
    }

    @Transactional
    public StoreDTO saveForSetup(StoreDTO storeDTO) {
        validateStore(storeDTO);

        Store store = new Store();
        store.setTitle(storeDTO.getTitle());
        store.setAddress(storeDTO.getAddress());
        store.setStatus(storeDTO.getStatus());
        store.setIsSystemEntity(true); // Mark as system entity for setup

        Store savedStore = storeRepository.save(store);
        LOGGER.info("Successfully saved initial setup store with ID: {}", savedStore.getId());
        return StoreDTO.fromModel(savedStore);
    }

    @Transactional
    public StoreDTO updateStoreStatus(Long storeId, Status status) throws StoreNotFoundException, AccessDeniedException {
        authorizationService.authorize(getAuthenticatedUsername(), "SUPER_ADMIN");

        Store storeToUpdate = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreNotFoundException("STORE_NOT_FOUND"));

        storeToUpdate.setStatus(status);

        Store savedStore = storeRepository.save(storeToUpdate);

        LOGGER.info("Store '{}' status changed to {} by '{}'",
                storeToUpdate.getTitle(),
                status,
                getAuthenticatedUsername());

        return StoreDTO.fromModel(savedStore);
    }

    private void validateStore(StoreDTO storeDTO) {
        if (storeDTO.getTitle() == null || storeDTO.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title is required.");
        }
        if (storeDTO.getAddress() == null || storeDTO.getAddress().isEmpty()) {
            throw new IllegalArgumentException("Address is required.");
        }
        if (storeDTO.getStatus() == null) {
            throw new IllegalArgumentException("Status is required.");
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

