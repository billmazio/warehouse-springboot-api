package gr.clothesmanager.auth;

import gr.clothesmanager.dto.UserDTO;
import gr.clothesmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserService userService;

    public void authorize(String username, String... allowedRoles) {
        UserDTO userDTO;
        userDTO = userService.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("User not found"));

        boolean hasRole = userDTO.getRoles().stream()
                .map(role -> role.getName().toUpperCase())
                .anyMatch(role -> Arrays.stream(allowedRoles)
                        .map(String::toUpperCase)
                        .anyMatch(role::equals));

        if (!hasRole) {
            throw new AccessDeniedException("Ο χρήστης δεν έχει τα απαιτούμενα δικαιώματα: " + String.join(", ", allowedRoles));
        }
    }
}
