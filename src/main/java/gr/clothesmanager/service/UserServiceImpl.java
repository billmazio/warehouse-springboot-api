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
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }
        Set<UserRole> roles = assignRoles(userDTO.getRoles());
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Encode the password correctly
        user.setEnable(userDTO.getEnable());
        user.setStore(store);
        user.setRoles(roles);

        userRepository.save(user);
        return UserDTO.fromModel(user);
    }

    @Transactional
    public Optional<UserDTO> findUserById(Long id) {
        if (id == null) return Optional.empty();
        return userRepository.findById(id).map(UserDTO::fromModel);
    }

    @Transactional
    public Optional<UserDTO> findUserByUsername(String username) {
        if (username == null || username.isBlank()) return Optional.empty();
        return userRepository.findByUsername(username).map(UserDTO::fromModel);
    }

    @Transactional
    public List<UserDTO> findAllUsers(String username) throws UserNotFoundException {
        User loggedInUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));


        if (loggedInUser.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("SUPER_ADMIN"))) {
            return userRepository.findAll().stream()
                    .map(UserDTO::fromModel)
                    .collect(Collectors.toList());
        } else if (loggedInUser.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("LOCAL_ADMIN"))) {
            return userRepository.findByStoreId(loggedInUser.getStore().getId()).stream()
                    .map(UserDTO::fromModel)
                    .collect(Collectors.toList());
        } else {
            throw new AccessDeniedException("You do not have permission to view users.");
        }
    }

    @Transactional
    public void deleteUserById(Long id) throws UserNotFoundException, AccessDeniedException, DataIntegrityViolationException {
        User authenticatedUser = userRepository.findByUsername(getAuthenticatedUsername())
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));

        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " does not exist"));

        if (authenticatedUser.getId().equals(userToDelete.getId())) {
            throw new AccessDeniedException("Δεν μπορεί να διαγραφεί ο SUPER_ADMIN.");
        }

        boolean isSuperAdmin = userToDelete.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("SUPER_ADMIN"));
        if (isSuperAdmin) {
            throw new AccessDeniedException("Δεν μπορείτε να διαγράψετε χρήστες με ρόλο SUPER_ADMIN.");
        }

        if (userToDelete.getStore() != null) {
            boolean hasMaterials = materialRepository.existsByStoreId(userToDelete.getStore().getId());
            boolean hasOrders = orderRepository.existsByStoreId(userToDelete.getStore().getId());

            if (hasMaterials || hasOrders) {
                throw new DataIntegrityViolationException("Ο χρήστης δεν μπορεί να διαγραφεί επειδή υπάρχουν συνδεδεμένα δεδομένα στην αποθήκη.");
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

    @Transactional
    public UserDTO toggleUserStatus(Long userId, boolean enable) throws UserNotFoundException, AccessDeniedException {
        String authenticatedUsername = getAuthenticatedUsername();
        User authenticatedUser = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));

        User userToToggle = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " does not exist"));

        // 1. Prevent user from disabling themselves
        if (authenticatedUser.getId().equals(userToToggle.getId())) {
            throw new AccessDeniedException("CANNOT_DISABLE_OWN_ACCOUNT");
        }

        // 2. Prevent disabling SUPER_ADMIN users (unless authenticated user is also SUPER_ADMIN)
        boolean targetIsSuperAdmin = userToToggle.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("SUPER_ADMIN"));
        boolean authenticatedIsSuperAdmin = authenticatedUser.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("SUPER_ADMIN"));

        if (targetIsSuperAdmin && !authenticatedIsSuperAdmin) {
            throw new AccessDeniedException("CANNOT_MODIFY_SUPER_ADMIN");
        }

        boolean authenticatedIsLocalAdmin = authenticatedUser.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("LOCAL_ADMIN"));

        if (authenticatedIsLocalAdmin && !authenticatedIsSuperAdmin) {
            if (userToToggle.getStore() == null ||
                    !userToToggle.getStore().getId().equals(authenticatedUser.getStore().getId())) {
                throw new AccessDeniedException("CANNOT_MODIFY_OTHER_STORE_USERS");
            }
        }

        // Update the user status
        int enableValue = enable ? 1 : 0;
        userToToggle.setEnable(enableValue);

        User savedUser = userRepository.save(userToToggle);

        LOGGER.info("User '{}' status changed to {} by '{}'",
                userToToggle.getUsername(),
                enable ? "enabled" : "disabled",
                authenticatedUsername);

        return UserDTO.fromModel(savedUser);
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
