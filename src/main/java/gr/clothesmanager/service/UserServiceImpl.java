package gr.clothesmanager.service;

import gr.clothesmanager.dto.UserDTO;
import gr.clothesmanager.interfaces.RoleService;
import gr.clothesmanager.interfaces.UserService;
import gr.clothesmanager.model.Store;
import gr.clothesmanager.model.User;
import gr.clothesmanager.model.UserRole;
import gr.clothesmanager.repository.MaterialRepository;
import gr.clothesmanager.repository.OrderRepository;
import gr.clothesmanager.repository.StoreRepository;
import gr.clothesmanager.repository.UserRepository;
import gr.clothesmanager.service.exceptions.UserAlreadyExistsException;
import gr.clothesmanager.service.exceptions.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final MaterialRepository materialRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO saveUser(UserDTO userDTO, Store store) throws UserAlreadyExistsException {
        if (userDTO == null) {
            LOGGER.error("[saveUser] UserDTO is null");
            throw new IllegalArgumentException("UserDTO cannot be null");
        }
        if (userDTO.getUsername() == null || userDTO.getUsername().isBlank()) {
            LOGGER.error("[saveUser] Username is blank");
            throw new IllegalArgumentException("Username cannot be blank");
        }
        if (userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
            LOGGER.error("[saveUser] Password is blank");
            throw new IllegalArgumentException("Password cannot be blank");
        }
        if (store == null) {
            LOGGER.error("[saveUser] Store is null");
            throw new IllegalArgumentException("Store cannot be null");
        }

        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            LOGGER.error("[saveUser] User already exists with username: {}", userDTO.getUsername());
            throw new UserAlreadyExistsException("User already exists");
        }

        Set<UserRole> roles = assignRoles(userDTO.getRoles());

        User user = new User();
        user.setUsername(userDTO.getUsername().trim());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEnable(userDTO.getEnable());
        user.setStore(store);
        user.setRoles(roles);

        User saved = userRepository.save(user);
        LOGGER.info("Successfully created user '{}' (id={})", saved.getUsername(), saved.getId());
        return UserDTO.fromModel(saved);
    }


    @Transactional
    public UserDTO findUserById(Long id) throws UserNotFoundException {
        if (id == null) {
            LOGGER.error("[findUserById] User ID is null");
            throw new UserNotFoundException("UserId", "User ID cannot be null");
        }

        User user = userRepository.findById(id).orElseThrow(() -> {
            LOGGER.error("[findUserById] User not found with ID: {}", id);
            return new UserNotFoundException("User", "User not found with ID: " + id);
        });

        return UserDTO.fromModel(user);
    }

    @Transactional
    public UserDTO findUserByUsername(String username) throws UserNotFoundException {
        if (username == null || username.isBlank()) {
            LOGGER.error("[findUserByUsername] Username is blank");
            throw new UserNotFoundException("Username", "Username cannot be blank");
        }

        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            LOGGER.error("[findUserByUsername] User not found with username: {}", username);
            return new UserNotFoundException("User", "User not found with username: " + username);
        });

        return UserDTO.fromModel(user);
    }

    @Transactional
    public List<UserDTO> findAllUsers(String username) throws UserNotFoundException {
        if (username == null || username.isBlank()) {
            LOGGER.error("[findAllUsers] Username is blank");
            throw new UserNotFoundException("Username cannot be blank");
        }

        User loggedInUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        boolean isSuperAdmin = loggedInUser.getRoles().stream()
                .anyMatch(r -> "SUPER_ADMIN".equalsIgnoreCase(r.getName()));
        if (isSuperAdmin) {
            return userRepository.findAll().stream()
                    .map(UserDTO::fromModel)
                    .collect(Collectors.toList());
        }

        boolean isLocalAdmin = loggedInUser.getRoles().stream()
                .anyMatch(r -> "LOCAL_ADMIN".equalsIgnoreCase(r.getName()));
        if (isLocalAdmin) {
            if (loggedInUser.getStore() == null) {
                LOGGER.info("[findAllUsers] LOCAL_ADMIN '{}' has no store; returning empty list", username);
                return Collections.emptyList();
            }
            return userRepository.findByStoreId(loggedInUser.getStore().getId()).stream()
                    .map(UserDTO::fromModel)
                    .collect(Collectors.toList());
        }

        throw new AccessDeniedException("You do not have permission to view users.");
    }

    @Transactional
    public void deleteUserById(Long id) throws UserNotFoundException {
        if (id == null) {
            LOGGER.error("[deleteUserById] User ID is null");
            throw new UserNotFoundException("User ID cannot be null");}

        String currentUsername = getAuthenticatedUsername();

        User authenticatedUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found: " + currentUsername));

        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " does not exist"));

        if (authenticatedUser.getId().equals(userToDelete.getId())) {
            throw new AccessDeniedException("Δεν μπορείτε να διαγράψετε τον εαυτό σας.");
        }

        boolean isSuperAdmin = userToDelete.getRoles().stream()
                .anyMatch(r -> "SUPER_ADMIN".equalsIgnoreCase(r.getName()));
        if (isSuperAdmin) {
            throw new AccessDeniedException("Δεν μπορείτε να διαγράψετε χρήστες με ρόλο SUPER_ADMIN.");
        }

        if (userToDelete.getStore() != null) {
            Long storeId = userToDelete.getStore().getId();
            boolean hasMaterials = materialRepository.existsByStoreId(storeId);
            boolean hasOrders = orderRepository.existsByStoreId(storeId);

            if (hasMaterials || hasOrders) {
                throw new DataIntegrityViolationException(
                        "Ο χρήστης δεν μπορεί να διαγραφεί επειδή υπάρχουν συνδεδεμένα δεδομένα στην αποθήκη.");
            }
        }

        userRepository.deleteById(id);
        LOGGER.info("User with ID '{}' deleted successfully", id);
    }


    public boolean isSetupRequired() {
        return userRepository.count() == 0;
    }

    @Transactional
    public UserDTO createSuperAdminUser(String username, String password, Store store) throws UserAlreadyExistsException {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        // Create a SUPER_ADMIN role
        UserRole superAdminRole = roleService.getOrCreateRole("SUPER_ADMIN", "Super Admin");

        // Create a set with just the SUPER_ADMIN role
        Set<UserRole> roles = new HashSet<>();
        roles.add(superAdminRole);

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnable(1); // Enable the user
        user.setStore(store);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        LOGGER.info("Successfully created SUPER_ADMIN user: {}", username);
        return UserDTO.fromModel(savedUser);
    }


    private Set<UserRole> assignRoles(Set<UserRole> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            throw new IllegalArgumentException("Roles cannot be null or empty");
        }

        return roleNames.stream()
                .map(role -> {
                    String roleName = role.getName().toUpperCase(); // Convert role name to upper case
                    switch (roleName) {
                        case "SUPER_ADMIN":
                            return roleService.getOrCreateRole("SUPER_ADMIN", "Super Admin");
                        case "LOCAL_ADMIN":
                            return roleService.getOrCreateRole("LOCAL_ADMIN", "Local Admin");
                        default:
                            LOGGER.warn("Unknown role '{}', skipping...", roleName);
                            return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Transactional
    public UserDTO getAuthenticatedUserDetails() throws UserNotFoundException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        return UserDTO.fromModel(user);
    }

    public String getAuthenticatedUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    public void assignRoleToUser(Long userId, String roleName) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        UserRole role = roleService.getRoleByTag(roleName);
        if (role == null) {
            throw new IllegalArgumentException("Role with name '" + roleName + "' does not exist");
        }

        user.getRoles().clear();
        user.getRoles().add(role);
        userRepository.save(user);
    }
}
