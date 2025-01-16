package gr.clothesmanager.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String jwt = extractJwt(request);

            if (jwt == null || tokenBlacklistService.isTokenRevoked(jwt)) {
                filterChain.doFilter(request, response);
                return;
            }

            validateAndAuthenticateToken(jwt, request);

        } catch (ExpiredJwtException e) {
            LOGGER.warn("JWT expired: {}", e.getMessage());
            setUnauthorizedResponse(response, "Token has expired.");
        } catch (MalformedJwtException e) {
            LOGGER.error("Malformed JWT: {}", e.getMessage());
            setUnauthorizedResponse(response, "Malformed token.");
        } catch (Exception e) {
            LOGGER.error("Unexpected error during JWT processing: {}", e.getMessage(), e);
            setUnauthorizedResponse(response, "Authentication failed.");
        }

        filterChain.doFilter(request, response);
    }

    private void validateAndAuthenticateToken(String jwt, HttpServletRequest request) {
        String userId = jwtService.extractId(jwt);

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

            if (jwtService.isTokenValid(jwt, userId)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                LOGGER.warn("Invalid token for userId: {}", userId);
            }
        }
    }


    private String extractJwt(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            LOGGER.warn("Authorization header missing or invalid: {}", authHeader);
            return null;
        }
        return authHeader.substring(7);
    }

    private void setUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
