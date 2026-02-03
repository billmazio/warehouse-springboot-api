package gr.clothesmanager.service;

import gr.clothesmanager.core.enums.Status;
import gr.clothesmanager.dto.UserDTO;
import gr.clothesmanager.dto.UserRoleDTO;
import gr.clothesmanager.model.Store;
import gr.clothesmanager.model.User;
import gr.clothesmanager.model.UserRole;
import gr.clothesmanager.repository.MaterialRepository;
import gr.clothesmanager.repository.OrderRepository;
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
public class UserService  {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final MaterialRepository materialRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO saveUser(UserDTO userDTO, Store store) throws UserAlreadyExistsException {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("USER_ALREADY_EXISTS");
        }

        Set<UserRole> roles = assignRoles(userDTO.getRoles());
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setStatus(Status.ACTIVE);
        user.setStore(store);
        user.setRoles(roles);
        user.setIsSystemEntity(userDTO.getIsSystemEntity() != null ? userDTO.getIsSystemEntity() : false);

        userRepository.save(user);
        // Fetch with roles after save to populate them
        User savedUser = userRepository.findByIdWithRoles(user.getId()).orElse(user);
        return UserDTO.fromModel(savedUser);
    }

    @Transactional
    public Optional<UserDTO> findUserById(Long id) {
        if (id == null) return Optional.empty();
        return userRepository.findByIdWithRoles(id)
                .map(UserDTO::fromModel);
    }

    @Transactional
    public Optional<UserDTO> findUserByUsername(String username) {
        if (username == null || username.isBlank()) return Optional.empty();
        return userRepository.findByUsernameWithRoles(username)
                .map(UserDTO::fromModel);
    }

    @Transactional
    public List<UserDTO> findAllUsers(String username) throws UserNotFoundException {
        User loggedInUser = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UserNotFoundException("USER_NOT_FOUND"));

        if (loggedInUser.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("SUPER_ADMIN"))) {
            return userRepository.findAll().stream()
                    .map(user -> {
                        // Fetch each user with roles
                        return userRepository.findByIdWithRoles(user.getId())
                                .map(UserDTO::fromModel)
                                .orElse(null);
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else if (loggedInUser.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("LOCAL_ADMIN"))) {
            return userRepository.findByStoreIdWithRoles(loggedInUser.getStore().getId())
                    .stream()
                    .map(UserDTO::fromModel)
                    .collect(Collectors.toList());
        } else {
            throw new AccessDeniedException("ACCESS_DENIED");
        }
    }

    @Transactional
    public void deleteUserById(Long id) throws UserNotFoundException, AccessDeniedException, DataIntegrityViolationException {
        User authenticatedUser = userRepository.findByUsernameWithRoles(getAuthenticatedUsername())
                .orElseThrow(() -> new UserNotFoundException("USER_NOT_FOUND"));

        User userToDelete = userRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new UserNotFoundException("USER_NOT_FOUND"));

        if (userToDelete.getIsSystemEntity() != null && userToDelete.getIsSystemEntity()) {
            throw new IllegalStateException("SYSTEM_USER_PROTECTED");
        }

        if (authenticatedUser.getId().equals(userToDelete.getId())) {
            throw new AccessDeniedException("CANNOT_DELETE_SELF");
        }

        boolean isSuperAdmin = userToDelete.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("SUPER_ADMIN"));
        if (isSuperAdmin) {
            throw new AccessDeniedException("CANNOT_DELETE_SUPER_ADMIN");
        }

        if (userToDelete.getStore() != null) {
            boolean hasMaterials = materialRepository.existsByStoreId(userToDelete.getStore().getId());
            boolean hasOrders = orderRepository.existsByStoreId(userToDelete.getStore().getId());
            if (hasMaterials || hasOrders) {
                throw new DataIntegrityViolationException("INTEGRITY_VIOLATION");
            }
        }

        userRepository.deleteById(id);
    }

    @Transactional
    public UserDTO createSuperAdminUser(String username, String password, Store store) throws UserAlreadyExistsException {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        UserRole superAdminRole = roleService.getOrCreateRole("SUPER_ADMIN", "Super Admin");

        Set<UserRole> roles = new HashSet<>();
        roles.add(superAdminRole);

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(Status.ACTIVE);
        user.setStore(store);
        user.setRoles(roles);
        user.setIsSystemEntity(true);

        User savedUser = userRepository.save(user);
        // Fetch with roles after save
        savedUser = userRepository.findByIdWithRoles(savedUser.getId()).orElse(savedUser);
        LOGGER.info("Successfully created SUPER_ADMIN user: {}", username);
        return UserDTO.fromModel(savedUser);
    }

    private Set<UserRole> assignRoles(List<UserRoleDTO> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            throw new IllegalArgumentException("Roles cannot be null or empty");
        }

        return roleNames.stream()
                .map(role -> {
                    String roleName = role.getName().toUpperCase();
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

        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        return UserDTO.fromModel(user);
    }

    @Transactional
    public UserDTO toggleUserStatus(Long userId, Status newStatus) throws UserNotFoundException, AccessDeniedException {
        String authenticatedUsername = getAuthenticatedUsername();
        User authenticatedUser = userRepository.findByUsernameWithRoles(authenticatedUsername)
                .orElseThrow(() -> new UserNotFoundException("USER_NOT_FOUND"));

        User userToToggle = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new UserNotFoundException("USER_NOT_FOUND"));

        if (authenticatedUser.getId().equals(userToToggle.getId()) && newStatus == Status.INACTIVE) {
            throw new AccessDeniedException("CANNOT_DISABLE_OWN_ACCOUNT");
        }

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

        userToToggle.setStatus(newStatus);
        LOGGER.info("Changed user status for user ID {}: {}", userId, newStatus);

        User savedUser = userRepository.save(userToToggle);
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

    @Transactional
    public void assignRoleToUser(Long userId, String roleName) throws UserNotFoundException {
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        UserRole role = roleService.getRoleByTag(roleName);
        if (role == null) {
            throw new IllegalArgumentException("Role with name '" + roleName + "' does not exist");
        }

        user.getRoles().clear();
        user.getRoles().add(role);
        userRepository.save(user);
    }

    public boolean isSetupRequired() {
        return userRepository.count() == 0;
    }
}