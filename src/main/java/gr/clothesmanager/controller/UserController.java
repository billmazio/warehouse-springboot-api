package gr.clothesmanager.controller;

import gr.clothesmanager.auth.AuthorizationService;
import gr.clothesmanager.auth.dto.ResponseMessageDTO;
import gr.clothesmanager.core.enums.Status;
import gr.clothesmanager.dto.UserDTO;
import gr.clothesmanager.service.UserServiceImpl;
import gr.clothesmanager.service.StoreServiceImpl;
import gr.clothesmanager.service.exceptions.StoreNotFoundException;
import gr.clothesmanager.service.exceptions.UserAlreadyExistsException;
import gr.clothesmanager.service.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;
    private final StoreServiceImpl storeService;
    private final AuthorizationService authorizationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'LOCAL_ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() throws UserNotFoundException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<UserDTO> users = userService.findAllUsers(username);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'LOCAL_ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) throws UserNotFoundException {
        UserDTO user = userService.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{userId}/roles/{roleName}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ResponseMessageDTO> assignRoleToUser(
            @PathVariable Long userId,
            @PathVariable String roleName
    ) throws UserNotFoundException {
        userService.assignRoleToUser(userId, roleName);
        return ResponseEntity.ok(new ResponseMessageDTO("success", "Role assigned successfully"));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) throws UserAlreadyExistsException, StoreNotFoundException {
        if (userDTO.getStore() == null) {
            throw new IllegalArgumentException("Store ID is required.");
        }

        var store = storeService.findById(userDTO.getStore().getId());
        UserDTO createdUser = userService.saveUser(userDTO, store.toModel());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) throws UserNotFoundException {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/toggle-status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'LOCAL_ADMIN')")
    public ResponseEntity<UserDTO> toggleUserStatus(@PathVariable Long userId, @RequestBody Map<String, String> payload) throws UserNotFoundException {
        String statusStr = payload.get("status");
        if (statusStr == null) {
            throw new IllegalArgumentException("Status field is required");
        }

        Status status;
        try {
            status = Status.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + statusStr);
        }

        UserDTO updatedUser = userService.toggleUserStatus(userId, status);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/details")
    public ResponseEntity<UserDTO> getLoggedInUserDetails() throws UserNotFoundException {
        UserDTO loggedInUser = userService.getAuthenticatedUserDetails();
        return ResponseEntity.ok(loggedInUser);
    }
}