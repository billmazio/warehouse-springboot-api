/*
package gr.clothesmanager.auth;

import gr.clothesmanager.dto.UserDTO;
import gr.clothesmanager.interfaces.UserService;
import gr.clothesmanager.service.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserService userService;

    public void authorize(String username, String requiredRole) {
        UserDTO userDTO;
        try {
            userDTO = userService.findUserByUsername(username)
                    .orElseThrow(() -> new AccessDeniedException("User not found"));
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Check role-specific conditions
        boolean hasRole = userDTO.getRoles().stream()
                .anyMatch(role -> role.getName().equals(requiredRole));

        if (!hasRole) {
            throw new AccessDeniedException("User does not have the required role: " + requiredRole);
        }

        // Additional checks for specific roles
        if (requiredRole.equals("SUPER_ADMIN")) {
            if (!isSuperAdmin(userDTO)) {
                throw new AccessDeniedException("User is not a valid SUPER_ADMIN");
            }
        } else if (requiredRole.equals("LOCAL_ADMIN")) {
            if (!isLocalAdmin(userDTO)) {
                throw new AccessDeniedException("User is not a valid LOCAL_ADMIN");
            }
        }
    }

    private boolean isSuperAdmin(UserDTO userDTO) {
        // Custom logic to check if the user is a SUPER_ADMIN
        // For example, check if the username starts with "super_" or matches a specific code
        return userDTO.getUsername().startsWith("super_") || "SUPER_CODE".equals(userDTO.getPassword());
    }

    private boolean isLocalAdmin(UserDTO userDTO) {
        // Custom logic to check if the user is a LOCAL_ADMIN
        // For example, check if the username starts with "local_" or matches a specific code
        return userDTO.getUsername().startsWith("local_") || "LOCAL_CODE".equals(userDTO.getPassword());
    }
}
*/
