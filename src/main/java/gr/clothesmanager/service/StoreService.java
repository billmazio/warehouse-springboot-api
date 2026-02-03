package gr.clothesmanager.service;


import gr.clothesmanager.auth.AuthorizationService;
import gr.clothesmanager.core.enums.Status;
import gr.clothesmanager.dto.StoreDTO;
import gr.clothesmanager.dto.UserDTO;
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
public class StoreService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreService.class);
    private final StoreRepository storeRepository;
    private final MaterialRepository materialRepository;
    private final OrderRepository orderRepository;
    private final AuthorizationService authorizationService;
    private final UserService userService;
    private final UserRepository userRepository;

    @Transactional
    public StoreDTO save(StoreDTO dto) throws StoreAlreadyExistsException {
        authorizationService.authorize(getAuthenticatedUsername(), "SUPER_ADMIN");

        validateStore(dto);

        Store store = dto.toModel();
        store.setTitle(dto.getTitle());
        store.setAddress(dto.getAddress());
        store.setStatus(dto.getStatus());

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
        UserDTO userDTO = userService.findUserByUsername(username)
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
    public void edit(Long id, StoreDTO dto) throws StoreNotFoundException {
        authorizationService.authorize(getAuthenticatedUsername(), "SUPER_ADMIN");

        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new StoreNotFoundException("STORE_NOT_FOUND"));

        validateStore(dto);

        store.setTitle(dto.getTitle());
        store.setAddress(dto.getAddress());
        store.setStatus(dto.getStatus());

        storeRepository.save(store);
        LOGGER.info("Successfully edited store with ID: {}", id);
    }

    @Transactional
    public void deleteStoreById(Long id) throws StoreNotFoundException, UserNotFoundException {
        authorizationService.authorize(userService.getAuthenticatedUserDetails().getUsername(), "SUPER_ADMIN");
        LOGGER.info("Attempting to delete store with ID: {}", id);

        if (materialRepository.existsByStoreId(id)) throw new IllegalStateException("STORE_DELETE_HAS_MATERIALS");
        if (orderRepository.existsByStoreId(id)) throw new IllegalStateException("STORE_DELETE_HAS_ORDERS");
        if (userRepository.existsByStoreId(id)) throw new IllegalStateException("STORE_DELETE_HAS_USERS");

        int deleted = storeRepository.deleteDirectlyById(id);
        if (deleted == 0) {
            throw new StoreNotFoundException("STORE_NOT_FOUND");
        }

        LOGGER.info("Successfully deleted store with ID: {}", id);
    }

    @Transactional
    public StoreDTO saveForSetup(StoreDTO dto) {
        validateStore(dto);

        Store store = new Store();
        store.setTitle(dto.getTitle());
        store.setAddress(dto.getAddress());
        store.setStatus(dto.getStatus());

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

    private void validateStore(StoreDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title is required.");
        }
        if (dto.getAddress() == null || dto.getAddress().isEmpty()) {
            throw new IllegalArgumentException("Address is required.");
        }
        if (dto.getStatus() == null) {
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

