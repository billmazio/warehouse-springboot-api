package gr.clothesmanager.controller;

import gr.clothesmanager.auth.dto.ResponseMessageDTO;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        try {
            if (userDTO.getStore() == null) {
                throw new IllegalArgumentException("Store ID is required.");
            }

            var store = storeService.findById(userDTO.getStore().getId()); // Fetch the store entity
            UserDTO createdUser = userService.saveUser(userDTO, store.toModel());
            return ResponseEntity.ok(createdUser);
        } catch (UserAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Username already exists")); // Return a 409 Conflict response
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An error occurred"));
        }
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
