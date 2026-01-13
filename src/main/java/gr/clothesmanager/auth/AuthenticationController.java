package gr.clothesmanager.auth;

import gr.clothesmanager.auth.dto.ChangePasswordRequest;
import gr.clothesmanager.auth.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authenticationService;

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            authenticationService.changePassword(request);
            return ResponseEntity.ok("Password changed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Αποτυχία αλλαγής κωδικού: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            String clientIp = getClientIpAddress(request);
            LOGGER.info("Login attempt for user: {} from IP: {}", loginRequest.getUsername(), clientIp);

            String token = authenticationService.authenticateAndGenerateToken(loginRequest);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Login successful");
            responseBody.put("token", token);
            responseBody.put("expiresInMinutes",30);
            responseBody.put("tokenType", "Bearer"); // Helpful for frontend

            LOGGER.info("Login successful for user: {} from IP: {}", loginRequest.getUsername(), clientIp);
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            String clientIp = getClientIpAddress(request);
            LOGGER.warn("Login failed for user: {} from IP: {} - {}",
                    loginRequest.getUsername(), clientIp, e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid login credentials");
            errorResponse.put("error", true);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Token is present, log successful logout
            LOGGER.info("User logged out successfully from IP: {}", clientIp);

            return ResponseEntity.ok(Map.of(
                    "message", "Logout successful. Token will expire automatically.",
                    "expiresInMinutes", 15 // Let frontend know when token expires
            ));
        } else {
            // No token provided
            LOGGER.warn("Logout attempt without valid token from IP: {}", clientIp);

            return ResponseEntity.ok(Map.of(
                    "message", "Logout successful."
            ));
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}