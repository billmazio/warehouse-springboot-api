package gr.clothesmanager.auth;


import gr.clothesmanager.auth.dto.AuthenticationRequest;
import gr.clothesmanager.auth.dto.AuthenticationResponse;
import gr.clothesmanager.service.exceptions.UserNotAuthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request, HttpServletRequest webRequest) {
        try {
            // Authenticate user credentials
            AuthenticationResponse response = authenticationService.authenticate(request);

            // Log the successful login
            LOGGER.info("User '{}' logged in successfully from IP: {}", request.getUsername(), getClientIpAddress(webRequest));

            return ResponseEntity.ok(response);
        } catch (UserNotAuthorizedException e) {
            // Log the failure
            LOGGER.error("Authentication failed for user '{}': {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(401).body(null); // Unauthorized
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return ResponseEntity.ok("Logged out successfully");
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader != null && !xForwardedForHeader.isEmpty()) {
            return xForwardedForHeader.split(",")[0].trim();
        } else {
            return request.getRemoteAddr();
        }
    }
}
