package gr.clothesmanager.auth;


import gr.clothesmanager.auth.dto.AuthenticationRequest;
import gr.clothesmanager.auth.dto.AuthenticationResponse;
import gr.clothesmanager.service.exceptions.UserNotAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        try {
            AuthenticationResponse response = authenticationService.authenticate(request);
            LOGGER.info("User '{}' logged in successfully", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (UserNotAuthorizedException e) {
            LOGGER.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(401).body(null); // Unauthorized
        }
    }
}
