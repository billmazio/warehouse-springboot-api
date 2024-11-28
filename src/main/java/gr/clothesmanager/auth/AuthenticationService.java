package gr.clothesmanager.auth;

import gr.clothesmanager.auth.dto.AuthenticationRequest;
import gr.clothesmanager.auth.dto.AuthenticationResponse;
import gr.clothesmanager.dto.UserDTO;
import gr.clothesmanager.interfaces.UserService;
import gr.clothesmanager.service.exceptions.UserNotAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticates the user based on the username and password.
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) throws UserNotAuthorizedException {
        try {
            // Fetch the user by username
            UserDTO userDTO = userService.findUserByUsername(request.getUsername())
                    .orElseThrow(() -> new UserNotAuthorizedException("Invalid username or password"));

            // Validate the password
            if (!passwordEncoder.matches(request.getPassword(), userDTO.getPassword())) {
                throw new UserNotAuthorizedException("Invalid username or password");
            }

            LOGGER.info("User '{}' authenticated successfully", request.getUsername());
            return new AuthenticationResponse(null, userDTO.getUsername(), userDTO.getRoles()); // Token is set to null since we're not using JWT
        } catch (Exception e) {
            LOGGER.error("Authentication failed for user '{}': {}", request.getUsername(), e.getMessage());
            throw new UserNotAuthorizedException("Authentication failed: " + e.getMessage());
        }
    }
}
