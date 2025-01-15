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

        boolean hasRole = userDTO.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(requiredRole));

        if (!hasRole) {
            throw new AccessDeniedException("User does not have the required role: " + requiredRole);
        }

        if (requiredRole.equalsIgnoreCase("SUPER_ADMIN") && !isSuperAdmin(userDTO)) {
            throw new AccessDeniedException("User is not a valid SUPER_ADMIN");
        }

        if (requiredRole.equalsIgnoreCase("LOCAL_ADMIN") && !isLocalAdmin(userDTO)) {
            throw new AccessDeniedException("User is not a valid LOCAL_ADMIN");
        }
    }

    private boolean isSuperAdmin(UserDTO userDTO) {
        return userDTO.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("SUPER_ADMIN"));
    }

    private boolean isLocalAdmin(UserDTO userDTO) {
        return userDTO.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("LOCAL_ADMIN"));
    }

}
