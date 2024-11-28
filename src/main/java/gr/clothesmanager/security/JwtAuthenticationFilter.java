package gr.clothesmanager.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

    private final JwtService jwtService;  // Service to validate and extract info from the JWT
    private final UserDetailsService userDetailsService;  // Service to load user details from the database

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Extract Authorization header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userId;
        final String userRole;

        // If the Authorization header is absent or doesn't start with 'Bearer ', pass the request along
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);  // Extract token by removing "Bearer "

        try {
            // Extract user ID and role from the token
            userId = jwtService.extractId(jwt);  // Custom method to extract user ID
            userRole = jwtService.getStringClaim(jwt, "role");  // Custom method to extract role from token

            // If userId is extracted and no authentication is set in the SecurityContext
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load user details
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userId);

                // If the JWT is valid for this user
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Create authentication token and set it to the security context
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()  // Pass the user authorities/roles to the token
                    );

                    // Set the request details
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Set the authentication context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // If an error occurs, proceed with the filter chain without setting authentication
            filterChain.doFilter(request, response);
            return;
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}
