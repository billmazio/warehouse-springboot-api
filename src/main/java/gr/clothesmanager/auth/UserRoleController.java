package gr.clothesmanager.auth;

import gr.clothesmanager.auth.dto.ResponseMessageDTO;
import gr.clothesmanager.dto.UserDTO;
import gr.clothesmanager.service.RoleServiceImpl;
import gr.clothesmanager.service.UserServiceImpl;
import gr.clothesmanager.service.exceptions.UserAlreadyExistsException;
import gr.clothesmanager.service.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRoleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRoleController.class);

    private final UserServiceImpl userService;
    private final RoleServiceImpl roleService;


    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) throws UserAlreadyExistsException {
        try {
            UserDTO createdUser = userService.saveUser(userDTO);
            LOGGER.info("User created with username: {}", createdUser.getUsername());
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            LOGGER.error("Error creating user: {}", e.getMessage());
            throw e;
        }
    }


    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAllUsers();
        LOGGER.info("Fetched {} users", users.size());
        return ResponseEntity.ok(users);
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) throws UserNotFoundException {
        var user = userService.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
        LOGGER.info("Fetched user with ID: {}", id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{userId}/roles/{roleTag}")
    public ResponseEntity<ResponseMessageDTO> assignRoleToUser(
            @PathVariable Long userId,
            @PathVariable String roleTag
    ) throws UserNotFoundException {
        try {
            userService.assignRoleToUser(userId, roleTag);
            LOGGER.info("Role '{}' assigned to user with ID {}", roleTag, userId);
            return ResponseEntity.ok(new ResponseMessageDTO("success", "Role assigned successfully"));
        } catch (Exception e) {
            LOGGER.error("Error assigning role to user: {}", e.getMessage());
            throw e;
        }
    }


    @GetMapping("/roles")
    public ResponseEntity<List<String>> getAllRoles() {
        List<String> roles = roleService.findAllRoles();
        LOGGER.info("Fetched {} roles", roles.size());
        return ResponseEntity.ok(roles);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO> deleteUser(@PathVariable Long id) throws UserNotFoundException {
        userService.deleteUserById(id);
        LOGGER.info("User with ID {} deleted successfully", id);
        return ResponseEntity.ok(new ResponseMessageDTO("success", "User deleted successfully"));
    }
}
