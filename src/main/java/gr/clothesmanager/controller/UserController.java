package gr.clothesmanager.controller;

import gr.clothesmanager.auth.dto.ResponseMessageDTO;
import gr.clothesmanager.dto.UserDTO;
import gr.clothesmanager.service.UserServiceImpl;
import gr.clothesmanager.service.StoreServiceImpl;
import gr.clothesmanager.service.exceptions.StoreNotFoundException;
import gr.clothesmanager.service.exceptions.UserAlreadyExistsException;
import gr.clothesmanager.service.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;
    private final StoreServiceImpl storeService;

    // Fetch all users
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    // Fetch a single user by ID
    @GetMapping("/{id}")
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

    // Create a new user
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) throws UserAlreadyExistsException, StoreNotFoundException {
        if (userDTO.getStore() == null || userDTO.getStore().getId() == null) { // Check if store and its ID exist
            throw new IllegalArgumentException("Store ID is required.");
        }

        // Fetch the store entity using the store ID from UserDTO
        var store = storeService.findById(userDTO.getStore().getId());
        UserDTO createdUser = userService.saveUser(userDTO, store.toModel());
        return ResponseEntity.ok(createdUser);
    }



    // Delete a user
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) throws UserNotFoundException {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    // Get logged-in user details
    @GetMapping("/details")
    public ResponseEntity<UserDTO> getLoggedInUserDetails() throws UserNotFoundException {
        UserDTO loggedInUser = userService.getAuthenticatedUserDetails();
        return ResponseEntity.ok(loggedInUser);
    }
}
