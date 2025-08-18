package gr.clothesmanager.auth;

import gr.clothesmanager.dto.UserDTO;
import gr.clothesmanager.interfaces.UserService;
import gr.clothesmanager.service.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserService userService;

    public void authorize(String username, String... allowedRoles) {
        if (username == null || username.isBlank()) {
            throw new AccessDeniedException("Missing username");
        }

        UserDTO userDTO;
        try {
            userDTO = userService.findUserByUsername(username);
        } catch (UserNotFoundException e) {
            throw new AccessDeniedException("User not found: " + username);
        }

        boolean isSuperAdmin = userDTO.getRoles() != null && userDTO.getRoles().stream()
                .anyMatch(r -> "SUPER_ADMIN".equalsIgnoreCase(r.getName()));
        if (isSuperAdmin) return;

        Set<String> allowed = Arrays.stream(allowedRoles == null ? new String[0] : allowedRoles)
                .filter(Objects::nonNull)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        boolean hasRole = userDTO.getRoles() != null && userDTO.getRoles().stream()
                .map(r -> r.getName() == null ? "" : r.getName().toUpperCase())
                .anyMatch(allowed::contains);

        if (!hasRole) {
            throw new AccessDeniedException(
                    "Ο χρήστης δεν έχει τα απαιτούμενα δικαιώματα: " + String.join(", ", allowed));
        }
    }

}
