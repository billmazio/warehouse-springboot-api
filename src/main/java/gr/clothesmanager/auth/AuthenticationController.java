package gr.clothesmanager.auth;


import gr.clothesmanager.auth.dto.AuthenticationRequest;
import gr.clothesmanager.auth.dto.AuthenticationResponse;
import gr.clothesmanager.auth.dto.LoginRequest;
import gr.clothesmanager.core.CustomUserDetailsService;
import gr.clothesmanager.interfaces.UserService;
import gr.clothesmanager.security.JwtService;
import gr.clothesmanager.security.TokenBlacklistService;
import gr.clothesmanager.service.exceptions.UserNotAuthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authenticationService;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String expiredToken) {
        try {
            // Validate the expired token
            if (!jwtService.isTokenExpired(expiredToken)) {
                throw new IllegalArgumentException("Token is not expired");
            }

            // Extract user ID from the expired token
            String userId = jwtService.extractId(expiredToken);

            // Generate a new token
            String newToken = jwtService.generateToken(userId);

            return ResponseEntity.ok(newToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Invalid or expired token");
        }
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            String token = authenticationService.authenticateAndGenerateToken(loginRequest);

            // Set token as HttpOnly cookie
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // Use true in production (only with HTTPS)
            cookie.setPath("/");
            cookie.setMaxAge(15 * 60); // Token valid for 15 minutes
            response.addCookie(cookie);

            // For testing, include token in response body
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Login successful");
            responseBody.put("token", token); // Include the token for frontend debugging (remove in production)

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login credentials");
        }
    }



    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            tokenBlacklistService.revokeToken(jwt);
        }
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

