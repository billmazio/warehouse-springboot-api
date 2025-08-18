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

    @Transactional
    public StoreDTO save(StoreDTO storeDTO) throws StoreAlreadyExistsException {
        authorizationService.authorize(getAuthenticatedUsername(), "SUPER_ADMIN");

        if (storeDTO == null) {
            LOGGER.error("[save] StoreDTO is null");
            throw new IllegalArgumentException("StoreDTO cannot be null");
        }

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

        if (id == null) {
            LOGGER.error("[findById] Store ID is null");
            throw new StoreNotFoundException("Store ID cannot be null");
        }

        var store = storeRepository.findById(id).orElseThrow(() -> {
            LOGGER.error("[findById] Store not found with ID: {}", id);
            return new StoreNotFoundException("Store not found with ID: " + id);
        });

        return StoreDTO.fromModel(store);
    }


    @Transactional
    public List<StoreDTO> findAll() throws UserNotFoundException, StoreNotFoundException {
        String username = getAuthenticatedUsername();
        UserDTO userDTO = userServiceImpl.findUserByUsername(username); // throws if missing

        boolean isSuperAdmin = userDTO.getRoles().stream()
                .anyMatch(r -> "SUPER_ADMIN".equalsIgnoreCase(r.getName()));
        if (isSuperAdmin) {
            return storeRepository.findAll().stream()
                    .map(StoreDTO::fromModel)
                    .collect(Collectors.toList());
        }

        boolean isLocalAdmin = userDTO.getRoles().stream()
                .anyMatch(r -> "LOCAL_ADMIN".equalsIgnoreCase(r.getName()));
        if (isLocalAdmin) {
            if (userDTO.getStore() == null) {
                throw new AccessDeniedException("You do not have a store assigned to your account.");
            }
            Long storeId = userDTO.getStore().getId();
            var store = storeRepository.findById(storeId).orElseThrow(() -> {
                LOGGER.error("[findAll] Store not found for user '{}' (storeId={})", username, storeId);
                return new StoreNotFoundException("Store not found for your account.");
            });
            return List.of(StoreDTO.fromModel(store));
        }

        throw new AccessDeniedException("You do not have permission to view stores.");
    }


    @Transactional
    public void edit(Long id, StoreDTO storeDTO) throws StoreNotFoundException {
        authorizationService.authorize(getAuthenticatedUsername(), "SUPER_ADMIN");

        if (id == null) {
            LOGGER.error("[edit] Store ID is null");
            throw new StoreNotFoundException("Store ID cannot be null");
        }
        if (storeDTO == null) {
            LOGGER.error("[edit] StoreDTO is null");
            throw new IllegalArgumentException("StoreDTO cannot be null");
        }

        Store store = storeRepository.findById(id).orElseThrow(() -> {
            LOGGER.error("[edit] Store not found with ID: {}", id);
            return new StoreNotFoundException("Store not found with ID: " + id);
        });

        validateStore(storeDTO);

        store.setTitle(storeDTO.getTitle());
        store.setAddress(storeDTO.getAddress());
        store.setEnable(storeDTO.getEnable());

        storeRepository.save(store);
        LOGGER.info("Successfully edited store with ID: {}", id);
    }

    @Transactional
    public void deleteStoreById(Long id) throws StoreNotFoundException, UserNotFoundException {
        authorizationService.authorize(getAuthenticatedUsername(), "SUPER_ADMIN");

        if (id == null) {
            LOGGER.error("[deleteStoreById] Store ID is null");
            throw new StoreNotFoundException("Store ID cannot be null");
        }

        Store store = storeRepository.findById(id).orElseThrow(() -> {
            LOGGER.error("[deleteStoreById] Store not found with ID: {}", id);
            return new StoreNotFoundException("Η αποθήκη με ID " + id + " δεν βρέθηκε.");
        });

        boolean hasMaterials = materialRepository.existsByStoreId(id);
        boolean hasOrders = orderRepository.existsByStoreId(id);

        if (hasMaterials || hasOrders) {
            String msg = hasMaterials
                    ? "Η αποθήκη έχει συνδεδεμένα υλικά και δεν μπορεί να διαγραφεί."
                    : "Η αποθήκη έχει συνδεδεμένες παραγγελίες και δεν μπορεί να διαγραφεί.";
            LOGGER.error("[deleteStoreById] {}", msg);
            throw new IllegalStateException(msg);
        }

        storeRepository.delete(store);
        LOGGER.info("Successfully deleted store with ID: {}", id);
    }

    @Transactional
    public StoreDTO saveForSetup(StoreDTO storeDTO) {

        validateStore(storeDTO);

        Store store = new Store();
        store.setTitle(storeDTO.getTitle());
        store.setAddress(storeDTO.getAddress());
        store.setEnable(storeDTO.getEnable());

        Store savedStore = storeRepository.save(store);
        LOGGER.info("Successfully saved initial setup store with ID: {}", savedStore.getId());
        return StoreDTO.fromModel(savedStore);
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

