package gr.clothesmanager.auth.dto;

import gr.clothesmanager.model.UserRole;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;
    private String username;
    private Set<UserRole> roles;
}
