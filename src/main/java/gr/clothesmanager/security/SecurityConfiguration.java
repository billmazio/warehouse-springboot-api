package gr.clothesmanager.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

   // private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Disable CSRF protection
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/resources/**",
                                "/static/**",
                                "/js/**",
                                "/css/**",
                                "/font-awesome/**",
                                "/fonts/**",
                                "/lineicons/**",
                                "/img/**"
                        ).permitAll()
                        .requestMatchers("/orders/new").hasAnyAuthority("LocalAdmin")
                        .requestMatchers("/materials/new").hasAnyAuthority("SuperAdmin")
                        .requestMatchers("/materials/edit/{id}").hasAnyAuthority("SuperAdmin")
                        .requestMatchers("/materials/viewStore/{id}").hasAnyAuthority("SuperAdmin")
                        .requestMatchers("/materials/materialsByStore").hasAnyAuthority("LocalAdmin")
                        .requestMatchers("**/users/change/**").hasAnyAuthority("SuperAdmin", "LocalAdmin")
                        .requestMatchers("**/users/**").hasAnyAuthority("SuperAdmin")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage("/error"))
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .permitAll())
                .logout(logout -> logout
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .authenticationProvider(authenticationProvider)
              //  .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));  // Apply CORS configuration

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://yourfrontenddomain.com", "http://localhost:4200")); // Define allowed origins for CORS
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH")); // Define allowed methods
        configuration.setAllowedHeaders(List.of("*")); // Allow all headers
        configuration.setAllowCredentials(true); // Allow credentials to be sent
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  // Apply configuration to all routes
        return source;
    }
}
